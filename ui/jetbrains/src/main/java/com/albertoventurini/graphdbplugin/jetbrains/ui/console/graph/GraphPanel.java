/**
 * Copied and adapted from plugin
 * <a href="https://github.com/neueda/jetbrains-plugin-graph-database-support">Graph Database Support</a>
 * by Neueda Technologies, Ltd.
 * Modified by Alberto Venturini, 2022
 */
package com.albertoventurini.graphdbplugin.jetbrains.ui.console.graph;

import com.albertoventurini.graphdbplugin.database.api.data.GraphEntity;
import com.albertoventurini.graphdbplugin.database.api.data.GraphNode;
import com.albertoventurini.graphdbplugin.database.api.data.GraphRelationship;
import com.albertoventurini.graphdbplugin.database.api.query.GraphQueryResult;
import com.albertoventurini.graphdbplugin.jetbrains.actions.execute.ExecuteQueryPayload;
import com.albertoventurini.graphdbplugin.jetbrains.component.datasource.state.DataSourceApi;
import com.albertoventurini.graphdbplugin.jetbrains.ui.console.GraphConsoleView;
import com.albertoventurini.graphdbplugin.jetbrains.ui.console.event.PluginSettingsUpdated;
import com.albertoventurini.graphdbplugin.jetbrains.ui.console.event.QueryExecutionProcessEvent;
import com.albertoventurini.graphdbplugin.jetbrains.ui.datasource.tree.TreeMouseAdapter;
import com.albertoventurini.graphdbplugin.jetbrains.ui.helpers.UiHelper;
import com.albertoventurini.graphdbplugin.jetbrains.ui.renderes.tree.PropertyTreeCellRenderer;
import com.albertoventurini.graphdbplugin.jetbrains.util.Notifier;
import com.albertoventurini.graphdbplugin.platform.GraphConstants.ToolWindow.Tabs;
import com.albertoventurini.graphdbplugin.visualization.PrefuseVisualization;
import com.albertoventurini.graphdbplugin.visualization.services.LookAndFeelService;
import com.intellij.ide.DataManager;
import com.intellij.ide.SelectInEditorManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.BalloonBuilder;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.ColorUtil;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.labels.LinkLabel;
import com.intellij.ui.components.labels.LinkListener;
import com.intellij.ui.popup.BalloonPopupBuilderImpl;
import com.intellij.ui.treeStructure.PatchedDefaultMutableTreeNode;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.usageView.UsageInfo;
import com.intellij.util.messages.MessageBus;
import org.jetbrains.annotations.NotNull;
import prefuse.visual.VisualItem;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import static com.albertoventurini.graphdbplugin.jetbrains.ui.console.event.OpenTabEvent.OPEN_TAB_TOPIC;
import static com.albertoventurini.graphdbplugin.visualization.util.DisplayUtil.getTooltipText;
import static com.albertoventurini.graphdbplugin.visualization.util.DisplayUtil.getTooltipTitle;

public class GraphPanel {

    private PrefuseVisualization visualization;
    private LookAndFeelService lookAndFeelService;
    private BalloonBuilder balloonPopupBuilder;
    private Balloon balloon;
    private JBLabel balloonLabel = new JBLabel();
    private GraphPanelInteractions interactions;
    private Tree entityDetailsTree;
    private DefaultTreeModel entityDetailsTreeModel;
    private DataSourceApi dataSource;
    private Project project;

    public GraphPanel() {
        entityDetailsTreeModel = new DefaultTreeModel(null);
    }

    public void initialize(@NotNull final GraphConsoleView graphConsoleView, @NotNull final Project project) {
        MessageBus messageBus = project.getMessageBus();
        this.lookAndFeelService = graphConsoleView.getLookAndFeelService();
        this.entityDetailsTree = graphConsoleView.getEntityDetailsTree();
        entityDetailsTree.addMouseListener(new TreeMouseAdapter());

        // Bootstrap visualisation
        visualization = new PrefuseVisualization(lookAndFeelService);
        graphConsoleView.getGraphCanvas().add(visualization.getCanvas());

        // Entity data table
        entityDetailsTree.setCellRenderer(new PropertyTreeCellRenderer());
        entityDetailsTree.setModel(entityDetailsTreeModel);
        messageBus.connect().subscribe(QueryExecutionProcessEvent.QUERY_EXECUTION_PROCESS_TOPIC, new QueryExecutionProcessEvent() {
            @Override
            public void executionStarted(DataSourceApi dataSource, ExecuteQueryPayload payload) {
                GraphPanel.this.dataSource = dataSource;
                entityDetailsTreeModel.setRoot(null);
            }

            @Override
            public void resultReceived(ExecuteQueryPayload payload, GraphQueryResult result) {
                if (result.getNodes().isEmpty() && !result.getRows().isEmpty()) {
                    LinkListener<?> openTableTab = (l, s) -> messageBus.syncPublisher(OPEN_TAB_TOPIC).openTab(Tabs.TABLE);
                    LinkLabel<?> link = new LinkLabel<>("Nothing to display in Graph. Click to view results as Table.", null, openTableTab);
                    entityDetailsTreeModel.setRoot(new PatchedDefaultMutableTreeNode(link));
                } else if (result.getNodes().isEmpty()) {
                    entityDetailsTreeModel.setRoot(new PatchedDefaultMutableTreeNode("Query returned no results."));
                } else {
                    entityDetailsTreeModel.setRoot(new PatchedDefaultMutableTreeNode("Select an item in the graph to view details..."));
                }
            }

            @Override
            public void postResultReceived(ExecuteQueryPayload payload) {
            }

            @Override
            public void handleError(ExecuteQueryPayload payload, Exception exception) {
            }

            @Override
            public void executionCompleted(ExecuteQueryPayload payload) {
            }
        });

        messageBus.connect().subscribe(PluginSettingsUpdated.TOPIC, visualization::updateSettings);

        // Tooltips
        balloonBuilder();

        // Interactions
        this.interactions = new GraphPanelInteractions(
                project,
                graphConsoleView,
                messageBus,
                visualization);
    }

