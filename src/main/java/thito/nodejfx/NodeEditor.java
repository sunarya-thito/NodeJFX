package thito.nodejfx;

import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;

import java.util.*;

public class NodeEditor extends AnchorPane {
    public static <T extends Region> T clip(T region) {
        Rectangle rectangle = new Rectangle();
        rectangle.widthProperty().bind(region.widthProperty());
        rectangle.heightProperty().bind(region.heightProperty());
        region.clipProperty().set(rectangle);
        return region;
    }
    private NodeViewport viewport;
    private NodeViewportControl control;
    private NodeProperties properties = new NodeProperties();
    public NodeEditor() {
        viewport = newViewport();

        setCache(true);
        setCacheShape(true);
        setCacheHint(CacheHint.SPEED);

        setTopAnchor(viewport, 0d);
        setBottomAnchor(viewport, 0d);
        setRightAnchor(viewport, 0d);
        setLeftAnchor(viewport, 0d);

        clip(this);
        clip(viewport);

        viewport.maxHeightProperty().bind(heightProperty());
        viewport.maxWidthProperty().bind(widthProperty());

        addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.getButton() == MouseButton.PRIMARY && !event.isControlDown()) {
                viewport.getCanvas().getSelectedNodes().clear();
            }
        });

        control = newControl();

        setBottomAnchor(control, 20d);
        setLeftAnchor(control, 20d);

        getChildren().addAll(viewport); // was adding control, but nvm

        addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            Pane dummyNode = getViewport().getViewportContainer();
            if (properties.HOTKEY_DELETE.get().match(event)) {
                deleteSelected();
                event.consume();
            } else if (properties.HOTKEY_PAN_MICRO_UP.get().match(event)) {
                dummyNode.setTranslateY(dummyNode.getTranslateY() - properties.MICRO_PAN.get());
                event.consume();
            } else if (properties.HOTKEY_PAN_MICRO_DOWN.get().match(event)) {
                dummyNode.setTranslateY(dummyNode.getTranslateY() + properties.MICRO_PAN.get());
                event.consume();
            } else if (properties.HOTKEY_PAN_MICRO_LEFT.get().match(event)) {
                dummyNode.setTranslateX(dummyNode.getTranslateX() - properties.MICRO_PAN.get());
                event.consume();
            } else if (properties.HOTKEY_PAN_MICRO_RIGHT.get().match(event)) {
                dummyNode.setTranslateX(dummyNode.getTranslateX() + properties.MICRO_PAN.get());
                event.consume();
            } else if (properties.HOTKEY_PAN_MACRO_UP.get().match(event)) {
                dummyNode.setTranslateY(dummyNode.getTranslateY() - properties.MACRO_PAN.get());
                event.consume();
            } else if (properties.HOTKEY_PAN_MACRO_DOWN.get().match(event)) {
                dummyNode.setTranslateY(dummyNode.getTranslateY() + properties.MACRO_PAN.get());
                event.consume();
            } else if (properties.HOTKEY_PAN_MACRO_LEFT.get().match(event)) {
                dummyNode.setTranslateX(dummyNode.getTranslateX() - properties.MACRO_PAN.get());
                event.consume();
            } else if (properties.HOTKEY_PAN_MACRO_RIGHT.get().match(event)) {
                dummyNode.setTranslateX(dummyNode.getTranslateX() + properties.MACRO_PAN.get());
                event.consume();
//            } else if (properties.HOTKEY_ZOOM_MICRO_UP.get().match(event)) {
//                getViewport().setScale(getViewport().getScale() + properties.MICRO_ZOOM.get());
//                event.consume();
//            } else if (properties.HOTKEY_ZOOM_MICRO_DOWN.get().match(event)) {
//                getViewport().setScale(getViewport().getScale() - properties.MICRO_ZOOM.get());
//                event.consume();
//            } else if (properties.HOTKEY_ZOOM_MACRO_UP.get().match(event)) {
//                getViewport().setScale(getViewport().getScale() + properties.MACRO_ZOOM.get());
//                event.consume();
//            } else if (properties.HOTKEY_ZOOM_MACRO_DOWN.get().match(event)) {
//                getViewport().setScale(getViewport().getScale() - properties.MACRO_ZOOM.get());
//                event.consume();
            } else if (properties.HOTKEY_SELECT_ALL.get().match(event)) {
                getCanvas().getSelectedNodes().addAll(getCanvas().getNodes());
                getCanvas().getSelectedNodes().addAll(getCanvas().getGroups());
                event.consume();
            }
        });
    }

    public Point2D getLocationAt(double x, double y) {
        return getCanvas().getNodeContainer().parentToLocal(getCanvas().parentToLocal(getViewport().parentToLocal(x, y)));
    }

    public void deleteSelected() {
        for (NodeCanvasElement element : new ArrayList<>(getViewport().getCanvas().getSelectedNodes())) {
            element.delete();
        }
    }

    public NodeProperties getNodeProperties() {
        return properties;
    }

    public void setProperties(NodeProperties properties) {
        this.properties = properties;
    }

    public NodeViewport getViewport() {
        return viewport;
    }

    public NodeCanvas getCanvas() {
        return getViewport().getCanvas();
    }

    protected NodeViewportControl newControl() {
        return new NodeViewportControl(this);
    }

    protected NodeViewport newViewport() {
        return new NodeViewport();
    }
}
