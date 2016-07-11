package com.neueda.jetbrains.plugin.graphdb.jetbrains.database;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.util.messages.MessageBus;
import com.neueda.jetbrains.plugin.graphdb.database.api.GraphDatabaseApi;
import com.neueda.jetbrains.plugin.graphdb.database.api.query.GraphQueryResult;
import com.neueda.jetbrains.plugin.graphdb.jetbrains.actions.execute.ExecuteQueryPayload;
import com.neueda.jetbrains.plugin.graphdb.jetbrains.component.analytics.Analytics;
import com.neueda.jetbrains.plugin.graphdb.jetbrains.component.datasource.state.DataSourceApi;
import com.neueda.jetbrains.plugin.graphdb.jetbrains.ui.console.event.QueryExecutionProcessEvent;
import com.neueda.jetbrains.plugin.graphdb.jetbrains.ui.util.Notifier;

import java.util.concurrent.Future;

public class QueryExecutionService {

    private final DatabaseManagerService databaseManager;
    private final MessageBus messageBus;
    private Future<?> runningQuery;

    public QueryExecutionService(MessageBus messageBus) {
        this.messageBus = messageBus;
        this.databaseManager = ServiceManager.getService(DatabaseManagerService.class);
    }

    public void executeQuery(DataSourceApi dataSource, ExecuteQueryPayload payload) {
        checkForRunningQuery();

        try {
            executeInBackground(dataSource, payload);
        } catch (Exception e) {
            Notifier.error("Query execution", "Error during execution: " + e.toString());
        } finally {
            Analytics.event(dataSource, "executeQuery");
        }
    }

    private synchronized void checkForRunningQuery() {
        if (runningQuery == null) {
            return;
        }

        if (runningQuery.isDone()) {
            runningQuery = null;
        } else {
            runningQuery.cancel(true);
        }
    }

    private synchronized void executeInBackground(DataSourceApi dataSource, ExecuteQueryPayload payload) {
        QueryExecutionProcessEvent event = messageBus.syncPublisher(QueryExecutionProcessEvent.QUERY_EXECUTION_PROCESS_TOPIC);
        event.executionStarted(payload);

        runningQuery = ApplicationManager.getApplication().executeOnPooledThread(() -> {
            try {
                GraphDatabaseApi database = databaseManager.getDatabaseFor(dataSource);
                GraphQueryResult result = database.execute(payload.getContent());

                ApplicationManager.getApplication().invokeLater(() -> {
                    event.resultReceived(result);
                    event.postResultReceived();
                    event.executionCompleted();
                });
            } catch (Exception e) {
                ApplicationManager.getApplication().invokeLater(() -> {
                    event.handleError(e);
                    event.executionCompleted();
                });
            }
        });
    }
}
