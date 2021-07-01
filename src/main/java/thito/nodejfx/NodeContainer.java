package thito.nodejfx;

import javafx.collections.*;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

public class NodeContainer extends Pane {
    private ObservableList<Node> nodes = FXCollections.observableArrayList();
    public NodeContainer(NodeCanvas canvas) {
        setPickOnBounds(false);
        setManaged(false);
        getNodes().addListener((ListChangeListener<Node>) c -> {
            while (c.next()) {
                if (c.wasRemoved()) {
                    for (Node node : c.getRemoved()) {
                        if (node instanceof thito.nodejfx.Node) {
                            getChildren().remove(node);
                            canvas.destroy((thito.nodejfx.Node) node);
                            canvas.getSelectedNodes().remove(node);
                            ((thito.nodejfx.Node) node).getGroups().clear();
                        }
                    }
                }
                if (c.wasAdded()) {
                    for (Node node : c.getAddedSubList()) {
                        if (node instanceof thito.nodejfx.Node) {
                            getChildren().add(node);
                            ((thito.nodejfx.Node) node).initialize(canvas);
                            canvas.prepare((thito.nodejfx.Node) node);
                            ((thito.nodejfx.Node) node).updateGroups();
                        }
                    }
                }
            }
        });
    }

    public ObservableList<Node> getNodes() {
        return nodes;
    }
}
