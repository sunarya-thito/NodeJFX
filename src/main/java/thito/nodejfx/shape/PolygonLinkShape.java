package thito.nodejfx.shape;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;
import thito.nodejfx.NodeLinkShape;
import thito.nodejfx.NodeParameter;

public class PolygonLinkShape implements NodeLinkShape {
    @Override
    public NodeLinkShapeHandler createNewHandler(NodeParameter parameter, boolean input) {
        return new PolygonLinkShapeHandler(input);
    }

    public class PolygonLinkShapeHandler extends Polygon implements NodeLinkShapeHandler {
        public PolygonLinkShapeHandler(boolean input) {
            getPoints().addAll(
                    -5d, -5d, // top left
                    -5d, 5d, // bottom left
                    5d, 5d, // bottom right
                    10d, 0d, // center right
                    5d, -5d); // top right

            if (input) {
                setTranslateX(5);
            } else {
                setTranslateX(-5);
            }
            //  ------\
            // |       >
            //  ------/
        }

        @Override
        public Node getComponent() {
            return this;
        }

        @Override
        public NodeLinkShape getType() {
            return PolygonLinkShape.this;
        }

        @Override
        public void setColor(Paint color) {
            setFill(color);
        }

        public PolygonLinkShapeHandler color(Paint color) {
            setColor(color);
            return this;
        }

        PolygonLinkShapeHandler shift() {
            setTranslateX(2.5);
            return this;
        }

        @Override
        public Paint getColor() {
            return getFill();
        }

        @Override
        public NodeLinkShapeHandler cloneDummyShape() {
            return new PolygonLinkShapeHandler(true).shift().color(getFill());
        }
    }
}
