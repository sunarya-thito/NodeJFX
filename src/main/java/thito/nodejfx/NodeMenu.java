package thito.nodejfx;

import javafx.collections.ListChangeListener;
import javafx.collections.SetChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;

public class NodeMenu {
    private NodeViewport viewport;
    private ContextMenu rightClick;
    private double rightLocX, rightLocY;
    public NodeMenu(NodeViewport viewport) {
        this.viewport = viewport;
        rightClick = new ContextMenu();
        Menu addItem = new Menu("New");

        {
            MenuItem addGroup = new MenuItem("Group");
            addGroup.setOnAction(event -> {
                Point2D point = viewport.getCanvas().sceneToLocal(rightLocX, rightLocY);
                NodeGroup group = new NodeGroup();
                group.getTopPos().set(point.getY());
                group.getLeftPos().set(point.getX());
                group.getRightPos().set(point.getX() + 100);
                group.getBottomPos().set(point.getY() + 100);
                viewport.getCanvas().getGroups().add(group);
            });
            addItem.getItems().add(addGroup);
        }

        MenuItem deleteItem = new MenuItem("Delete Selected");
        deleteItem.setOnAction(event -> deleteSelected());

        viewport.getCanvas().getSelectedNodes().addListener((SetChangeListener<NodeCanvasElement>) c -> {
            if (c.getSet().isEmpty()) {
                deleteItem.setDisable(true);
            } else {
                deleteItem.setDisable(false);
            }
        });
        deleteItem.setDisable(true); // selected items are empty by default

        rightClick.getItems().addAll(addItem, deleteItem);

        viewport.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            rightClick.hide();
        });
        viewport.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                rightLocX = event.getSceneX();
                rightLocY = event.getSceneY();
                rightClick.show(viewport, event.getScreenX(), event.getScreenY());
            }
        });
    }

    public NodeViewport getViewport() {
        return viewport;
    }

    public void deleteSelected() {
        for (NodeCanvasElement element : new ArrayList<>(getViewport().getCanvas().getSelectedNodes())) {
            element.delete();
        }
    }
}