    public void nodeAction(GraphNode node, VisualItem item, MouseEvent e){
        if(e.getButton() == MouseEvent.BUTTON3){ // 右键
            DataContext dataContext = DataManager.getInstance().getDataContext(e.getComponent());
            ListPopup popup = JBPopupFactory.getInstance().createActionGroupPopup(
                    node.getRepresentation(),
                    new EntityActionGroup(dataSource, node),
                    dataContext,
                    JBPopupFactory.ActionSelectionAid.SPEEDSEARCH,
                    true
            );

            popup.showInBestPositionFor(dataContext);
        }
    }

    public void edgeAction(GraphRelationship relationship, VisualItem item, MouseEvent e){
        if(e.getButton() == MouseEvent.BUTTON3){ // 右键
            DataContext dataContext = DataManager.getInstance().getDataContext(e.getComponent());
            ListPopup popup = JBPopupFactory.getInstance().createActionGroupPopup(
                    relationship.getRepresentation(),
                    new EntityActionGroup(dataSource, relationship),
                    dataContext,
                    JBPopupFactory.ActionSelectionAid.SPEEDSEARCH,
                    true
            );

            popup.showInBestPositionFor(dataContext);
        }
    }

    public void navigateToMethod(GraphNode node, VisualItem item, MouseEvent e){
        boolean isDoubleClick = e.getClickCount() == 2;
        List<String> types = node.getTypes();
        if(isDoubleClick && this.project != null && types.contains("Method")){
            String classname = (String) node.getPropertyContainer().getProperties().get("CLASSNAME");
            if(classname.endsWith("_jsp")){
                navigateToFile(node, item, e);
            }else{
                PsiMethod method = NavigateFactory.getMethod(this.project, node);
                if(method != null){
                    PsiIdentifier psiIdentifier = method.getNameIdentifier();
                    if(psiIdentifier != null){
                        UsageInfo usage = new UsageInfo(psiIdentifier);
                        try{
                            SelectInEditorManager.getInstance(this.project)
                                    .selectInEditor(usage.getVirtualFile(),
                                            usage.getSegment().getStartOffset(),
                                            usage.getSegment().getEndOffset(), true, false);
                        }catch (Exception ee){
                            ee.printStackTrace();
                        }

                    }
                }else{
                    Notifier.error("Navigate", "file or method not found on this project.");
                }
            }
        }
    }

    public void navigateToFile(GraphNode node, VisualItem item, MouseEvent e){
        boolean isDoubleClick = e.getClickCount() == 2;
        List<String> types = node.getTypes();
        if(isDoubleClick && this.project != null){

            String classname = null;
            if(types.contains("Class")){
                classname = (String) node.getPropertyContainer().getProperties().get("NAME");
            }else if(types.contains("Method")){
                classname = (String) node.getPropertyContainer().getProperties().get("CLASSNAME");
            }

            if(classname != null){
                if(classname.endsWith("_jsp")){ // jump to jsp files
                    String filename = classname.substring(classname.lastIndexOf(".")+1).replace("_jsp", ".jsp");
                    Collection<VirtualFile> virtualFiles = FilenameIndex.getVirtualFilesByName(filename, GlobalSearchScope.allScope(project));
                    String filepath  = classname.replace(".", "/").replace("_jsp", ".jsp");
                    for(VirtualFile virtualFile:virtualFiles){
                        if(virtualFile != null && virtualFile.getCanonicalPath().endsWith(filepath)){
                            OpenFileDescriptor descriptor = new OpenFileDescriptor(project, virtualFile);
                            if(descriptor.canNavigate()){
                                descriptor.navigate(true);
                                break;
                            }
                        }
                    }
                }else{
                    JavaPsiFacade facade = JavaPsiFacade.getInstance(project);
                    PsiClass psiClass = facade.findClass(classname, GlobalSearchScope.allScope(project));
                    if(psiClass != null && psiClass.canNavigate()){
                        psiClass.navigate(true);
                    }
                }
            }
        }
    }

