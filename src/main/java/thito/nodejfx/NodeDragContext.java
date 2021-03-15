package thito.nodejfx;

import thito.nodejfx.event.NodeLinkEvent;

import java.util.ArrayList;
import java.util.List;

public class NodeDragContext {
    private List<NodeLinking> nodeLinking = new ArrayList<>();
    private NodeLinkContainer container;

    public NodeDragContext(NodeLinkContainer container) {
        this.container = container;
    }

    public NodeLinkContainer getContainer() {
        return container;
    }

    public List<NodeLinking> getNodeLinking() {
        return nodeLinking;
    }

    public void stopDragging(double x, double y) {
        NodeParameter param = getContainer().findByPosition(x, y);
        NodeCanvas canvas = getContainer().getCanvas();
        for (NodeLinking linking : nodeLinking) {
            if (param != null) {
                if (linking.isInput()) {
                    canvas.connect(param, linking.getParameter());
                } else {
                    canvas.connect(linking.getParameter(), param);
                }
            } else {
                if (!move) {
                    if (linking.isInput()) {
                        container.getCanvas().fireEvent(new NodeLinkEvent(NodeLinkEvent.NODE_LINK_CANCEL_EVENT, linking, null, null, linking.getParameter()));
                    } else {
                        container.getCanvas().fireEvent(new NodeLinkEvent(NodeLinkEvent.NODE_LINK_CANCEL_EVENT ,linking, null, linking.getParameter(), null));
                    }
                }
            }
            if (!linking.isHold()) {
                container.removeLink(linking);
            }
        }
        nodeLinking.clear();
    }

    private boolean move;

    public void startReallocating(NodeDragListener dragging, double x, double y) {
        NodeParameter parameter = dragging.getParameter();
        NodeCanvas canvas = getContainer().getCanvas();
        List<NodeParameter> linked;
        move = false;
        if (dragging.isInput()) {
            linked = new ArrayList<>(parameter.inputLinks());
            for (NodeParameter opposite : linked) {
                if (startDragging(opposite.getOutputDrag(), x, y)) {
                    move = true;
                    canvas.disconnect(opposite, parameter);
                }
            }
        } else {
            linked = new ArrayList<>(parameter.outputLinks());
            for (NodeParameter opposite : linked) {
                if (startDragging(opposite.getInputDrag(), x, y)) {
                    move = true;
                    canvas.disconnect(parameter, opposite);
                }
            }
        }
    }

    public boolean startDragging(NodeDragListener dragging, double x, double y) {
        NodeLinkEvent event;
        move = false;
        if (dragging.isInput()) {
            event = new NodeLinkEvent(NodeLinkEvent.NODE_LINKING_EVENT, null, null, dragging.getParameter(), null);
        } else {
            event = new NodeLinkEvent(NodeLinkEvent.NODE_LINKING_EVENT, null, null, null, dragging.getParameter());
        }
        dragging.getParameter().fireEvent(event);
        if (event.isConsumed()) return false;
        NodeLinking nodeLinking = new NodeLinking(container, container.getCanvas().nodeLinkStyleProperty().get(),
                dragging.getParameter(), dragging.isInput(), x, y);
        this.nodeLinking.add(nodeLinking);
        container.addLink(nodeLinking);
        return true;
    }

}
