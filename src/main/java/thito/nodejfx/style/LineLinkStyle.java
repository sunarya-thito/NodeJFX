package thito.nodejfx.style;

import javafx.scene.*;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import thito.nodejfx.*;
import thito.nodejfx.style.active.*;

public class LineLinkStyle implements NodeLinkStyle {

    @Override
    public NodeLinkStyleHandler createNewHandler(NodeLink nodeLink) {
        return new LineLinkStyleHandler(nodeLink);
    }

    public class LineLinkStyleHandler implements NodeLinkStyleHandler {
        private NodeLink link;
        private Line line;
        private ActiveLinkHelper activeLinkHelper;

        public LineLinkStyleHandler(NodeLink link) {
            this.link = link;
            line = new Line();
            line.setStrokeWidth(2.5);
            line.setPickOnBounds(false);
            line.setFill(Color.TRANSPARENT);
            activeLinkHelper = new ActiveLinkHelper(line, link.getEndShape());
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
            line.setStrokeWidth(selected ? 4 : 2.5);
        }

        @Override
        public Node getComponent() {
            return line;
        }

        @Override
        public void initialize(NodeLinkContainer container) {
            container.getChildren().add(0, line);
            activeLinkHelper.setContainer(container);
            if (active) {
                activeLinkHelper.play();
            }
        }

        @Override
        public void destroy(NodeLinkContainer container) {
            activeLinkHelper.stop();
            Parent parent = line.getParent();
            if (parent instanceof Pane) {
                ((Pane) parent).getChildren().remove(line);
            }
        }

        @Override
        public NodeLinkStyle getStyle() {
            return LineLinkStyle.this;
        }

        @Override
        public void update() {
            activeLinkHelper.setSourceColor(link.getSourceColor());
            activeLinkHelper.setTargetColor(link.getTargetColor());
            line.setStroke(link.getLinePaint());
            line.setStartX(link.getStartX().get());
            line.setStartY(link.getStartY().get());
            line.setEndX(link.getEndX().get());
            line.setEndY(link.getEndY().get());
        }
    }
}
