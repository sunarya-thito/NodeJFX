package thito.nodejfx;

import javafx.animation.*;
import javafx.beans.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.css.*;
import javafx.embed.swing.*;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.transform.*;
import javafx.util.*;

import java.awt.image.*;

public class NodeViewport extends AnchorPane {

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
    private NodeCanvas target;
    private Pane dummyNode;
    private Pane scaleNode;
    private Scale scale = new Scale(1, 1);

    private ObjectProperty<Image> gridImage = new SimpleObjectProperty<>();

    private double dragOffsetX, dragOffsetY;

    private StyleableObjectProperty<Color> baseColor = new SimpleStyleableObjectProperty<>(baseColorCss, Color.rgb(35, 35, 35));
    private StyleableObjectProperty<Color> microLineColor = new SimpleStyleableObjectProperty<>(microLineColorCss, Color.rgb(45, 45, 45));
    private StyleableObjectProperty<Color> macroLineColor = new SimpleStyleableObjectProperty<>(macroLineColorCss, Color.rgb(25, 25, 25));

    private ObservableSet<Object> animationRequested = FXCollections.observableSet();
    private DoubleProperty xVelocity = new SimpleDoubleProperty(0);
    private DoubleProperty yVelocity = new SimpleDoubleProperty(0);
    private ObjectProperty<Point2D> dropPoint = new SimpleObjectProperty<>();

    private Canvas canvas = new Canvas();

