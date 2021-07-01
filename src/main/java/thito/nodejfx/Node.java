package thito.nodejfx;

import javafx.beans.Observable;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.event.Event;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import thito.nodejfx.internal.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.*;

public class Node extends BorderPane implements NodeCanvasElement {

    private SimpleStringProperty titleText = new SimpleStringProperty();
    private SimpleStringProperty subtitleText = new SimpleStringProperty();
    private NodeTitle title = new NodeTitle();
    private SimpleObjectProperty<Color> color = new SimpleObjectProperty<>();
    private ObservableList<NodeParameter> values = FXCollections.observableArrayList();
    private ObservableSet<NodeGroup> groups = FXCollections.observableSet(ConcurrentHashMap.newKeySet());
    private SimpleBooleanProperty selected = new SimpleBooleanProperty();
    private NodeCanvas canvas;
    private NodeContext.DragInfo dragInfo;
    private BooleanProperty reachableInput = new SimpleBooleanProperty();
    private BooleanProperty reachableOutput = new SimpleBooleanProperty();
    private BooleanProperty highlight = new SimpleBooleanProperty();
    private ObjectProperty<Color> shadowProperty = new SimpleObjectProperty<>(NodeContext.SHADOW_NODE);
    private boolean preventDelete;

    private ObjectProperty<Point2D> dropPoint = new SimpleObjectProperty<>(new Point2D(getLayoutX(), getLayoutY()));
    private VBox wrapped;

    public Node() {
        setPickOnBounds(false);
        setBackground(new Background(new BackgroundFill(
                new RadialGradient(0, 0.1, 0, 0, 200, false, CycleMethod.NO_CYCLE,
                        new Stop(0, NodeContext.BACKGROUND_NODE),
                        new Stop(1, NodeContext.BACKGROUND_NODE.darker().darker()))
                , new CornerRadii(5), null)));

        setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(1))));
//        getChildren().add(title);
        setTop(title);

        wrapped = new VBox();

        setCenter(wrapped);
