package thito.nodejfx.shape;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;
import thito.nodejfx.NodeLinkShape;
import thito.nodejfx.NodeParameter;

public class TriangleLinkShape implements NodeLinkShape {
    @Override
    public NodeLinkShapeHandler createNewHandler(NodeParameter parameter, boolean input) {
        return new TriangleLinkShapeHandler(input);
    }

    public class TriangleLinkShapeHandler extends Polygon implements NodeLinkShapeHandler {
        public TriangleLinkShapeHandler(boolean input) {
            if (input) {
                getPoints().addAll(
                        0d, -5d, // top left
                        0d, 5d, // bottom left
                        10d, 0d); // center right
            } else {
                getPoints().addAll(
                        -10d, -5d, // top left
                        -10d, 5d, // bottom left
                        0d, 0d); // center right
            }
            //  ------\
            // |       >
            //  ------/
        }

        @Override
        public Paint getColor() {
            return getFill();
        }

        @Override
        public Node getComponent() {
            return this;
        }

        @Override
        public NodeLinkShape getType() {
            return TriangleLinkShape.this;
        }

        @Override
        public void setColor(Paint color) {
            setFill(color);
        }

        public TriangleLinkShapeHandler color(Paint color) {
            setColor(color);
            return this;
        }

        TriangleLinkShapeHandler shift() {
            setTranslateX(-5);
            return this;
        }

        @Override
        public NodeLinkShapeHandler cloneDummyShape() {
            // input: true because if its false, the offset gonna change a lil bit
            return new TriangleLinkShapeHandler(true).color(getFill()).shift();
        }
    }
}
