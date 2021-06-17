package thito.nodejfx;

import javafx.event.Event;
import javafx.geometry.*;

public class NodeDragListener {
    private NodeParameter parameter;
    private boolean input;
    private boolean move;
    public NodeDragListener(NodeParameter parameter, boolean input, javafx.scene.Node node) {
        this.parameter = parameter;
        this.input = input;

        node.setOnDragDetected(Event::consume);

        node.setOnMousePressed(event -> {
            NodeDragContext context = getContext();
            if (event.isPrimaryButtonDown() && context != null) {
                NodeViewport viewport = context.getContainer().getCanvas().getViewport();
                if (viewport != null) {
                    viewport.getAnimationRequested().add(this);
                }
                if (input ? parameter.inputLinks().isEmpty() : parameter.outputLinks().isEmpty()) {
                    context.startDragging(this, event.getSceneX(), event.getSceneY());
                } else if (event.isShiftDown() && (input ? parameter.getMultipleInputAssigner().get() : parameter.getMultipleOutputAssigner().get())) {
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
                NodeViewport viewport = context.getContainer().getCanvas().getViewport();
                if (viewport != null) {
                    Point2D point = viewport.sceneToLocal(event.getSceneX(), event.getSceneY());
                    viewport.xOverflowProperty().set(point.getX());
                    viewport.yOverflowProperty().set(point.getY());
                }
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
                NodeViewport viewport = context.getContainer().getCanvas().getViewport();
                if (viewport != null) {
                    viewport.getAnimationRequested().remove(this);
                }
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