//        getChildren().add(wrapped);

        getParameters().addListener((ListChangeListener<? super NodeParameter>) change -> {
            final List<javafx.scene.Node> list = wrapped.getChildren();
            synchronized (list) {
                while (change.next()) {
                    if (change.wasPermutated()) {
                        List<javafx.scene.Node> cleared = list.subList(change.getFrom(), change.getTo());
                        for (javafx.scene.Node node : cleared) {
                            if (node instanceof NodeParameter) {
                                ((NodeParameter) node).destroy(this);
                            }
                        }
                        cleared.clear();
                        List<? extends NodeParameter> param = change.getList().subList(change.getFrom(), change.getTo());
                        for (NodeParameter par : param) {
                            par.initialize(this);
                        }
                        list.addAll(change.getFrom(), param);
                    } else {
                        if (change.wasRemoved()) {
                            List<javafx.scene.Node> removed = list.subList(change.getFrom(), change.getFrom() + change.getRemovedSize());
                            for (javafx.scene.Node node : removed) {
                                if (node instanceof NodeParameter) {
                                    ((NodeParameter) node).destroy(this);
                                }
                            }
                            removed.clear();
                        }
                        if (change.wasAdded()) {
                            List<? extends NodeParameter> parameters = change.getAddedSubList();
                            for (NodeParameter parameter : parameters) {
                                parameter.initialize(this);
                            }
                            list.addAll(change.getFrom(), parameters);
                        }
                    }
                }
            }
            int index = 0;
            for (NodeParameter n : getParameters()) {
                n.setSeparated(index > 0);
                if (n.insertableProperty().get()) {
                    if (index + 1 < getParameters().size()) {
                        NodeParameter parameter = getParameters().get(index + 1);
                        AnchorPane.setTopAnchor(parameter.getContainer(), 5d);
                    }
                } else {
                    if (index + 1 < getParameters().size()) {
                        NodeParameter parameter = getParameters().get(index + 1);
                        AnchorPane.setTopAnchor(parameter.getContainer(), 0d);
                    }
                }
                index++;
            }
        });

        color.addListener((obs, oldVal, newVal) -> {
            title.setColor(newVal);
        });

        addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (dragToFront()) {
                preventDelete = true;
                toFront();
                preventDelete = false;
            }
            if (!isSelected()) {
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
                        setSelected(false);
                    }
                }
            }
            updateGroups();
        });

        addEventHandler(MouseEvent.MOUSE_PRESSED, Event::consume);

        dragInfo = NodeContext.makeDraggable(title, this);

        selected.addListener((obs, oldVal, newVal) -> {
            NodeCanvas canvas = getCanvas();
            if (newVal) {
                if (canvas != null) {
                    canvas.getSelectedNodes().add(this);
                }
                setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(1)))); // jfx bug here, when you change the border width to something else than 1, it will resize the children body to its compact size
                setBackground(new Background(new BackgroundFill(
                        new RadialGradient(0, 0.1, 0, 0, 200, false, CycleMethod.NO_CYCLE,
                                new Stop(0, NodeContext.BACKGROUND_NODE_SELECTED),
                                new Stop(1, NodeContext.BACKGROUND_NODE_SELECTED.darker().darker()))
                        , new CornerRadii(5), null)));
            } else {
                if (canvas != null) {
                    canvas.getSelectedNodes().remove(this);
                }
                setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(1))));
                setBackground(new Background(new BackgroundFill(
                        new RadialGradient(0, 0.1, 0, 0, 200, false, CycleMethod.NO_CYCLE,
                                new Stop(0, NodeContext.BACKGROUND_NODE),
                                new Stop(1, NodeContext.BACKGROUND_NODE.darker().darker()))
                        , new CornerRadii(5), null)));
            }
        });

        groups.addListener((SetChangeListener<NodeGroup>) c -> {
            if (c.wasRemoved()) {
                if (isSelected() && c.getElementRemoved().isSelected()) {
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
                c.getElementRemoved().getElements().remove(this);
            }
            if (c.wasAdded()) {
                if (c.getElementAdded().isSelected() && !isSelected()) {
                    setSelected(true);
                }
                c.getElementAdded().getElements().add(this);
            }
        });

        // default styling
        DropShadow shadow = new DropShadow(10, NodeContext.SHADOW_NODE);
        shadow.colorProperty().bind(shadowProperty);
        ColorAdjust adjust = new ColorAdjust();
        adjust.brightnessProperty().bind(Bindings.when(reachableInput.or(highlight)).then(0).otherwise(-0.5));
        shadow.setInput(adjust);
        setEffect(shadow);
        setMinWidth(150);

        // initialize default values
        colorProperty().set(NodeContext.randomBrightColor(0.5f));

        layoutXProperty().addListener(this::updateGroups);
        layoutYProperty().addListener(this::updateGroups);
        widthProperty().addListener(this::updateGroups);
        heightProperty().addListener(this::updateGroups);
    }

    private void updateLinks() {
        for (NodeParameter parameter : getParameters()) {
            parameter.updateLinks();
        }
    }

    public boolean isPreventDelete() {
        return preventDelete;
    }

    public VBox getParameterContainer() {
        return wrapped;
    }

    public ObjectProperty<Color> shadowProperty() {
        return shadowProperty;
    }

    public BooleanProperty highlightProperty() {
        return highlight;
    }

    public BooleanProperty reachableInputProperty() {
        return reachableInput;
    }

    public BooleanProperty reachableOutputProperty() {
        return reachableOutput;
    }

    public ObjectProperty<Point2D> dropPointProperty() {
        return dropPoint;
    }

    @Override
    public ElementState getState() {
        ElementState state = new ElementState();
        state.setLayoutX(getLayoutX());
        state.setLayoutY(getLayoutY());
        return state;
    }

    @Override
    public void loadState(ElementState state) {
        setLayoutX(state.getLayoutX());
        setLayoutY(state.getLayoutY());
    }

    @Override
    public javafx.scene.Node getComponent() {
        return this;
    }

    @Override
    public Bounds getExactBounds() {
        // due to effects, bounds (BoundingBox) must be created manually
        return localToScene(new BoundingBox(0, 0, getWidth(), getHeight()));
    }

    @Override
    public NodeContext.DragInfo getDragInfo() {
        return dragInfo;
    }

    public NodeTitle getTitleNode() {
        return title;
    }

    @Override
    public void delete() {
        if (canvas != null) {
            canvas.getNodes().remove(this);
        }
    }

    void updateGroups() {
        updateGroups(null);
    }

    void updateGroups(Observable obs) {
        updateLinks();
        for (NodeGroup group : new ArrayList<>(getGroups())) {
            if (!group.isOnBounds(this)) {
                getGroups().remove(group);
                group.getElements().remove(this);
            }
        }
        NodeCanvas canvas = getCanvas();
        if (canvas != null) {
            for (NodeGroup group : canvas.getGroups()) {
                if (group.isOnBounds(this)) {
                    getGroups().add(group);
                    group.getElements().add(this);
                }
            }
        }
    }

    @Override
    public ObservableSet<NodeGroup> getGroups() {
        return groups;
    }

    @Override
    public NodeCanvas getCanvas() {
        return canvas;
    }

    public boolean isSelected() {
        return selected.get();
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    public List<Node> getRelatedInput() {
        List<Node> nodes = new ArrayList<>();
        for (NodeParameter parameter : getParameters()) {
            for (NodeParameter input : parameter.getUnmodifiableInputLinks()) {
                if (!nodes.contains(input.getNode())) {
                    nodes.add(input.getNode());
                }
            }
        }
        return nodes;
    }

    public List<Node> getRelatedOutput() {
        List<Node> nodes = new ArrayList<>();
        for (NodeParameter parameter : getParameters()) {
            for (NodeParameter input : parameter.getUnmodifiableOutputLinks()) {
                if (!nodes.contains(input.getNode())) {
                    nodes.add(input.getNode());
                }
            }
        }
        return nodes;
    }

    protected void updatePosition() {
        List<Node> collide = collideWithOtherNodes();
        // TODO Avoid collision?
    }

    public void initialize(NodeCanvas canvas) {
        this.canvas = canvas;
        if (isSelected()) {
            canvas.getSelectedNodes().add(this);
        }
        updateGroups();
    }

    protected void destroy(NodeCanvas canvas) {
        this.canvas = null;
    }

    protected List<Node> collideWithOtherNodes() {
        NodeCanvas canvas = getCanvas();
        List<Node> collide = new ArrayList<>();
        if (canvas != null) {
            for (Node node : canvas.getNodes()) {
                if (node != this && node.getBoundsInParent().intersects(getBoundsInParent())) {
                    collide.add(node);
                }
            }
        }
        return collide;
    }

    public boolean dragToFront() {
        return true;
    }

    public ObservableList<NodeParameter> getParameters() {
        return values;
    }

    public StringProperty titleProperty() {
        return titleText;
    }

    public ObjectProperty<Color> colorProperty() {
        return color;
    }

    public String getTitle() {
        return titleProperty().get();
    }

    public void setTitle(String title) {
        titleProperty().set(title);
    }

    public StringProperty subtitleProperty() {
        return subtitleText;
    }

    public Color getColor() {
        return colorProperty().get();
    }

    public void setColor(Color color) {
        colorProperty().set(color);
    }

    public class NodeTitle extends BorderPane {
        private Label title = new Label();
//        private Label subtitle = new Label();
        private Color color; // simplifies

        public NodeTitle() {
            title.textProperty().bind(titleProperty());
//            Tooltip tooltip = new Tooltip();
//            tooltip.textProperty().bind(subtitleProperty());
//            title.setTooltip(tooltip);
//            subtitle.textProperty().bind(subtitleProperty());
//            title.setStyle("-fx-font: 14 System");
            title.setFont(Font.font("system", 14));
            title.setTextAlignment(TextAlignment.CENTER);
            title.setAlignment(Pos.CENTER);
            setPickOnBounds(false);
            title.setTextFill(Color.WHITE);
            title.setFont(NodeContext.FONT_NODE);
//            subtitle.setTextFill(Color.WHITE);
//            subtitle.setFont(NodeContext.FONT_NODE);
            this.setPadding(new Insets(7, 20, 7, 20));
            this.setHeight(NodeContext.HEIGHT_NODE);
            setCenter(title);
//            setBottom(subtitle);
//            BorderPane.setAlignment(subtitle, Pos.CENTER);
        }

        public Label getLabel() {
            return title;
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
            setBackground(new Background(new BackgroundFill(new LinearGradient(0, 0, 1, 0, true, CycleMethod.REPEAT, new Stop(0, color.brighter().brighter()), new Stop(0.5, color), new Stop(1, color.darker().darker())), new CornerRadii(5, 5, 0, 0, false), null)));
        }

        public String getTitle() {
            return title.getText();
        }

        public void setTitle(String text) {
            title.setText(text);
        }
    }

}
