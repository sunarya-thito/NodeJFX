package thito.nodejfx.style;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import thito.nodejfx.NodeLink;
import thito.nodejfx.NodeLinkContainer;
import thito.nodejfx.NodeLinkStyle;

public class BezierLinkStyle implements NodeLinkStyle {

    @Override
    public NodeLinkStyleHandler createNewHandler(NodeLink nodeLink) {
        return new BezierLinkStyleHandler(nodeLink);
    }

    public class BezierLinkStyleHandler implements NodeLinkStyleHandler {
        private CubicCurve curve;
        private NodeLink nodeLink;

        public BezierLinkStyleHandler(NodeLink nodeLink) {
            this.nodeLink = nodeLink;
            curve = new CubicCurve();
            curve.setFill(Color.TRANSPARENT);
            curve.setPickOnBounds(false);
            curve.setStrokeWidth(2.5);
        }

        @Override
        public void setSelected(boolean selected) {
            curve.setStrokeWidth(selected ? 4 : 2.5);
        }

        @Override
        public Node getComponent() {
            return curve;
        }

        @Override
        public void initialize(NodeLinkContainer container) {
            container.getChildren().add(0, curve);
        }

        @Override
        public void destroy(NodeLinkContainer container) {
            container.getChildren().remove(curve);
        }

        @Override
        public NodeLinkStyle getStyle() {
            return BezierLinkStyle.this;
        }

        @Override
        public void update() {
            curve.setStroke(nodeLink.getLinePaint());
            double x1 = nodeLink.getStartX().get();
            double y1 = nodeLink.getStartY().get();
            double x2 = nodeLink.getEndX().get();
            double y2 = nodeLink.getEndY().get();
            double x = x2 - x1;
            curve.setStartX(x1);
            curve.setStartY(y1);
            curve.setEndX(x2);
            curve.setEndY(y2);
            curve.setControlX1(x1 + x / 2);
            curve.setControlX2(x2 - x / 2);
            curve.setControlY1(y1);
            curve.setControlY2(y2);
        }
    }
}

