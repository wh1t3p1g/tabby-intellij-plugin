package com.neueda.jetbrains.plugin.graphdb.visualization;

import com.neueda.jetbrains.plugin.graphdb.database.api.GraphNode;
import com.neueda.jetbrains.plugin.graphdb.database.api.GraphRelationship;
import com.neueda.jetbrains.plugin.graphdb.visualization.decorators.CenteredLayout;
import com.neueda.jetbrains.plugin.graphdb.visualization.events.EventType;
import com.neueda.jetbrains.plugin.graphdb.visualization.events.NodeCallback;
import com.neueda.jetbrains.plugin.graphdb.visualization.events.RelationshipCallback;
import com.neueda.jetbrains.plugin.graphdb.visualization.listeners.NodeListener;
import com.neueda.jetbrains.plugin.graphdb.visualization.listeners.RelationshipListener;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.Activity;
import prefuse.controls.DragControl;
import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Schema;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.render.ShapeRenderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.util.PrefuseLib;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static prefuse.Constants.EDGE_TYPE_LINE;
import static prefuse.Constants.SHAPE_ELLIPSE;

public class GraphDisplay extends Display {

    private static final String GRAPH = "graph";
    private static final String NODES = "graph.nodes";
    private static final String EDGES = "graph.edges";
    private static final boolean DIRECTED = true;
    private static final int NODE_DIAMETER = 25;
    private static final String NODE_LABEL = "nodelabel";
    private static final String LAYOUT = "layout";

    private static final int FONT_SIZE = 10;
    private static final int FONT_COLOR = ColorLib.rgb(15, 15, 45);

    private Graph graph;

    private Map<String, Node> nodeMap = new HashMap<>();
    private Map<String, GraphNode> graphNodeMap = new HashMap<>();
    private Map<String, GraphRelationship> graphRelationshipMap = new HashMap<>();

    public GraphDisplay() {
        super(new Visualization());
        graph = new Graph(DIRECTED);
        graph.addColumn("id", String.class);

        Schema nodeSchema = PrefuseLib.getVisualItemSchema();
        nodeSchema.setDefault(VisualItem.SHAPE, SHAPE_ELLIPSE);

        Schema edgeSchema = PrefuseLib.getVisualItemSchema();

        m_vis.addGraph(GRAPH, graph, null, nodeSchema, edgeSchema);
        m_vis.setInteractive(EDGES, null, false);
        m_vis.setValue(NODES, null, VisualItem.SHAPE, SHAPE_ELLIPSE);

        setupRenderer();
        createLayout();
        setHighQuality(true);
        addControlListener(new DragControl());
    }

    public void clearGraph() {
        graph.clear();
    }

    public void addNodeListener(EventType type, NodeCallback callback) {
        addControlListener(new NodeListener(type, callback, graphNodeMap));
    }

    public void addEdgeListener(EventType type, RelationshipCallback callback) {
        addControlListener(new RelationshipListener(type, callback, graphRelationshipMap));
    }

    public void addNode(GraphNode graphNode) {
        Node node = graph.addNode();
        node.set("id", graphNode.getId());
        nodeMap.put(graphNode.getId(), node);
        graphNodeMap.put(graphNode.getId(), graphNode);
    }

    public void addRelationship(GraphRelationship graphRelationship) {
        String start = graphRelationship.getStart().getId();
        String end = graphRelationship.getEnd().getId();

        Edge edge = graph.addEdge(nodeMap.get(start), nodeMap.get(end));
        edge.set("id", graphRelationship.getId());
        graphRelationshipMap.put(graphRelationship.getId(), graphRelationship);
    }

    private void setupRenderer() {
        ShapeRenderer nodeRenderer = new ShapeRenderer();

        nodeRenderer.setBaseSize(NODE_DIAMETER);
        DefaultRendererFactory rf = new DefaultRendererFactory(nodeRenderer, new EdgeRenderer(EDGE_TYPE_LINE));
        LabelRenderer labelRenderer = new LabelRenderer("id");
        labelRenderer.setMaxTextWidth(NODE_DIAMETER);
        rf.add(new InGroupPredicate(NODE_LABEL), labelRenderer);

        final Schema decoratorSchema = PrefuseLib.getVisualItemSchema();
        decoratorSchema.setDefault(VisualItem.INTERACTIVE, false);
        decoratorSchema.setDefault(VisualItem.TEXTCOLOR, FONT_COLOR);
        Font font = FontLib.getFont(UIManager.getFont("Label.font").getFontName(), FONT_SIZE);
        decoratorSchema.setDefault(VisualItem.FONT, font);
//        decoratorSchema.setDefault(VisualItem.BOUNDS, );

        m_vis.addDecorators(NODE_LABEL, NODES, decoratorSchema);

        m_vis.setRendererFactory(rf);
    }

    private void createLayout() {
        ActionList layout = new ActionList(Activity.INFINITY);
        layout.add(getColors());
        layout.add(new ForceDirectedLayout(GRAPH, true));
        layout.add(new RepaintAction());
        layout.add(new CenteredLayout(NODE_LABEL));

        m_vis.putAction(LAYOUT, layout);
    }

    private ActionList getColors() {
        ActionList colors = new ActionList();

        ColorAction nStroke = new ColorAction(NODES, VisualItem.STROKECOLOR);
        nStroke.setDefaultColor(ColorLib.gray(100));
        nStroke.add("_hover", ColorLib.gray(50));
        colors.add(nStroke);

        ColorAction nFill = new ColorAction(NODES, VisualItem.FILLCOLOR);
        nFill.setDefaultColor(ColorLib.gray(255));
        nFill.add("_hover", ColorLib.gray(200));
        colors.add(nFill);

        ColorAction nEdges = new ColorAction(EDGES, VisualItem.STROKECOLOR);
        nEdges.setDefaultColor(ColorLib.gray(100));
        colors.add(nEdges);

        ColorAction arrow = new ColorAction(EDGES, VisualItem.FILLCOLOR);
        nEdges.setDefaultColor(ColorLib.gray(100));
        colors.add(arrow);

        return colors;
    }

    public void startLayout() {
        m_vis.run(LAYOUT);
    }

    public void stopLayout() {
        m_vis.cancel(LAYOUT);
    }
}