    public NodeViewport() {
        this.target = newCanvas();
        target.setManaged(false);
        target.setViewport(this);
        this.dummyNode = new BorderPane(target);
        dummyNode.setManaged(false);
        scaleNode = new BorderPane(dummyNode);
        scaleNode.setManaged(false);

        setCache(true);
        setCacheShape(true);
        setCacheHint(CacheHint.SPEED);

        InvalidationListener updateGrid = obs -> {
            double size = gridSize;
            BufferedImage image = NodeContext.generateGrid(baseColor.get(), microLineColor.get(), macroLineColor.get(), (int) size, (int) size);
            Image img = SwingFXUtils.toFXImage(image, null);
            gridImage.set(img);
        };
        updateGrid.invalidated(null);
        baseColor.addListener(updateGrid);
        macroLineColor.addListener(updateGrid);
        microLineColor.addListener(updateGrid);

        dummyNode.layoutXProperty().bind(widthProperty().divide(2d));
        dummyNode.layoutYProperty().bind(heightProperty().divide(2d));
        scale.xProperty().addListener(x -> updateBackground());
        scale.yProperty().addListener(x -> updateBackground());

        scale.pivotXProperty().bind(widthProperty().divide(2));
        scale.pivotYProperty().bind(heightProperty().divide(2));

        scaleNode.getTransforms().add(scale);
        setTopAnchor(scaleNode, 0d);
        setBottomAnchor(scaleNode, 0d);
        setLeftAnchor(scaleNode, 0d);
        setRightAnchor(scaleNode, 0d);
        getChildren().add(scaleNode);

        widthProperty().addListener(x -> updateBackground());
        heightProperty().addListener(x -> updateBackground());
//        dummyNode.scaleYProperty().addListener(updateGrid);

        dummyNode.translateXProperty().addListener(x -> updateBackground());
        dummyNode.translateYProperty().addListener(x -> updateBackground());
//        gridImage.addListener(x -> updateBackground());

        setOnScroll(event -> {
            double zoomFactor = 1.1;
            if (event.getDeltaY() <= 0) {
                // zoom out
                zoomFactor = 1 / zoomFactor;
            }
            zoom(dummyNode, zoomFactor, event.getX(), event.getY());
        });

        addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.isPrimaryButtonDown()) {
                getAnimationRequested().add(this);
                requestFocus();
                getCanvas().getSelectionContainer().startDragging(event.getSceneX(), event.getSceneY(), event.isControlDown());
                event.consume();
            }
        });

        addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            if (getCanvas().getSelectionContainer().isDragging()) {
                xOverflowProperty().set(event.getX());
                yOverflowProperty().set(event.getY());
                getCanvas().getSelectionContainer().moveDrag(event.getSceneX(), event.getSceneY(), event.isShiftDown());
                event.consume();
            }
        });

        addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            if (getCanvas().getSelectionContainer().isDragging()) {
                getAnimationRequested().remove(this);
                getCanvas().getSelectionContainer().stopDragging();
                event.consume();
            }
        });

        addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.getButton() == MouseButton.MIDDLE) {
                dragOffsetX = event.getX();
                dragOffsetY = event.getY();
                event.consume();
                if (dropPoint.get() == null) {
                    dropPoint.set(new Point2D(dummyNode.getTranslateX(), dummyNode.getTranslateY()));
                }
            }
        });

        addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
            if (event.getButton() == MouseButton.MIDDLE) {
                dummyNode.setTranslateX(clamp(-10000, dummyNode.getTranslateX() + (event.getX() - dragOffsetX) / scale.getX(), 10000));
                dummyNode.setTranslateY(clamp(-10000, dummyNode.getTranslateY() + (event.getY() - dragOffsetY) / scale.getY(), 10000));
                dragOffsetX = event.getX();
                dragOffsetY = event.getY();
                event.consume();
            }
        });

        addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
            dropPoint.set(new Point2D(dummyNode.getTranslateX(), dummyNode.getTranslateY()));
        });

        updateBackground();

        animationRequested.addListener((InvalidationListener) observable -> {
            if (animationRequested.isEmpty()) {
                stopTimeline();
            } else {
                startTimeline();
            }
        });
    }

    private Timeline timeline;
    public void stopTimeline() {
        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }
    }

    public void startTimeline() {
        if (timeline == null) {
            timeline = new Timeline(new KeyFrame(Duration.millis(16), event -> tick()));
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();
        }
    }

    private void tick() {
        double velX = xVelocity.doubleValue();
        double velY = yVelocity.doubleValue();
        Bounds bounds = getLayoutBounds();
        int padding = 25;
        if (velX < bounds.getMinX() + padding) {
            paneX().set(paneX().get() + (bounds.getMinX() + padding - velX) * 0.1);
        } else if (velX > bounds.getMaxX() - padding) {
            paneX().set(paneX().get() - (velX - (bounds.getMaxX() - padding)) * 0.1);
        }
        if (velY < bounds.getMinY() + padding) {
            paneY().set(paneY().get() + (bounds.getMinY() + padding - velY) * 0.1);
        } else if (velY > bounds.getMaxY() - padding) {
            paneY().set(paneY().get() - (velY - (bounds.getMaxY() - padding)) * 0.1);
        }
    }

    public ObservableSet<Object> getAnimationRequested() {
        return animationRequested;
    }

    public DoubleProperty xOverflowProperty() {
        return xVelocity;
    }

    public DoubleProperty yOverflowProperty() {
        return yVelocity;
    }

    public ObjectProperty<Point2D> dropPointProperty() {
        return dropPoint;
    }

    public DoubleProperty paneX() {
        return dummyNode.translateXProperty();
    }

    public DoubleProperty paneY() {
        return dummyNode.translateYProperty();
    }

    public DoubleProperty scaleX() {
        return scale.xProperty();
    }

    public DoubleProperty scaleY() {
        return scale.yProperty();
    }

    static double clamp(double min, double value, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public Pane getViewportContainer() {
        return dummyNode;
    }

    void updateBackground() {
//        GraphicsContext context = canvas.getGraphicsContext2D();
//        context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
//        context.setFill(baseColor.get());
//        context.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
//        int offsetX = (int) dummyNode.getTranslateX();
//        int offsetY = (int) dummyNode.getTranslateY();
//        for (int x = 0; x < canvas.getWidth(); x++) {
//            for (int y = 0; y < canvas.getHeight(); y++) {
//                if ((x - offsetX) % gridSize == 0 || (y - offsetY) % gridSize == 0) {
//                    context.setFill(macroLineColor.get());
//                    context.fillRect(x - 0.75, y - 0.75, 1.5, 1.5);
//                } else if ((x - offsetX) % (gridSize / 5) == 0 || (y - offsetY) % (gridSize / 5) == 0) {
//                    context.setFill(microLineColor.get());
//                    context.fillRect(x, y, 1, 1);
//                }
//            }
//        }
        backgroundProperty().set(new Background(new BackgroundFill(
                new ImagePattern(gridImage.get(),
                        dummyNode.getTranslateX() * scale.getX() + getWidth() / 2,
                        dummyNode.getTranslateY() * scale.getY() + getHeight() / 2,
                        gridImage.get().getWidth() * scale.getX(),
                        gridImage.get().getHeight() * scale.getY(),
                        false),
                null, null)));
    }

    public ViewportState getState() {
        ViewportState state = new ViewportState();
        state.setPanX(target.getTranslateX());
        state.setPanY(target.getTranslateY());
//        state.setZoom(scaleValue.get());
        return state;
    }

    public void loadState(ViewportState state) {
        target.setTranslateX(state.getPanX());
        target.setTranslateY(state.getPanY());
    }

    public NodeCanvas getCanvas() {
        return target;
    }

    protected NodeCanvas newCanvas() {
        return new NodeCanvas();
    }

    private void zoom(Node dummyNode, double factor, double x, double y) {
        // determine scale
        double oldScale = this.scale.getX();
        double scale = oldScale * factor;
        scale = Math.max(Math.min(1, scale), 0.1);
        double f = (scale / oldScale) - 1;

        // determine offset that we will have to move the node
        double dx = x - getWidth() / 2d;
        double dy = y - getHeight() / 2d;

        // timeline that scales and moves the node
        dummyNode.setTranslateX(dummyNode.getTranslateX() - f * dx);
        dummyNode.setTranslateY(dummyNode.getTranslateY() - f * dy);
        this.scale.setX(scale);
        this.scale.setY(scale);
    }
}
