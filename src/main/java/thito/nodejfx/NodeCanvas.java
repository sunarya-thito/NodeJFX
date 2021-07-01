package thito.nodejfx;

import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import thito.nodejfx.event.*;

import java.util.*;
import java.util.concurrent.*;

public class NodeCanvas extends Pane {

    private NodeLinkContainer linkContainer;
    private NodeContainer nodeContainer = new NodeContainer(this);
    private NodeGroupContainer groupContainer = new NodeGroupContainer();
    private NodeGroupHighlightContainer groupHighlightContainer = new NodeGroupHighlightContainer();
    private ObservableList<Node> nodes = FXCollections.observableArrayList();
    private ObjectProperty<NodeLinkStyle> style = new SimpleObjectProperty<>(NodeLinkStyle.BEZIER_STYLE);
    private ObservableSet<NodeCanvasElement> selectedNodes = FXCollections.observableSet(ConcurrentHashMap.newKeySet());
    private ObservableList<NodeGroup> groups = FXCollections.observableArrayList();
    private BooleanProperty snapToGrid = new SimpleBooleanProperty(false);
    private NodeSelectionContainer selectionContainer = new NodeSelectionContainer(this);
    private NodeViewport viewport;
    private NodeGroup draggingGroup;

    public NodeCanvas() {
        setManaged(false);
        linkContainer = new NodeLinkContainer(this);

        setCache(true);
        setCacheShape(true);
        setCacheHint(CacheHint.SPEED);

        groups.addListener((ListChangeListener<NodeGroup>) c -> {
            while (c.next()) {
                for (NodeGroup group : new ArrayList<>(c.getAddedSubList())) {
                    if (group.getParent() != null) continue;
                    group.initialize(this);
                    groupContainer.getChildren().add(group);
                    group.updateGroups();
                    groupHighlightContainer.getChildren().add(group.getHighlight());
                }
                NodeContext.iterateLater(c.getRemoved(), group -> {
                    selectedNodes.remove(group);
                    group.destroy(this);
                    groupContainer.getChildren().remove(group);
                    group.getElements().clear();
                    groupHighlightContainer.getChildren().remove(group.getHighlight());
                });
            }
        });
        Bindings.bindContent(nodeContainer.getNodes(), nodes);
        getChildren().addAll(groupHighlightContainer, linkContainer, nodeContainer, groupContainer, selectionContainer);
        style.addListener((obs, oldValue, newValue) -> {
            for (NodeLink link : linkContainer.getLinks()) {
                link.setStyle(newValue);
            }
        });

        selectedNodes.addListener((SetChangeListener<NodeCanvasElement>) change -> {
            if (change.wasRemoved()) {
                if (change.getElementRemoved().isSelected()) {
                    change.getElementRemoved().setSelected(false);
                }
            }
            if (change.wasAdded()) {
                if (!change.getElementAdded().isSelected()) {
                    change.getElementAdded().setSelected(true);
                }
            }
        });

        addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
            for (NodeLink link : getLinkContainer().getLinks()) {
                if (link instanceof NodeLinked) {
                    javafx.scene.Node node = ((NodeLinked) link).getLinkingElement().getComponent();
                    Point2D point = node.sceneToLocal(event.getSceneX(), event.getSceneY());
                    if (node.contains(point.getX(), point.getY())) {
                        ((NodeLinked) link).setHover(true);
                    } else {
                        ((NodeLinked) link).setHover(false);
                    }
                }
            }
        });
    }

    public NodeGroup getDraggingGroup() {
        return draggingGroup;
    }

    public void setDraggingGroup(NodeGroup draggingGroup) {
        this.draggingGroup = draggingGroup;
    }

    public NodeSelectionContainer getSelectionContainer() {
        return selectionContainer;
    }

    public NodeViewport getViewport() {
        return viewport;
    }

    protected void setViewport(NodeViewport viewport) {
        this.viewport = viewport;
    }

    public BooleanProperty snapToGridProperty() {
        return snapToGrid;
    }

    public NodeLinkContainer getLinkContainer() {
        return linkContainer;
    }

    public NodeContainer getNodeContainer() {
        return nodeContainer;
    }

    public NodeGroupHighlightContainer getGroupHighlightContainer() {
        return groupHighlightContainer;
    }

    public ObservableList<NodeGroup> getGroups() {
        return groups;
    }

    public ObservableSet<NodeCanvasElement> getSelectedNodes() {
        return selectedNodes;
    }

    public List<Node> getRoots() {
        List<Node> roots = new ArrayList<>();
        for (Node node : nodes) {
            boolean foundInput = false;
            for (NodeParameter param : node.getParameters()) {
                if (!param.inputLinks().isEmpty()) {
                    foundInput = true;
                    break;
                }
            }
            if (!foundInput) {
                roots.add(node);
            }
        }
        return roots;
    }

    public ObjectProperty<NodeLinkStyle> nodeLinkStyleProperty() {
        return style;
    }

    public NodeDragContext getDragContext() {
        return linkContainer.getContext();
    }

    public ObservableList<Node> getNodes() {
        return nodes;
    }

    void prepare(Node node) {
        for (NodeParameter parameter : node.getParameters()) {
            prepare(node, parameter);
        }
    }

    public NodeLinked connect(NodeParameter source, NodeParameter target) {
        if (!checkAssignable(source, target)) return null;
        NodeLinked linked = link(source, target, false);
        if (linked != null) {
            source.outputLinks().add(target);
            target.inputLinks().add(source);
        }
        return linked;
    }

    public NodeLinked forceConnect(NodeParameter source, NodeParameter target) {
        NodeLinked linked = new NodeLinked(linkContainer, style.get(), source, target);
        linkContainer.addLink(linked);
        source.outputLinks().add(target);
        target.inputLinks().add(source);
        return linked;
    }

    public NodeLinked disconnect(NodeParameter source, NodeParameter target) {
        NodeLinked linked = find(source, target);
        if (linked != null) {
            NodeLinkEvent event = new NodeLinkEvent(NodeLinkEvent.NODE_UNLINKED_EVENT, null, linked, source, target);
            source.fireEvent(event);
            if (event.isConsumed()) return null;
            target.fireEvent(event);
            if (event.isConsumed()) return null;
            linkContainer.removeLink(linked);
            source.outputLinks().remove(target);
            target.inputLinks().remove(source);
            return linked;
        }
        return null;
    }

    private boolean handleNodeRemoval = true;

    public boolean isHandleNodeRemoval() {
        return handleNodeRemoval;
    }

    public void setHandleNodeRemoval(boolean handleNodeRemoval) {
        this.handleNodeRemoval = handleNodeRemoval;
    }

    void destroy(Node node) {
        if (!handleNodeRemoval) return;
        for (NodeParameter parameter : node.getParameters()) {
            destroy(node, parameter);
        }
    }

    void destroy(Node node, NodeParameter parameter) {
        for (NodeParameter linked : new ArrayList<>(parameter.inputLinks())) {
            disconnect(linked, parameter);
        }
        for (NodeParameter linked : new ArrayList<>(parameter.outputLinks())) {
            disconnect(parameter, linked);
        }
    }

    boolean checkAssignable(NodeParameter source, NodeParameter target) {
        if (source == target) return false;
        NodeLinked existing = find(source, target);
        if (existing != null) return false;
        if (!source.getAllowOutput().get() || !target.getAllowInput().get()) {
            return false;
        }
        if (!target.isAssignableFrom(source)) {
            return false;
        }
        if (!target.getInputType().get().isAssignableFrom(source.getOutputType().get())) {
            return false;
        }
        if (!source.getOutputType().get().isAssignableFrom(target.getInputType().get())) {
            return false;
        }
        if (!source.getMultipleOutputAssigner().get()&& !source.outputLinks().isEmpty()) {
            return false;
        }
        if (!target.getMultipleInputAssigner().get() && !target.inputLinks().isEmpty()) {
            return false;
        }
        return true;
    }

    NodeLinked link(NodeParameter source, NodeParameter target, boolean force) {
        NodeLinked linked = new NodeLinked(linkContainer, style.get(), source, target);
        if (!force) {
            NodeLinkEvent event = new NodeLinkEvent(NodeLinkEvent.NODE_LINKED_EVENT, null, linked, source, target);
            source.fireEvent(event);
            if (event.isConsumed()) {
                linked.destroy(linkContainer);
                return null;
            }
            target.fireEvent(event);
            if (event.isConsumed()) {
                linked.destroy(linkContainer);
                return null;
            }
            fireEvent(event);
            if (event.isConsumed()) {
                linked.destroy(linkContainer);
                return null;
            }
        }
        linkContainer.addLink(linked);
        return linked;
    }

    protected NodeLinked find(NodeParameter source, NodeParameter target) {
        for (NodeLink link : linkContainer.getLinks()) {
            if (link instanceof NodeLinked && ((NodeLinked) link).getSource() == source && ((NodeLinked) link).getTarget() == target) {
                return (NodeLinked) link;
            }
        }
        return null;
    }

    void prepare(Node node, NodeParameter parameter) {
        for (NodeParameter linked : parameter.inputLinks()) {
            link(linked, parameter, true);
        }
        for (NodeParameter linked : parameter.outputLinks()) {
            link(parameter, linked, true);
        }
    }
}
