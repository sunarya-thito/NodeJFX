package thito.nodejfx;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.event.Event;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import javax.naming.Binding;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class NodeGroup extends Group implements NodeCanvasElement {

    private NodeGroupTitle title;
    private NodeCanvas canvas;

    private NodeGroupCorner topLeft, top, topRight, left, right, bottomLeft, bottom, bottomRight;
    private Pane borderPane;
    private Rectangle mask;
    private SimpleDoubleProperty topPos = new SimpleDoubleProperty(), rightPos = new SimpleDoubleProperty(), leftPos = new SimpleDoubleProperty(), bottomPos = new SimpleDoubleProperty();
    private NodeGroupHighlight highlight;

    private BooleanProperty selected = new SimpleBooleanProperty();

    private ObservableList<NodeCanvasElement> nodes = FXCollections.observableArrayList();

    private ObservableSet<NodeGroup> groups = FXCollections.observableSet(ConcurrentHashMap.newKeySet());

    private ObjectProperty<Color> groupColor = new SimpleObjectProperty<>(NodeContext.randomBrightColor(1));

    public boolean isOnBounds(NodeCanvasElement node) {
        return getExactBounds().contains(node.getExactBounds());
    }

    @Override
    public javafx.scene.Node getComponent() {
        return this;
    }

    @Override
    public Bounds getExactBounds() {
        return borderPane.localToScene(borderPane.getBoundsInLocal());
    }

    public Pane getBorderPane() {
        return borderPane;
    }

    private ObjectProperty<Color> controllerColor = new SimpleObjectProperty<>(NodeContext.BACKGROUND_GROUP);

    private NodeContext.DragInfo dragInfo;

    @Override
    public ElementState getState() {
        ElementState state = new ElementState();
        state.setLayoutX(leftPos.get());
        state.setLayoutY(topPos.get());
        state.setWidth(rightPos.get() - state.getLayoutX());
        state.setHeight(bottomPos.get() - state.getLayoutY());
        return state;
    }

    @Override
    public void loadState(ElementState state) {
        leftPos.set(state.getLayoutX());
        topPos.set(state.getLayoutY());
        rightPos.set(state.getWidth() + state.getLayoutX());
        bottomPos.set(state.getHeight() + state.getLayoutY());
    }

    public NodeGroup() {

        double big = 10;
        double small = 8;
        setPickOnBounds(false);
        borderPane = new Pane();
        borderPane.setPickOnBounds(false);
        borderPane.setBorder(new Border(new BorderStroke(NodeContext.BACKGROUND_GROUP_BORDER, BorderStrokeStyle.SOLID, null, new BorderWidths(2))));
        borderPane.addEventFilter(MouseEvent.MOUSE_PRESSED, this::attemptPress);
        borderPane.addEventHandler(MouseEvent.MOUSE_PRESSED, Event::consume);

        mask = new Rectangle();
        mask.widthProperty().bind(borderPane.widthProperty());
        mask.heightProperty().bind(borderPane.heightProperty());

        borderPane.getChildren().add(title = new NodeGroupTitle());

        getChildren().addAll(borderPane,
                left = new NodeGroupCorner(small, true, false),
                right = new NodeGroupCorner(small, true, false),
                top = new NodeGroupCorner(small, false, true),
                bottom = new NodeGroupCorner(small, false, true),
                topLeft = new NodeGroupCorner(big, true, true),
                topRight = new NodeGroupCorner(big, true, true),
                bottomRight = new NodeGroupCorner(big, true, true),
                bottomLeft = new NodeGroupCorner(big, true, true)
                );

        highlight = new NodeGroupHighlight(this);

        topPos.bindBidirectional(topRight.layoutYProperty());
        topPos.bindBidirectional(topLeft.layoutYProperty());
        topPos.bindBidirectional(top.layoutYProperty());

        leftPos.bindBidirectional(topLeft.layoutXProperty());
        leftPos.bindBidirectional(bottomLeft.layoutXProperty());
        leftPos.bindBidirectional(left.layoutXProperty());

        rightPos.bindBidirectional(topRight.layoutXProperty());
        rightPos.bindBidirectional(bottomRight.layoutXProperty());
        rightPos.bindBidirectional(right.layoutXProperty());

        bottomPos.bindBidirectional(bottomRight.layoutYProperty());
        bottomPos.bindBidirectional(bottomLeft.layoutYProperty());
        bottomPos.bindBidirectional(bottom.layoutYProperty());

        left.layoutYProperty().bindBidirectional(right.layoutYProperty());
        top.layoutXProperty().bindBidirectional(bottom.layoutXProperty());

        bottomPos.addListener((obs, old, val) -> {
            left.layoutYProperty().set(topPos.get() + (val.doubleValue() - topPos.get()) / 2);
            update();
        });

        topPos.addListener((obs, old, val) -> {
            left.layoutYProperty().set(val.doubleValue() + (bottomPos.get() - val.doubleValue()) / 2);
            update();
        });

        rightPos.addListener((obs, old, val) -> {
            top.layoutXProperty().set(leftPos.get() + (val.doubleValue() - leftPos.get()) / 2);
            borderPane.setPrefWidth(Math.abs(val.doubleValue() - leftPos.get()) - 5);
            update();
        });

        leftPos.addListener((obs, old, val) -> {
            top.layoutXProperty().set(val.doubleValue() + (rightPos.get() - val.doubleValue()) / 2);
            borderPane.setLayoutX(Math.min(rightPos.get(), val.doubleValue()) - 5 / 2);
            update();
        });

        // DO NOT UPDATE GROUPS WHILE MOVING
        // WE DO NOT PICK UP NODE ELEMENTS WHILE MOVING!
//        layoutXProperty().addListener(e -> updateGroups());
//        layoutYProperty().addListener(e -> updateGroups());

        title.setClip(mask);

        dragInfo = NodeContext.makeDraggable(title, this);
        dragInfo.getEnableDrag().bind(title.editableProperty().not());

        title.addEventFilter(MouseEvent.MOUSE_PRESSED, this::attemptPress);
        title.addEventHandler(MouseEvent.MOUSE_PRESSED, Event::consume);
        highlight.addEventFilter(MouseEvent.MOUSE_PRESSED, this::attemptPress);

        selected.addListener((obs, old, val) -> {
            NodeCanvas canvas = getCanvas();
            if (val) {
                highlight.toFront();
                controllerColor.set(NodeContext.BACKGROUND_GROUP_SELECTED);
                if (canvas != null) {
                    canvas.getSelectedNodes().add(this);
                    for (NodeCanvasElement node : getElements()) {
                        node.setSelected(true);
                    }
                }
            } else {
                controllerColor.set(NodeContext.BACKGROUND_GROUP);
                if (canvas != null) {
                    canvas.getSelectedNodes().remove(this);
                    for (NodeCanvasElement node : getElements()) {
                        node.setSelected(false);
                    }
                }
            }
        });

        addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            if (!isSelected()) {
                controllerColor.set(NodeContext.BACKGROUND_GROUP_HOVER);
            }
        });

        addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            if (!isSelected()) {
                controllerColor.set(NodeContext.BACKGROUND_GROUP);
            }
        });

        nodes.addListener((ListChangeListener<NodeCanvasElement>) c -> {
            while (c.next()) {
                for (NodeCanvasElement node : new ArrayList<>(c.getAddedSubList())) {
                    if (!node.getGroups().contains(this)) {
                        if (isSelected() && !node.isSelected()) {
                            node.setSelected(true);
                        }
                        node.getGroups().add(this);
                    }
                }
                NodeContext.iterateLater(c.getRemoved(), node -> {
                    if (node.getGroups().contains(this)) {
                        if (node.isSelected() && isSelected()) {
                            boolean selected = false;
                            for (NodeGroup other : node.getGroups()) {
                                if (other.isSelected()) {
                                    selected = true;
                                    break;
                                }
                            }
                            if (!selected) {
                                node.setSelected(false);
                            }
                        }
                        node.getGroups().remove(this);
                    }
                });
            }
        });

        groups.addListener((SetChangeListener<NodeGroup>) c -> {
            if (c.wasRemoved()) {
                if (c.getElementRemoved().isSelected() && !isSelected()) {
                    setSelected(true);
                }
                c.getElementRemoved().getElements().add(this);
            }
            if (c.wasAdded()) {
                if (isSelected() && c.getElementAdded().isSelected()) {
                    boolean selected = false;
                    for (NodeGroup other : getGroups()) {
                        if (other.isSelected()) {
                            selected = true;
                            break;
                        }
                    }
                    if (!selected) {
                        setSelected(false);
                    }
                }
                c.getElementAdded().getElements().remove(this);
            }
        });

        // to trigger listeners
        rightPos.set(50);
        bottomPos.set(50);
    }

    public NodeGroupHighlight getHighlight() {
        return highlight;
    }

    @Override
    public void delete() {
        if (canvas != null) {
            canvas.getGroups().remove(this);
        }
    }

    public SimpleDoubleProperty getTopPos() {
        return topPos;
    }

    public SimpleDoubleProperty getBottomPos() {
        return bottomPos;
    }

    public SimpleDoubleProperty getRightPos() {
        return rightPos;
    }

    public SimpleDoubleProperty getLeftPos() {
        return leftPos;
    }

    public ObjectProperty<Color> groupColorProperty() {
        return groupColor;
    }

    @Override
    public NodeContext.DragInfo getDragInfo() {
        return dragInfo;
    }

    @Override
    public ObservableSet<NodeGroup> getGroups() {
        return groups;
    }

    public ObservableList<NodeCanvasElement> getElements() {
        return nodes;
    }

    private void update() {
        double y = Math.min(topPos.doubleValue(), bottomPos.doubleValue());
        double x = Math.min(leftPos.doubleValue(), rightPos.doubleValue());
        double height = Math.max(topPos.doubleValue(), bottomPos.doubleValue()) - y;
        double width = Math.max(leftPos.doubleValue(), rightPos.doubleValue()) - x;
        borderPane.setLayoutX(x + 5);
        borderPane.setLayoutY(y + 5);
        borderPane.setPrefWidth(width);
        borderPane.setPrefHeight(height);
        updateGroups();
        // TODO update cursors

    }

    protected void updateGroups() {
        for (NodeCanvasElement node : new ArrayList<>(getElements())) {
            if (!isOnBounds(node)) {
                getElements().remove(node);
            }
        }
        NodeCanvas canvas = getCanvas();
        if (canvas != null) {
            for (Node node : canvas.getNodes()) {
                if (isOnBounds(node)) {
                    getElements().add(node);
                }
            }
            for (NodeGroup node : canvas.getGroups()) {
                if (node == this) continue;
                if (isOnBounds(node)) {
                    getElements().add(node);
                }
            }
        }
    }

    void attemptPress(MouseEvent event) {
        if (!isSelected()) {
            title.requestFocus();
            NodeCanvas canvas = getCanvas();
            if (canvas != null && !event.isControlDown()) {
                canvas.getSelectedNodes().clear();
            }
            setSelected(true);
        } else {
            if (event.isControlDown()) {
                boolean selected = false;
                for (NodeGroup group : getGroups()) {
                    if (group.isSelected()) {
                        selected = true;
                        break;
                    }
                }
                if (!selected) {
                    requestFocus(); // remove TextField focus
                    setSelected(false);
                }
            }
        }
        updateGroups();
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    @Override
    public boolean isSelected() {
        return selected.get();
    }

    @Override
    public NodeCanvas getCanvas() {
        return canvas;
    }

    protected void initialize(NodeCanvas canvas) {
        this.canvas = canvas;
    }

    protected void destroy(NodeCanvas canvas) {
        this.canvas = null;
    }

    private StringProperty groupName;

    public StringProperty getGroupName() {
        return groupName;
    }

    public class NodeGroupCorner extends Pane {

        private NodeContext.DragInfo info;
        public NodeGroupCorner(double size, boolean xMovement, boolean yMovement) {
            setHeight(size);
            setWidth(size);
            setPrefWidth(size);
            setPrefHeight(size);
            setMaxWidth(size);
            setMaxHeight(size);
            setMinWidth(size);
            setMinHeight(size);

            setBackground(new Background(new BackgroundFill(NodeContext.BACKGROUND_GROUP, null, null)));
            controllerColor.addListener((obs, old, val) -> {
                setBackground(new Background(new BackgroundFill(val, null, null)));
            });

            info = NodeContext.makeDraggable(this);
            info.getMovementX().set(xMovement);
            info.getMovementY().set(yMovement);
            setPickOnBounds(false);
            addEventFilter(MouseEvent.MOUSE_PRESSED, NodeGroup.this::attemptPress);
            addEventHandler(MouseEvent.MOUSE_PRESSED, Event::consume);
        }

        public NodeContext.DragInfo getDragInfo() {
            return info;
        }
    }

    public class NodeGroupTitle extends TextField {
        private int MAGIC_PADDING = 20;
        private String previousText;

        private Text measuringText = new Text();
        // To allow potential css formatting to work for the text,
        // it is required that text be placed in a Scene,
        // even though we never explicitly use the scene.
        private final Scene measuringScene = new Scene(new Group(measuringText));

        public NodeGroupTitle() {
            groupName = textProperty();
            setPrefWidth(MAGIC_PADDING);
            setMinWidth(MAGIC_PADDING);
            setMaxWidth(USE_PREF_SIZE);
            setPickOnBounds(false);
//            addEventHandler(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event -> {
//                if (isEditable()) {
//                    event.consume();
//                }
//            });
            setStyle("-fx-background-radius: 0 0 10 0;");
            selectionProperty().addListener((obs, old, val) -> {
                if (!isEditable()) {
                    deselect();
                }
            });
            setOnMouseClicked(click -> {
                if (click.getClickCount() >= 2) {
                    setEditable(true);
                }
                click.consume();
            });
            editableProperty().addListener((obs, old, val) -> {
                if (val) {
                    requestFocus();
                    selectAll();
                    setCursor(Cursor.TEXT);
                } else {
                    Parent parent = getParent();
                    if (parent != null) {
                        parent.requestFocus();
                    }
                    setCursor(Cursor.DEFAULT);
                    deselect();
                }
            });
            setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ESCAPE) {
                    setText(previousText);
                    setEditable(false);
                }
            });
            setOnAction(event -> {
                setEditable(false);
                previousText = getText();
                event.consume();
            });
            focusedProperty().addListener((obs, old, val) -> {
                if (!val) {
                    setEditable(false);
                }
            });

            // note if the text in your text field is styled, then you should also apply the
            // a similar style to the measuring text here if you want to get an accurate measurement.

            textProperty().addListener(observable -> {
                measuringText.setText(getText());
                setPrefWidth(measureTextWidth(measuringText) + MAGIC_PADDING);
            });
            setEditable(false);
            setMinWidth(50);
            setText(previousText = "Untitled Group");
            maxWidthProperty().bind(borderPane.widthProperty());
        }

        private double measureTextWidth(Text text) {
            text.applyCss();
            return text.getLayoutBounds().getWidth();
        }
    }

}