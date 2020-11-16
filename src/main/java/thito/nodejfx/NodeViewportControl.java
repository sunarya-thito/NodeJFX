package thito.nodejfx;

import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.util.StringConverter;
import thito.nodejfx.control.LinkStyleViewportControl;
import thito.nodejfx.control.ZoomViewportControl;
import thito.nodejfx.internal.ModeButton;

public class NodeViewportControl extends FlowPane {
    private NodeEditor editor;
    public NodeViewportControl(NodeEditor editor) {
        this.editor = editor;
        setPadding(new Insets(5, 15, 5, 15));
        setBackground(new Background(new BackgroundFill(NodeContext.BACKGROUND_CONTROL, new CornerRadii(5), null)));
        setEffect(new DropShadow(5, NodeContext.SHADOW_CONTROL));
        setOpacity(0.5);
        setHgap(10);
        setVgap(5);
        setPrefWrapLength(0);
        setOrientation(Orientation.VERTICAL);

        setAlignment(Pos.CENTER_LEFT);

        setOnMousePressed(Event::consume);

        onMouseEnteredProperty().set(event -> {
            setOpacity(1);
        });

        onMouseExitedProperty().set(event -> {
            setOpacity(0.5);
        });

        initDefaultController();
    }

    public NodeEditor getEditor() {
        return editor;
    }

    public NodeViewport getViewport() {
        return editor.getViewport();
    }

    protected void initDefaultController() {
        getChildren().add(new ZoomViewportControl(getViewport()));
        Button resetPanAndZoomButton = new Button("Reset");
        resetPanAndZoomButton.setOnMouseClicked(event -> {
            getViewport().setScale(100);
            getViewport().getMoveTransform().setX(0);
            getViewport().getMoveTransform().setY(0);
        });
        getChildren().add(resetPanAndZoomButton);
        ComboBox<ToolMode> tools = new ComboBox<>(FXCollections.observableArrayList(ToolMode.values()));
        tools.setValue(ToolMode.SELECT);
        tools.setConverter(new StringConverter<ToolMode>() {
            @Override
            public String toString(ToolMode object) {
                return object.getDisplayName();
            }

            @Override
            public ToolMode fromString(String string) {
                return null;
            }
        });
        editor.getSelectionContainer().getMode().bindBidirectional(tools.valueProperty());
        getChildren().add(tools);
        getChildren().add(new ModeButton("X"));
        getChildren().add(new LinkStyleViewportControl(getViewport()));
    }
}
