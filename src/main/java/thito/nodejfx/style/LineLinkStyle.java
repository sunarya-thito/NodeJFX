package thito.nodejfx.style;

import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import thito.nodejfx.NodeContext;
import thito.nodejfx.NodeLink;
import thito.nodejfx.NodeLinkContainer;
import thito.nodejfx.NodeLinkStyle;

public class LineLinkStyle implements NodeLinkStyle {

    @Override
    public NodeLinkStyleHandler createNewHandler(NodeLink nodeLink) {
        return new LineLinkStyleHandler(nodeLink);
    }

    public class LineLinkStyleHandler implements NodeLinkStyleHandler {
        private NodeLink link;
        private Line line;

        public LineLinkStyleHandler(NodeLink link) {
            this.link = link;
            line = new Line();
            line.setStrokeWidth(2.5);
            line.setPickOnBounds(false);
            line.setFill(Color.TRANSPARENT);
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
        }

        @Override
        public void destroy(NodeLinkContainer container) {
            container.getChildren().remove(line);
        }

        @Override
        public NodeLinkStyle getStyle() {
            return LineLinkStyle.this;
        }

        @Override
        public void update() {
            line.setStroke(link.getLinePaint());
            line.setStartX(link.getStartX().get());
            line.setStartY(link.getStartY().get());
            line.setEndX(link.getEndX().get());
            line.setEndY(link.getEndY().get());
        }
    }
}
