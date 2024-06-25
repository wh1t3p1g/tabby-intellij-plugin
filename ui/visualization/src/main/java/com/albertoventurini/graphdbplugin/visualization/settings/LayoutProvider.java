/**
 * Copied and adapted from plugin
 * <a href="https://github.com/neueda/jetbrains-plugin-graph-database-support">Graph Database Support</a>
 * by Neueda Technologies, Ltd.
 * Modified by Alberto Venturini, 2022
 */
package com.albertoventurini.graphdbplugin.visualization.settings;

import com.albertoventurini.graphdbplugin.visualization.GraphDisplay;
import com.albertoventurini.graphdbplugin.visualization.layouts.CenteredLayout;
import com.albertoventurini.graphdbplugin.visualization.layouts.RepaintAndRepositionAction;
import com.albertoventurini.graphdbplugin.visualization.services.LookAndFeelService;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.layout.graph.NodeLinkTreeLayout;
import prefuse.activity.Activity;

import static com.albertoventurini.graphdbplugin.visualization.constants.GraphGroups.*;

public class LayoutProvider {

    private static final boolean ENFORCE_BOUNDS = false;

    public static ActionList forceLayout(Visualization viz, GraphDisplay display, LookAndFeelService lookAndFeel) {
        ActionList actions = new ActionList(viz);

//        actions.add(new DynamicForceLayout(GRAPH, ENFORCE_BOUNDS));
        actions.add(new NodeLinkTreeLayout(GRAPH));
        actions.add(ColorProvider.colors(lookAndFeel));
        actions.add(new RepaintAndRepositionAction(viz, display));

        return actions;
    }

    public static ActionList repaintLayout(LookAndFeelService lookAndFeelService) {
        ActionList repaint = new ActionList(Activity.INFINITY);
        repaint.add(new CenteredLayout(NODE_LABEL));
        repaint.add(new CenteredLayout(EDGE_LABEL));
        repaint.add(ColorProvider.colors(lookAndFeelService));
        repaint.add(new RepaintAction());

        return repaint;
    }
}
