package thito.nodejfx;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.transform.Scale;

public class NodeViewport extends Pane {

    static final int gridSize = 100;
    private IntegerProperty scaleValue = new SimpleIntegerProperty(100);
    private NodeCanvas target;
    private Pane dummyNode;

//    private Translate moveTransform = new Translate();
    private Scale scaleTransform = new Scale(1, 1);

    private Image gridImage;

    private double dragOffsetX, dragOffsetY;

    public NodeViewport() {
        this.target = newCanvas();
        this.dummyNode = new Pane(target);

        gridImage = SwingFXUtils.toFXImage(NodeContext.generateGrid(gridSize, gridSize), null);

        dummyNode.layoutXProperty().bind(widthProperty().divide(2d));
        dummyNode.layoutYProperty().bind(heightProperty().divide(2d));

        target.getTransforms().add(scaleTransform);

        getChildren().add(dummyNode);

        target.scaleXProperty().bind(scaleValue.divide(100d));
        target.scaleYProperty().bind(scaleValue.divide(100d));

        dummyNode.translateXProperty().addListener(x -> updateBackground());
        dummyNode.translateYProperty().addListener(x -> updateBackground());

        scaleTransform.pivotXProperty().bind(dummyNode.widthProperty().divide(2d));
        scaleTransform.pivotYProperty().bind(dummyNode.heightProperty().divide(2d));

        setOnScroll(event -> {
            int oldScale = getScale();
            setScale((int) (getScale() + (event.getTextDeltaY() < 0 ? -Math.sqrt(-event.getTextDeltaY() / 5 * getScale()) : Math.sqrt(event.getTextDeltaY() / 5 * getScale()))));
            int newScale = getScale();
            if (oldScale == newScale) return;
            double x = event.getX();
            double y = event.getY();
            double centerX = getWidth() / 2; // this has to be divided by 4
            double centerY = getHeight() / 2; // since the scale pivot is at the center of the dummyNode
            double diffX = x - centerX;
            double diffY = y - centerY;
            target.setTranslateX(target.getTranslateX() - diffX * (event.getTextDeltaY() / 50));
            target.setTranslateY(target.getTranslateY() - diffY * (event.getTextDeltaY() / 50));
        });

        addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.getButton() == MouseButton.MIDDLE) {
                dragOffsetX = event.getX();
                dragOffsetY = event.getY();
                event.consume();
            }
        });

        addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
            if (event.getButton() == MouseButton.MIDDLE) {
                dummyNode.setTranslateX(clamp(-500, dummyNode.getTranslateX() + event.getX() - dragOffsetX, 500));
                dummyNode.setTranslateY(clamp(-500, dummyNode.getTranslateY() + event.getY() - dragOffsetY, 500));
                dragOffsetX = event.getX();
                dragOffsetY = event.getY();
                event.consume();
            }
        });

        updateBackground();
    }

    static double clamp(double min, double value, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public Pane getViewportContainer() {
        return dummyNode;
    }

    void updateBackground() {
        backgroundProperty().set(new Background(new BackgroundFill(
                new ImagePattern(gridImage, dummyNode.getTranslateX(), dummyNode.getTranslateY(), gridSize, gridSize, false),
                null, null)));
    }

    public ViewportState getState() {
        ViewportState state = new ViewportState();
        state.setPanX(target.getTranslateX());
        state.setPanY(target.getTranslateY());
        state.setZoom(scaleValue.get());
        return state;
    }

    public void loadState(ViewportState state) {
        target.setTranslateX(state.getPanX());
        target.setTranslateY(state.getPanY());
        scaleValue.set(state.getZoom());
    }

    public Scale getScaleTransform() {
        return scaleTransform;
    }

//    public Translate getMoveTransform() {
//        return moveTransform;
//    }

    public int getScale() {
        return scaleValue.get();
    }

    public IntegerProperty scaleProperty() {
        return scaleValue;
    }

    public void setScale(int scale) {
        scaleValue.set(scale);
    }

    public NodeCanvas getCanvas() {
        return target;
    }

    protected NodeCanvas newCanvas() {
        return new NodeCanvas();
    }
}
