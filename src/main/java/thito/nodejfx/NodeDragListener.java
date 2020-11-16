package thito.nodejfx;

import javafx.event.Event;
import javafx.geometry.Point2D;

public class NodeDragListener {
    private NodeParameter parameter;
    private boolean input;
    public NodeDragListener(NodeParameter parameter, boolean input, javafx.scene.Node node) {
        this.parameter = parameter;
        this.input = input;

        node.setOnDragDetected(Event::consume);

        node.setOnMousePressed(event -> {
            NodeDragContext context = getContext();
            if (event.isPrimaryButtonDown() && context != null) {
                if (event.isShiftDown() || (input ? parameter.inputLinks().isEmpty() : parameter.outputLinks().isEmpty())) {
                    context.startDragging(this, event.getSceneX(), event.getSceneY());
                } else {
                    context.startReallocating(this, event.getSceneX(), event.getSceneY());
                }
            }
            event.consume();
        });

        node.setOnMouseDragged(event -> {
            NodeDragContext context = getContext();
            if (event.isPrimaryButtonDown() && context != null) {
                for (NodeLinking linking : context.getNodeLinking()) {
                    double x = event.getSceneX();
                    double y = event.getSceneY();
                    Point2D point = context.getContainer().sceneToLocal(x, y);
                    x = point.getX();
                    y = point.getY();
                    linking.getEndX().set(x);
                    linking.getEndY().set(y);
                }
            }
            event.consume();
        });

        node.setOnMouseReleased(event -> {
            NodeDragContext context = getContext();
            if (context != null) {
                context.stopDragging(event.getSceneX(), event.getSceneY());
            }
            event.consume();
        });

    }

    public NodeDragContext getContext() {
        NodeCanvas canvas = parameter.getCanvas();
        return canvas == null ? null : canvas.getDragContext();
    }

    public NodeParameter getParameter() {
        return parameter;
    }

    public boolean isInput() {
        return input;
    }
}
