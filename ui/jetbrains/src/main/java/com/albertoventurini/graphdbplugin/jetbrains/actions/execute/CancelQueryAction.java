/**
 * Copied and adapted from plugin
 * <a href="https://github.com/neueda/jetbrains-plugin-graph-database-support">Graph Database Support</a>
 * by Neueda Technologies, Ltd.
 * Modified by Alberto Venturini, 2022
 */
package com.albertoventurini.graphdbplugin.jetbrains.actions.execute;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.util.messages.MessageBus;

import java.util.Map;

public class CancelQueryAction extends QueryAction {
    public CancelQueryAction() {
    }

    public CancelQueryAction(final PsiElement element) {
        super(element);
    }

    protected void actionPerformed(
            final AnActionEvent e,
            final Project project,
            final Editor editor,
            final String query,
            final Map<String, Object> parameters) {
        MessageBus messageBus = project.getMessageBus();
        CancelQueryEvent cancelQueryEvent = messageBus.syncPublisher(CancelQueryEvent.CANCEL_QUERY_TOPIC);
        cancelQueryEvent.cancelQuery();
    }
}
