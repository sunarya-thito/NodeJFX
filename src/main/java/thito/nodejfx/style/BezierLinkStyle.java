package thito.nodejfx.style;

import javafx.scene.*;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import thito.nodejfx.*;
import thito.nodejfx.style.active.*;

public class BezierLinkStyle implements NodeLinkStyle {

    @Override
    public NodeLinkStyleHandler createNewHandler(NodeLink nodeLink) {
        return new BezierLinkStyleHandler(nodeLink);
    }

    public class BezierLinkStyleHandler implements NodeLinkStyleHandler {
        private CubicCurve curve;
        private NodeLink nodeLink;
        private ActiveLinkHelper activeLinkHelper;

        public BezierLinkStyleHandler(NodeLink nodeLink) {
            this.nodeLink = nodeLink;
            curve = new CubicCurve();
            curve.setFill(Color.TRANSPARENT);
            curve.setPickOnBounds(false);
            curve.setStrokeWidth(2.5);
            activeLinkHelper = new ActiveLinkHelper(curve, nodeLink.getEndShape());
        }

        private boolean active;
        @Override
        public void setActive(boolean active) {
            this.active = active;
            if (active) {
                activeLinkHelper.play();
            } else {
                activeLinkHelper.stop();
            }
        }

        @Override
        public boolean isActive() {
            return active;
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
            activeLinkHelper.setContainer(container);
            if (active) {
                activeLinkHelper.play();
            }
        }

        @Override
        public void destroy(NodeLinkContainer container) {
            activeLinkHelper.stop();
            Parent parent = curve.getParent();
            if (parent instanceof Pane) {
                ((Pane) parent).getChildren().remove(curve);
            }
        }

        @Override
        public NodeLinkStyle getStyle() {
            return BezierLinkStyle.this;
        }

        @Override
        public void update() {
            curve.setStroke(nodeLink.getLinePaint());
            activeLinkHelper.setSourceColor(nodeLink.getSourceColor());
            activeLinkHelper.setTargetColor(nodeLink.getTargetColor());
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

