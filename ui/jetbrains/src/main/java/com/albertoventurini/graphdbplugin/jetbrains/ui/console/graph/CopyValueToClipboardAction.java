package com.albertoventurini.graphdbplugin.jetbrains.ui.console.graph;

import com.albertoventurini.graphdbplugin.database.api.data.GraphNode;
import com.albertoventurini.graphdbplugin.jetbrains.ui.helpers.SerialisationHelper;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.List;

/**
 * @author wh1t3p1g
 * @project tabby-intellij-plugin
 * @since 2024/1/9
 */
public class CopyValueToClipboardAction extends AnAction {

    private GraphNode node;
    private String key;
    public CopyValueToClipboardAction(String title, String description, Icon icon, GraphNode object, String key) {
        super(title, description, icon);
        this.node = object;
        this.key = key;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        List<String> types = node.getTypes();
        boolean isMethodNode = types.contains("Method");
        String data = null;

        switch (key){
            case "CLASSNAME":
                data = isMethodNode ?
                        (String) node.getPropertyContainer().getProperties().get("CLASSNAME"):
                        (String) node.getPropertyContainer().getProperties().get("NAME");
                break;
            case "METHOD":
                data = isMethodNode ?
                        (String) node.getPropertyContainer().getProperties().get("NAME"):
                        "";
                break;
            case "NAME":
                data = isMethodNode ?
                        (String) node.getPropertyContainer().getProperties().get("NAME0"):
                        (String) node.getPropertyContainer().getProperties().get("NAME");
                break;
            default:
                data = SerialisationHelper.convertToCsv(node);
        }

        StringSelection selection = new StringSelection(data);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }
}
