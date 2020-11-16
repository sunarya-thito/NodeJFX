package thito.nodejfx;

import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class NodeLink {
    protected NodeLinkContainer container;
    private DoubleProperty
            startX = new SimpleDoubleProperty(),
            startY = new SimpleDoubleProperty(),
            endX = new SimpleDoubleProperty(),
            endY = new SimpleDoubleProperty();
    private NodeLinkShape.NodeLinkShapeHandler
            endShape;
    private NodeLinkStyle.NodeLinkStyleHandler style;
    private InvalidationListener listener;

    public NodeLink(NodeLinkContainer container, NodeLinkStyle style, Point2D start, Point2D end, NodeLinkShape.NodeLinkShapeHandler shape) {
        this.container = container;

        if (shape != null) {
            endShape = shape.cloneDummyShape();
            endShape.getComponent().layoutXProperty().bind(endX);
            endShape.getComponent().layoutYProperty().bind(endY);
        }

        startX.set(start.getX());
        startY.set(start.getY());
        endX.set(end.getX());
        endY.set(end.getY());

        listener = e -> {
            if (
                            endShape != null) {
                endShape.setColor(getLinePaint());
            }
            this.style.update();
        };

        startX.addListener(listener);
        startY.addListener(listener);
        endX.addListener(listener);
        endY.addListener(listener);

    }

    public NodeLinkContainer getContainer() {
        return container;
    }

    protected InvalidationListener getListener() {
        return listener;
    }

    public void initialize(NodeLinkContainer container) {
        destroy(this.container);
        if (
                        endShape != null) {
            container.getChildren().add(
                    endShape.getComponent());
        }
        style.initialize(container);
        listener.invalidated(null);
        updateStyle();
    }

    protected void updateStyle() {
        style.getComponent().setEffect(new DropShadow(3, NodeContext.SHADOW_NODE));
    }

    public void destroy(NodeLinkContainer container) {
        if (
                        endShape != null) {
            container.getChildren().remove(
                    endShape.getComponent());
        }
        style.destroy(container);
    }

    public NodeLinkStyle.NodeLinkStyleHandler getStyle() {
        return style;
    }

    public void setStyle(NodeLinkStyle style) {
        if (this.style != null && container != null) {
            this.style.destroy(container);
        }
        this.style = style.createNewHandler(this);
        if (container != null) {
            this.style.initialize(container);
            this.style.update();
        }
        updateStyle();
    }

    public Paint getLinePaint() {
        return Color.DARKGRAY;
    }

    public DoubleProperty getStartX() {
        return startX;
    }

    public DoubleProperty getEndX() {
        return endX;
    }

    public DoubleProperty getEndY() {
        return endY;
    }

    public DoubleProperty getStartY() {
        return startY;
    }
}