    public void navigateToInvocation(GraphRelationship relationship, VisualItem item, MouseEvent e){
        boolean isDoubleClick = e.getClickCount() == 2;
        List<String> types = relationship.getTypes();
        if(isDoubleClick && this.project != null && types.contains("CALL")){
            String invokerType = (String) relationship.getPropertyContainer().getProperties().get("INVOKER_TYPE");
            GraphNode startNode = relationship.getStartNode();
            GraphNode endNode = relationship.getEndNode();
            if("ManualInvoke".equals(invokerType)){
                navigateToMethod(startNode, item, e);
            }else{
                try{
                    String classname = (String) startNode.getPropertyContainer().getProperties().get("CLASSNAME");
                    if(classname.endsWith("_jsp")){
                        navigateToFile(startNode, item, e);
                    }else{
                        PsiMethod startMethod = NavigateFactory.getMethod(project, startNode);
                        PsiCallExpression callExpression = NavigateFactory.getFirstCallExpression(startMethod, endNode);
                        if(callExpression != null){
                            VirtualFile virtualFile = startMethod.getContainingFile().getVirtualFile();
                            UsageInfo usage = new UsageInfo(callExpression);
                            SelectInEditorManager.getInstance(this.project)
                                    .selectInEditor(virtualFile, usage.getSegment().getStartOffset(), usage.getSegment().getEndOffset(), true, false);
                        }
                    }
                }catch (Exception ee){
                    ee.printStackTrace();
                }
            }
        }
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void showNodeData(GraphNode node, VisualItem item, MouseEvent e) {
        PatchedDefaultMutableTreeNode root = UiHelper.nodeToTreeNode(node.getRepresentation(), node, dataSource);
        entityDetailsTreeModel.setRoot(root);

        Enumeration childs = root.children();
        while (childs.hasMoreElements()) {
            PatchedDefaultMutableTreeNode treeNode
                    = (PatchedDefaultMutableTreeNode) childs.nextElement();
            entityDetailsTree.expandPath(new TreePath(treeNode.getPath()));
        }
    }

    public void showRelationshipData(GraphRelationship relationship, VisualItem item, MouseEvent e) {
        PatchedDefaultMutableTreeNode root = UiHelper.relationshipToTreeNode(
                relationship.getRepresentation(), relationship, dataSource);
        entityDetailsTreeModel.setRoot(root);

        Enumeration childs = root.children();
        while (childs.hasMoreElements()) {
            PatchedDefaultMutableTreeNode treeNode
                    = (PatchedDefaultMutableTreeNode) childs.nextElement();
            entityDetailsTree.expandPath(new TreePath(treeNode.getPath()));
        }
    }

    public void showTooltip(GraphEntity entity, VisualItem item, MouseEvent e) {
        if (balloon != null && !balloon.isDisposed()) {
            balloon.hide();
        }

        balloonPopupBuilder.setTitle(getTooltipTitle(entity));
        balloonLabel.setText(getTooltipText(entity));

        balloon = balloonPopupBuilder.createBalloon();
        Container panel = e.getComponent().getParent();

        final int magicNumber = 15;
        int heightOffset = balloon.getPreferredSize().height / 2 + magicNumber;

        int widthOffset;
        if (e.getX() > panel.getWidth() / 2) {
            widthOffset = balloon.getPreferredSize().width / 2;
        } else {
            widthOffset = panel.getWidth() - balloon.getPreferredSize().width / 2;
        }

        balloon.show(new RelativePoint(panel, new Point(widthOffset, heightOffset)), Balloon.Position.below);
    }

    public void resetPan() {
        visualization.resetPan();
    }

    private void balloonBuilder() {
        final BalloonPopupBuilderImpl builder = new BalloonPopupBuilderImpl(null, balloonLabel);

        final Color bg = lookAndFeelService.getBackgroundColor();
        final Color borderOriginal = lookAndFeelService.getEdgeStrokeColor();
        final Color border = ColorUtil.toAlpha(borderOriginal, 75);
        builder
                .setShowCallout(false)
                .setDialogMode(false)
                .setAnimationCycle(20)
                .setFillColor(bg).setBorderColor(border).setHideOnClickOutside(true)
                .setHideOnKeyOutside(false)
                .setHideOnAction(false)
                .setCloseButtonEnabled(false)
                .setShadow(true);

        balloonPopupBuilder = builder;
    }

    public void hideTooltip(GraphEntity entity, VisualItem visualItem, MouseEvent mouseEvent) {
        balloon.dispose();
    }
}
