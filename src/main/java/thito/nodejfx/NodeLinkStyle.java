package thito.nodejfx;

import thito.nodejfx.style.BezierLinkStyle;
import thito.nodejfx.style.LineLinkStyle;
import thito.nodejfx.style.PipeLinkStyle;

public interface NodeLinkStyle {

    NodeLinkStyle LINE_STYLE = new LineLinkStyle();
    NodeLinkStyle BEZIER_STYLE = new BezierLinkStyle();
    NodeLinkStyle PIPE_STYLE = new PipeLinkStyle();

    NodeLinkStyleHandler createNewHandler(NodeLink nodeLink);

    interface NodeLinkStyleHandler {

        javafx.scene.Node getComponent();

        void setSelected(boolean selected);

        void initialize(NodeLinkContainer container);

        void destroy(NodeLinkContainer container);

        void update();

        void setActive(boolean active);

        NodeLinkStyle getStyle();

        boolean isActive();
    }
}
