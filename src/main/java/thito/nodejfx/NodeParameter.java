package thito.nodejfx;

import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import thito.nodejfx.event.NodeLinkEvent;
import thito.nodejfx.internal.EventHandlerProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class NodeParameter extends AnchorPane {

    private SimpleBooleanProperty allowInput = new SimpleBooleanProperty();
    private SimpleBooleanProperty allowOutput = new SimpleBooleanProperty();
    private SimpleObjectProperty<NodeParameterType> inputType = new SimpleObjectProperty<>();
    private SimpleObjectProperty<NodeParameterType> outputType = new SimpleObjectProperty<>();

    private ObservableSet<NodeParameter> inputLinks = FXCollections.observableSet(ConcurrentHashMap.newKeySet());
    private ObservableSet<NodeParameter> outputLinks = FXCollections.observableSet(ConcurrentHashMap.newKeySet());

    private ObservableSet<NodeParameter> unmodifiableInputLinks = FXCollections.unmodifiableObservableSet(inputLinks);
    private ObservableSet<NodeParameter> unmodifiableOutputLinks = FXCollections.unmodifiableObservableSet(outputLinks);

    // Events

    private EventHandlerProperty<NodeLinkEvent> onNodeLinkedEvent = new EventHandlerProperty<>();
    private EventHandlerProperty<NodeLinkEvent> onNodeLinkingEvent = new EventHandlerProperty<>();
    private EventHandlerProperty<NodeLinkEvent> onNodeUnlinkedEvent = new EventHandlerProperty<>();

    //

    private List<NodeLink> links = new ArrayList<>();

    private NodeParameterContainer container = new NodeParameterContainer();

    private NodeLinkShape.NodeLinkShapeHandler inputShape, outputShape;

    private HBox separator;

    private NodeDragListener inputDrag, outputDrag;

    private Node node;

    private BooleanProperty multipleInputAssigner = new SimpleBooleanProperty(), multipleOutputAssigner = new SimpleBooleanProperty();

    public NodeParameter() {
        setPickOnBounds(false);

        container.setPadding(new Insets(5, 20, 5, 20));

        setTopAnchor(container, 0d);
        setBottomAnchor(container, 0d);
        setLeftAnchor(container, 0d);
        setRightAnchor(container, 0d);

        getChildren().add(container);

        heightProperty().addListener((obs, oldValue, newValue) -> {
            inputShape.getComponent().layoutYProperty().set(newValue.doubleValue() / 2d);
            outputShape.getComponent().layoutYProperty().set(newValue.doubleValue() / 2d);
        });

        widthProperty().addListener((obs, old, val) -> {
            outputShape.getComponent().layoutXProperty().set(val.doubleValue());
        });

        allowInput.addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                getChildren().add(inputShape.getComponent());
            } else {
                getChildren().remove(inputShape.getComponent());
            }
        });

        allowOutput.addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                getChildren().add(outputShape.getComponent());
            } else {
                getChildren().remove(outputShape.getComponent());
            }
        });

        InvalidationListener updateListener = e -> {
            for (NodeLink link : links) {
                if (link instanceof InvalidationListener) {
                    ((InvalidationListener) link).invalidated(e);
                }
            }
        };

        inputType.addListener((obs, oldValue, newValue) -> {
            inputShape.setColor(newValue.inputColorProperty().getValue());
            if (oldValue != null) {
                oldValue.inputColorProperty().removeListener(updateListener);
            }
            newValue.inputColorProperty().addListener(updateListener);
        });

        outputType.addListener((obs, oldValue, newValue) -> {
            outputShape.setColor(newValue.outputColorProperty().getValue());
            if (oldValue != null) {
                oldValue.outputColorProperty().removeListener(updateListener);
            }
            newValue.outputColorProperty().addListener(updateListener);
        });

        // event handler
        addEventHandler(NodeLinkEvent.NODE_LINKED_EVENT, onNodeLinkedEvent);
        addEventHandler(NodeLinkEvent.NODE_LINKING_EVENT, onNodeLinkingEvent);
        addEventHandler(NodeLinkEvent.NODE_UNLINKED_EVENT, onNodeUnlinkedEvent);

        // disconnect assigners
        multipleInputAssigner.addListener((obs, old, val) -> {
            NodeCanvas canvas = getCanvas();
            if (canvas != null) {
                boolean begin = false;
                for (NodeParameter other : getUnmodifiableInputLinks()) {
                    if (begin) {
                        canvas.disconnect(this, other);
                    } else begin = true; // this will skip and keep the first assigner and disconnect the rest
                }
            }
        });

        multipleOutputAssigner.addListener((obs, old, val) -> {
            NodeCanvas canvas = getCanvas();
            if (canvas != null) {
                boolean begin = false;
                for (NodeParameter other : getUnmodifiableOutputLinks()) {
                    if (begin) {
                        canvas.disconnect(other, this);
                    } else begin = true;
                }
            }
        });

        // Initialize default values
        setInputShape(NodeLinkShape.DEFAULT_SHAPE);
        setOutputShape(NodeLinkShape.DEFAULT_SHAPE);
        allowInput.set(true);
        allowOutput.set(true);
        inputType.set(NodeParameterType.DEFAULT_TYPE);
        outputType.set(NodeParameterType.DEFAULT_TYPE);
    }

    public NodeParameterContainer getContainer() {
        return container;
    }

    public void setInputShape(NodeLinkShape type) {
        if (inputShape != null) {
            getChildren().remove(inputShape.getComponent());
        }
        inputShape = type.createNewHandler(this, true);
        inputShape.getComponent().layoutXProperty().set(0);
        if (allowInput.get()) {
            getChildren().add(inputShape.getComponent());
        }
        inputShape.getComponent().setOnMouseEntered(event -> {
            NodeCanvas canvas = getCanvas();
            if (canvas != null) {
                for (NodeLink link : canvas.getLinkContainer().getLinks()) {
                    if (link instanceof NodeLinked) {
                        if (((NodeLinked) link).getTarget() == this) {
                            ((NodeLinked) link).setLinkHover(true);
                        }
                    }
                }
            }
        });
        inputShape.getComponent().setOnMouseExited(event -> {
            NodeCanvas canvas = getCanvas();
            if (canvas != null) {
                for (NodeLink link : canvas.getLinkContainer().getLinks()) {
                    if (link instanceof NodeLinked) {
                        if (((NodeLinked) link).getTarget() == this) {
                            ((NodeLinked) link).setLinkHover(false);
                        }
                    }
                }
            }
        });
        inputDrag = new NodeDragListener(this, true, inputShape.getComponent());
    }

    public BooleanProperty getMultipleInputAssigner() {
        return multipleInputAssigner;
    }

    public BooleanProperty getMultipleOutputAssigner() {
        return multipleOutputAssigner;
    }

    public void setOutputShape(NodeLinkShape type) {
        if (outputShape != null) {
            getChildren().remove(outputShape.getComponent());
        }
        outputShape = type.createNewHandler(this, false);
        outputShape.getComponent().setManaged(false);
        outputShape.getComponent().setLayoutX(getWidth());
        if (allowOutput.get()) {
            getChildren().add(outputShape.getComponent());
        }
        outputShape.getComponent().setOnMouseEntered(event -> {
            NodeCanvas canvas = getCanvas();
            if (canvas != null) {
                for (NodeLink link : canvas.getLinkContainer().getLinks()) {
                    if (link instanceof NodeLinked) {
                        if (((NodeLinked) link).getSource() == this) {
                            ((NodeLinked) link).setLinkHover(true);
                        }
                    }
                }
            }
        });
        outputShape.getComponent().setOnMouseExited(event -> {
            NodeCanvas canvas = getCanvas();
            if (canvas != null) {
                for (NodeLink link : canvas.getLinkContainer().getLinks()) {
                    if (link instanceof NodeLinked) {
                        if (((NodeLinked) link).getSource() == this) {
                            ((NodeLinked) link).setLinkHover(false);
                        }
                    }
                }
            }
        });
        outputDrag = new NodeDragListener(this, false, outputShape.getComponent());
    }

    public NodeLinkShape.NodeLinkShapeHandler getInputShape() {
        return inputShape;
    }

    public NodeLinkShape.NodeLinkShapeHandler getOutputShape() {
        return outputShape;
    }

    public ObjectProperty<EventHandler<NodeLinkEvent>> onNodeLinkedEvent() {
        return onNodeLinkedEvent;
    }

    public ObjectProperty<EventHandler<NodeLinkEvent>> onNodeLinkingEvent() {
        return onNodeLinkingEvent;
    }

    public ObjectProperty<EventHandler<NodeLinkEvent>> onNodeUnlinkedEvent() {
        return onNodeUnlinkedEvent;
    }

    public NodeDragListener getInputDrag() {
        return inputDrag;
    }

    public NodeDragListener getOutputDrag() {
        return outputDrag;
    }

    public Node getNode() {
        return node;
    }

    public NodeCanvas getCanvas() {
        return node == null ? null : node.getCanvas();
    }

    protected void initialize(Node node) {
        this.node = node;
    }

    protected void destroy(Node node) {
        this.node = null;
    }

    public boolean isInBounds(Point2D loc) {
        if (getLayoutBounds().contains(loc)) {
            return true;
        }
        if (inputShape.getComponent().getBoundsInParent().contains(loc)) {
            return true;
        }
        if (inputShape.getComponent().getBoundsInParent().contains(loc)) {
            return true;
        }
        return false;
    }

    void setSeparated(boolean separated) {
        if (separated) {
            if (separator == null) {
                separator = new HBox();
                separator.setBackground(new Background(new BackgroundFill(NodeContext.BACKGROUND_SEPARATOR, null, new Insets(0, 10, 0, 10))));
                separator.setMinHeight(1);
                separator.minWidthProperty().bind(widthProperty());
                getChildren().add(0, separator);
            }
        } else {
            if (separator != null) {
                getChildren().remove(separator);
                separator = null;
            }
        }
    }

    public Point2D getInputLocation() {
        return localToScene(inputShape.getComponent().getLayoutX(), inputShape.getComponent().getLayoutY());
    }

    public Point2D getOutputLocation() {
        return localToScene(outputShape.getComponent().getLayoutX(), outputShape.getComponent().getLayoutY());
    }

    protected void initialize(NodeLink x) {
        links.add(x);
        if (x instanceof InvalidationListener) {
            InvalidationListener linked = (InvalidationListener) x;
            Node node = getNode();
            node.layoutXProperty().addListener(linked);
            node.layoutYProperty().addListener(linked);
            heightProperty().addListener(linked);
            widthProperty().addListener(linked);
            getInputType().addListener(linked);
            getOutputType().addListener(linked);
        }
    }

    protected void destroy(NodeLink x) {
        links.remove(x);
        if (x instanceof InvalidationListener) {
            InvalidationListener linked = (InvalidationListener) x;
            Node node = getNode();
            node.layoutXProperty().removeListener(linked);
            node.layoutYProperty().removeListener(linked);
            heightProperty().removeListener(linked);
            widthProperty().removeListener(linked);
            getInputType().removeListener(linked);
            getOutputType().removeListener(linked);
        }
    }

    public SimpleBooleanProperty getAllowInput() {
        return allowInput;
    }

    public SimpleBooleanProperty getAllowOutput() {
        return allowOutput;
    }

    public SimpleObjectProperty<NodeParameterType> getOutputType() {
        return outputType;
    }

    public SimpleObjectProperty<NodeParameterType> getInputType() {
        return inputType;
    }

    public ObservableSet<NodeParameter> getUnmodifiableInputLinks() {
        return unmodifiableInputLinks;
    }

    public ObservableSet<NodeParameter> getUnmodifiableOutputLinks() {
        return unmodifiableOutputLinks;
    }

    protected ObservableSet<NodeParameter> inputLinks() {
        return inputLinks;
    }

    protected ObservableSet<NodeParameter> outputLinks() {
        return outputLinks;
    }

    protected Pos defaultAlignment() {
        return Pos.CENTER_LEFT;
    }

    public class NodeParameterContainer extends VBox {

        public NodeParameterContainer() {
            setPickOnBounds(false);
            allowInput.addListener(x -> updateAlignment());
            allowOutput.addListener(x -> updateAlignment());
            updateAlignment();
        }

        void updateAlignment() {
            if ((allowInput.get() && allowOutput.get()) || (!allowInput.get() && !allowOutput.get())) {
                setAlignment(defaultAlignment());
            } else if (allowInput.get()) {
                setAlignment(Pos.CENTER_LEFT);
            } else if (allowOutput.get()) {
                setAlignment(Pos.CENTER_RIGHT);
            } else {
                setAlignment(defaultAlignment());
            }
        }

    }

}
