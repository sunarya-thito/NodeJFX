package thito.nodejfx;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.*;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import thito.nodejfx.event.NodeLinkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class NodeCanvas extends Pane {

    private NodeLinkContainer linkContainer;
    private NodeContainer nodeContainer = new NodeContainer();
    private NodeGroupContainer groupContainer = new NodeGroupContainer();
    private NodeGroupHighlightContainer groupHighlightContainer = new NodeGroupHighlightContainer();
    private ObservableList<Node> nodes = FXCollections.observableArrayList();
    private ObjectProperty<NodeLinkStyle> style = new SimpleObjectProperty<>(NodeLinkStyle.BEZIER_STYLE);
    private ObservableSet<NodeCanvasElement> selectedNodes = FXCollections.observableSet(ConcurrentHashMap.newKeySet());
    private ObservableList<NodeGroup> groups = FXCollections.observableArrayList();

    public NodeCanvas() {
        setManaged(false);
        linkContainer = new NodeLinkContainer(this);
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
        nodes.addListener((ListChangeListener<Node>) c -> {
            while (c.next()) {
                for (Node node : new ArrayList<>(c.getAddedSubList())) {
                    node.initialize(this);
                    prepare(node);
                    nodeContainer.getChildren().add(node);
                    node.updateGroups();
                }
                NodeContext.iterateLater(c.getRemoved(), node -> {
                    destroy(node);
                    selectedNodes.remove(node);
                    node.getGroups().clear();
                    nodeContainer.getChildren().remove(node);
                });
            }
        });
        getChildren().addAll(groupHighlightContainer, linkContainer, nodeContainer, groupContainer);
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

    public boolean connect(NodeParameter source, NodeParameter target) {
        if (!checkAssignable(source, target)) return false;
        link(source, target, false);
        source.outputLinks().add(target);
        target.inputLinks().add(source);
        return true;
    }

    public boolean disconnect(NodeParameter source, NodeParameter target) {
        NodeLinked linked = find(source, target);
        if (linked != null) {
            NodeLinkEvent event = new NodeLinkEvent(NodeLinkEvent.NODE_UNLINKED_EVENT, null, linked, source, target);
            source.fireEvent(event);
            if (event.isConsumed()) return false;
            target.fireEvent(event);
            if (event.isConsumed()) return false;
            linkContainer.removeLink(linked);
            source.outputLinks().remove(target);
            target.inputLinks().remove(source);
            return true;
        }
        return false;
    }

    void destroy(Node node) {
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
        if (!target.getInputType().get().isAssignableFrom(source.getOutputType().get())) {
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

    void link(NodeParameter source, NodeParameter target, boolean force) {
        if (!force) {
            NodeLinkEvent event = new NodeLinkEvent(NodeLinkEvent.NODE_LINKED_EVENT, null, null, source, target);
            source.fireEvent(event);
            if (event.isConsumed()) return;
            target.fireEvent(event);
            if (event.isConsumed()) return;
        }
        linkContainer.addLink(new NodeLinked(linkContainer, style.get(), source, target));
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
