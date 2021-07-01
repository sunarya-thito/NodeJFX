package thito.nodejfx;

import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

public class NodeLinkContainer extends Pane {

    private NodeDragContext context;
    private List<NodeLink> links = new ArrayList<>();
    private NodeCanvas canvas;

    public NodeLinkContainer(NodeCanvas canvas) {
        setManaged(false);
        this.canvas = canvas;
        setPickOnBounds(false);
        context = new NodeDragContext(this);
    }

    public NodeCanvas getCanvas() {
        return canvas;
    }

    protected NodeParameter findByPosition(double mouseX, double mouseY) {
        for (javafx.scene.Node node : canvas.getChildren()) {
            if (node instanceof NodeContainer) {
                for (javafx.scene.Node nx : ((NodeContainer) node).getChildren()) {
                    if (nx instanceof Node) {
                        for (NodeParameter parameter : ((Node) nx).getParameters()) {
                            NodeParameter found = parameter.findByPosition(mouseX, mouseY);
                            if (found != null) {
                                return found;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public NodeDragContext getContext() {
        return context;
    }

    public void addLink(NodeLink link) {
        link.initialize(this);
        links.add(link);
    }

    public void removeLink(NodeLink link) {
        link.destroy(this);
        links.remove(link);
    }

    public List<NodeLink> getLinks() {
        return links;
    }
}
