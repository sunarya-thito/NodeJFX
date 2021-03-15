package thito.nodejfx;

import javafx.geometry.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;

import java.util.*;

public class NodeEditor extends AnchorPane {
    private NodeViewport viewport;
    private NodeViewportControl control;
    private NodeSelectionContainer selectionContainer;
    private NodeProperties properties = new NodeProperties();
    public NodeEditor() {
        viewport = newViewport();

        selectionContainer = new NodeSelectionContainer(viewport.getCanvas());
        selectionContainer.setManaged(false);

        setTopAnchor(viewport, 0d);
        setBottomAnchor(viewport, 0d);
        setRightAnchor(viewport, 0d);
        setLeftAnchor(viewport, 0d);

        setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.PRIMARY && !event.isControlDown()) {
                viewport.getCanvas().getSelectedNodes().clear();
            }
            event.consume();
        });

        control = newControl();

        setBottomAnchor(control, 20d);
        setLeftAnchor(control, 20d);

        getChildren().addAll(viewport, selectionContainer); // was adding control, but nvm

        addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.isPrimaryButtonDown()) {
                requestFocus();
                getSelectionContainer().startDragging(event.getX(), event.getY(), event.isControlDown());
                event.consume();
            }
        });

        addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            if (event.isPrimaryButtonDown()) {
                double moveSpeed = 5;
                double x = event.getX();
                double y = event.getY();
                double width = getWidth();
                double height = getHeight();
                Pane pane = getViewport().getViewportContainer();
                if (x < 1) {
                    pane.setTranslateX(pane.getTranslateX() + moveSpeed);
                    getSelectionContainer().getStartX().set(getSelectionContainer().getStartX().get() + moveSpeed);
                }
                if (y < 1) {
                    pane.setTranslateY(pane.getTranslateY() + moveSpeed);
                    getSelectionContainer().getStartY().set(getSelectionContainer().getStartY().get() + moveSpeed);
                }
                if (x >= width - 1) {
                    pane.setTranslateX(pane.getTranslateX() - moveSpeed);
                    getSelectionContainer().getStartX().set(getSelectionContainer().getStartX().get() - moveSpeed);
                }
                if (y >= height - 1) {
                    pane.setTranslateY(pane.getTranslateY() - moveSpeed);
                    getSelectionContainer().getStartY().set(getSelectionContainer().getStartY().get() - moveSpeed);
                }
                getSelectionContainer().moveDrag(x, y, event.isShiftDown());
                event.consume();
            }
        });

        addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            getSelectionContainer().stopDragging();
        });

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
            } else if (properties.HOTKEY_ZOOM_MICRO_UP.get().match(event)) {
                getViewport().setScale(getViewport().getScale() + properties.MICRO_ZOOM.get());
                event.consume();
            } else if (properties.HOTKEY_ZOOM_MICRO_DOWN.get().match(event)) {
                getViewport().setScale(getViewport().getScale() - properties.MICRO_ZOOM.get());
                event.consume();
            } else if (properties.HOTKEY_ZOOM_MACRO_UP.get().match(event)) {
                getViewport().setScale(getViewport().getScale() + properties.MACRO_ZOOM.get());
                event.consume();
            } else if (properties.HOTKEY_ZOOM_MACRO_DOWN.get().match(event)) {
                getViewport().setScale(getViewport().getScale() - properties.MACRO_ZOOM.get());
                event.consume();
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

    public NodeSelectionContainer getSelectionContainer() {
        return selectionContainer;
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
