package thito.nodejfx;

import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

public class NodeLinkContainer extends Pane {

    private NodeDragContext context;
    private List<NodeLink> links = new ArrayList<>();
    private NodeCanvas canvas;

    public NodeLinkContainer(NodeCanvas canvas) {
        this.canvas = canvas;
        setPickOnBounds(false);
        context = new NodeDragContext(this);
    }

    public NodeCanvas getCanvas() {
        return canvas;
    }

    protected NodeParameter findByPosition(double mouseX, double mouseY) {
        for (Node node : canvas.getNodes()) {
            for (NodeParameter parameter : node.getParameters()) {
                if (parameter.isInBounds(parameter.sceneToLocal(mouseX, mouseY))) {
                    return parameter;
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
