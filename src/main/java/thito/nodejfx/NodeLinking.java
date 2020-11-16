package thito.nodejfx;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.geometry.Point2D;
import javafx.scene.paint.Paint;

public class NodeLinking extends NodeLink implements InvalidationListener {
    private NodeParameter source;
    private boolean input;
    public NodeLinking(NodeLinkContainer container, NodeLinkStyle style, NodeParameter source, boolean input, double x, double y) {
        super(
                container, style,
                container.sceneToLocal(input ? source.getInputLocation() : source.getOutputLocation()),
                container.sceneToLocal(x, y),
                input ? source.getInputShape() : source.getOutputShape());
        this.source = source;
        this.input = input;

        setStyle(style);
    }

    @Override
    public Paint getLinePaint() {
        NodeParameterType type = input ? source.getInputType().get() : source.getOutputType().get();
        Paint color = type == null ? null : (input ? type.inputColorProperty() : type.outputColorProperty()).get();
        return color == null ? super.getLinePaint() : color;
    }

    public NodeParameter getParameter() {
        return source;
    }

    public boolean isInput() {
        return input;
    }

    @Override
    public void invalidated(Observable observable) {
        Point2D outLoc = container.sceneToLocal(input ? source.getInputLocation() : source.getOutputLocation());
        getStartX().set(outLoc.getX());
        getStartY().set(outLoc.getY());
        getStyle().update();
    }

    @Override
    public void initialize(NodeLinkContainer container) {
        super.initialize(container);
        source.initialize(this);
    }

    @Override
    public void destroy(NodeLinkContainer container) {
        super.destroy(container);
        source.destroy(this);
    }
}
