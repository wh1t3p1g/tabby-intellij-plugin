/**
 * Copied and adapted from plugin
 * <a href="https://github.com/neueda/jetbrains-plugin-graph-database-support">Graph Database Support</a>
 * by Neueda Technologies, Ltd.
 * Modified by Alberto Venturini, 2022
 */
package com.albertoventurini.graphdbplugin.jetbrains.actions.execute;

import com.intellij.util.messages.Topic;

public interface CancelQueryEvent {

    Topic<CancelQueryEvent> CANCEL_QUERY_TOPIC = Topic.create("GraphDatabase.CancelQueryTopic", CancelQueryEvent.class);

    void cancelQuery();
}
