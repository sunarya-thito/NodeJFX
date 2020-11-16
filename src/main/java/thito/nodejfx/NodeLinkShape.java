package thito.nodejfx;

import javafx.scene.paint.Paint;
import thito.nodejfx.shape.CircleLinkShape;
import thito.nodejfx.shape.PolygonLinkShape;
import thito.nodejfx.shape.TriangleLinkShape;

public interface NodeLinkShape {

    /**
     * For value input/output
     */
    NodeLinkShape CIRCLE_SHAPE = new CircleLinkShape();
    /**
     * For execution
     */
    NodeLinkShape POLYGON_SHAPE = new PolygonLinkShape();
    /**
     */
    NodeLinkShape TRIANGLE_SHAPE = new TriangleLinkShape();
    NodeLinkShape DEFAULT_SHAPE = POLYGON_SHAPE;

    NodeLinkShapeHandler createNewHandler(NodeParameter parameter, boolean input);

    interface NodeLinkShapeHandler {

        javafx.scene.Node getComponent();
        NodeLinkShape getType();
        void setColor(Paint color);
        Paint getColor();
        NodeLinkShapeHandler cloneDummyShape();

    }

}
