package thito.nodejfx.style;

import javafx.scene.*;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import thito.nodejfx.*;
import thito.nodejfx.style.active.*;

public class PipeLinkStyle implements NodeLinkStyle {

    @Override
    public NodeLinkStyleHandler createNewHandler(NodeLink nodeLink) {
        return new PipeLinkStyleHandler(nodeLink);
    }

    public class PipeLinkStyleHandler implements NodeLinkStyleHandler {
        private Path path;
        private MoveTo startLine;
        private LineTo endStartLine;
        private MoveTo verticalLine;
        private LineTo endVerticalLine;
        private MoveTo endLine;
        private LineTo endEndLine;
        private NodeLink nodeLink;
        private ActiveLinkHelper activeLinkHelper;

        public PipeLinkStyleHandler(NodeLink nodeLink) {
            this.nodeLink = nodeLink;
            startLine = new MoveTo();
            endStartLine = new LineTo();
            verticalLine = new MoveTo();
            endVerticalLine = new LineTo();
            endLine = new MoveTo();
            endEndLine = new LineTo();
            path = new Path();
            path.setPickOnBounds(false);
            path.setFill(Color.TRANSPARENT);
            path.setStrokeWidth(2.5);
            path.setStrokeLineCap(StrokeLineCap.ROUND);
            path.setStrokeLineJoin(StrokeLineJoin.ROUND);
            path.getElements().addAll(startLine, endStartLine, verticalLine, endVerticalLine, endLine, endEndLine);
            path.setEffect(new DropShadow(3, NodeContext.SHADOW_NODE));
            activeLinkHelper = new ActiveLinkHelper(path, nodeLink.getEndShape());
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
            path.setStrokeWidth(selected ? 4 : 2.5);
        }

        @Override
        public Node getComponent() {
            return path;
        }

        @Override
        public void initialize(NodeLinkContainer container) {
            container.getChildren().add(0, path);
            activeLinkHelper.setContainer(container);
            if (active) {
                activeLinkHelper.play();
            }
        }

        @Override
        public void destroy(NodeLinkContainer container) {
            activeLinkHelper.stop();
            Parent parent = path.getParent();
            if (parent instanceof Pane) {
                ((Pane) parent).getChildren().remove(path);
            }
        }

        @Override
        public NodeLinkStyle getStyle() {
            return PipeLinkStyle.this;
        }

        @Override
        public void update() {
            activeLinkHelper.setSourceColor(nodeLink.getSourceColor());
            activeLinkHelper.setTargetColor(nodeLink.getTargetColor());
            path.setStroke(nodeLink.getLinePaint());
            double x1 = nodeLink.getStartX().get();
            double y1 = nodeLink.getStartY().get();
            double x2 = nodeLink.getEndX().get();
            double y2 = nodeLink.getEndY().get();
            double x = x2 - x1;
            startLine.setX(x1);
            startLine.setY(y1);
            endStartLine.setX(x1 + x / 4);
            endStartLine.setY(y1);
            verticalLine.setX(endStartLine.getX());
            verticalLine.setY(endStartLine.getY());
            endVerticalLine.setX(x2 - x / 4);
            endVerticalLine.setY(y2);
            endLine.setX(endVerticalLine.getX());
            endLine.setY(endVerticalLine.getY());
            endEndLine.setX(x2);
            endEndLine.setY(y2);
        }
    }
}
