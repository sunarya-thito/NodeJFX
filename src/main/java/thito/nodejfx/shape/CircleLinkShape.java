package thito.nodejfx.shape;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import thito.nodejfx.NodeLinkShape;
import thito.nodejfx.NodeParameter;

public class CircleLinkShape implements NodeLinkShape {
    @Override
    public CircleLinkShapeHandler createNewHandler(NodeParameter parameter, boolean input) {
        return new CircleLinkShapeHandler();
    }

    public class CircleLinkShapeHandler extends Circle implements NodeLinkShapeHandler {

        public CircleLinkShapeHandler() {
            super(5);
        }

        @Override
        public NodeLinkShape getType() {
            return CircleLinkShape.this;
        }

        @Override
        public Node getComponent() {
            return this;
        }

        @Override
        public void setColor(Paint color) {
            setFill(color);
        }

        @Override
        public Paint getColor() {
            return getFill();
        }

        public CircleLinkShapeHandler color(Paint color) {
            setColor(color);
            return this;
        }

        @Override
        public NodeLinkShapeHandler cloneDummyShape() {
            return new CircleLinkShapeHandler().color(getFill());
        }
    }
}
