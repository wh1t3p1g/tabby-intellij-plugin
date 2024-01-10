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
    public CopyValueToClipboardAction(String title, String description, Icon icon, GraphNode object) {
        super(title, description, icon);
        this.node = object;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        List<String> types = node.getTypes();
        String data = null;

        if(types != null){
            if(types.contains("Method")){
                data = (String) node.getPropertyContainer().getProperties().get("NAME0");
            }else if(types.contains("Class")){
                data = (String) node.getPropertyContainer().getProperties().get("NAME");
            }
        }

        if(data == null){
            data = SerialisationHelper.convertToCsv(node);
        }

        StringSelection selection = new StringSelection(data);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }
}
