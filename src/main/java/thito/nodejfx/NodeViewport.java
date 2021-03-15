package thito.nodejfx;

import javafx.beans.*;
import javafx.beans.property.*;
import javafx.css.*;
import javafx.embed.swing.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.transform.*;

public class NodeViewport extends Pane {

    private static final CssMetaData<NodeViewport, Color> baseColorCss = new CssMetaData<NodeViewport, Color>("-fx-base-color", StyleConverter.getColorConverter()) {
        @Override
        public boolean isSettable(NodeViewport styleable) {
            return !styleable.baseColor.isBound();
        }

        @Override
        public StyleableProperty<Color> getStyleableProperty(NodeViewport styleable) {
            return styleable.baseColor;
        }
    };

    private static final CssMetaData<NodeViewport, Color> microLineColorCss = new CssMetaData<NodeViewport, Color>("-fx-micro-line-color", StyleConverter.getColorConverter()) {
        @Override
        public boolean isSettable(NodeViewport styleable) {
            return !styleable.microLineColor.isBound();
        }

        @Override
        public StyleableProperty<Color> getStyleableProperty(NodeViewport styleable) {
            return styleable.microLineColor;
        }
    };

    private static final CssMetaData<NodeViewport, Color> macroLineColorCss = new CssMetaData<NodeViewport, Color>("-fx-macro-line-color", StyleConverter.getColorConverter()) {
        @Override
        public boolean isSettable(NodeViewport styleable) {
            return !styleable.macroLineColor.isBound();
        }

        @Override
        public StyleableProperty<Color> getStyleableProperty(NodeViewport styleable) {
            return styleable.macroLineColor;
        }
    };

    static final int gridSize = 100;
    private IntegerProperty scaleValue = new SimpleIntegerProperty(100);
    private NodeCanvas target;
    private Pane dummyNode;

    private Scale scaleTransform = new Scale(1, 1);

    private ObjectProperty<Image> gridImage = new SimpleObjectProperty<>();

    private double dragOffsetX, dragOffsetY;

    private StyleableObjectProperty<Color> baseColor = new SimpleStyleableObjectProperty<>(baseColorCss, Color.rgb(35, 35, 35));
    private StyleableObjectProperty<Color> microLineColor = new SimpleStyleableObjectProperty<>(microLineColorCss, Color.rgb(45, 45, 45));
    private StyleableObjectProperty<Color> macroLineColor = new SimpleStyleableObjectProperty<>(macroLineColorCss, Color.rgb(25, 25, 25));


    public NodeViewport() {
        this.target = newCanvas();
        this.dummyNode = new Pane(target);

        InvalidationListener updateGrid = obs -> {
            gridImage.set(SwingFXUtils.toFXImage(NodeContext.generateGrid(baseColor.get(), microLineColor.get(), macroLineColor.get(), gridSize, gridSize), null));
        };
        updateGrid.invalidated(null);
        baseColor.addListener(updateGrid);
        macroLineColor.addListener(updateGrid);
        microLineColor.addListener(updateGrid);

        dummyNode.layoutXProperty().bind(widthProperty().divide(2d));
        dummyNode.layoutYProperty().bind(heightProperty().divide(2d));

        target.getTransforms().add(scaleTransform);

        getChildren().add(dummyNode);

        target.scaleXProperty().bind(scaleValue.divide(100d));
        target.scaleYProperty().bind(scaleValue.divide(100d));

        dummyNode.translateXProperty().addListener(x -> updateBackground());
        dummyNode.translateYProperty().addListener(x -> updateBackground());
        gridImage.addListener(x -> updateBackground());

        scaleTransform.xProperty().bind(scaleProperty().divide(100d));
        scaleTransform.yProperty().bind(scaleProperty().divide(100d));
        scaleTransform.pivotXProperty().bind(dummyNode.widthProperty().divide(2d));
        scaleTransform.pivotYProperty().bind(dummyNode.heightProperty().divide(2d));

        setOnScroll(event -> {
            if (focusedProperty().get()) {
                setScale((int) (getScale() + (event.getTextDeltaY() < 0 ? -Math.sqrt(-event.getTextDeltaY() / 5 * getScale()) : Math.sqrt(event.getTextDeltaY() / 5 * getScale()))));
            }
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
                dummyNode.setTranslateX(clamp(-5000, dummyNode.getTranslateX() + event.getX() - dragOffsetX, 5000));
                dummyNode.setTranslateY(clamp(-5000, dummyNode.getTranslateY() + event.getY() - dragOffsetY, 5000));
                dragOffsetX = event.getX();
                dragOffsetY = event.getY();
                event.consume();
            }
        });

        updateBackground();
    }

    public DoubleProperty paneX() {
        return dummyNode.translateXProperty();
    }

    public DoubleProperty paneY() {
        return dummyNode.translateYProperty();
    }

    static double clamp(double min, double value, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public Pane getViewportContainer() {
        return dummyNode;
    }

    void updateBackground() {
        backgroundProperty().set(new Background(new BackgroundFill(
                new ImagePattern(gridImage.get(), dummyNode.getTranslateX(), dummyNode.getTranslateY(), gridSize, gridSize, false),
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
