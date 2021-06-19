package thito.nodejfx;

import javafx.collections.*;
import javafx.scene.Node;
import javafx.scene.layout.*;

public class ChestContainer extends Pane {

    public ChestContainer(NodeCanvas canvas) {
        setPickOnBounds(false);
        setManaged(false);
        getChildren().addListener((ListChangeListener<Node>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (Node added : c.getAddedSubList()) {
                        if (added instanceof Chest) {
                            ((Chest) added).setCanvas(canvas);
                        }
                    }
                }
                if (c.wasRemoved()) {
                    for (Node removed : c.getRemoved()) {
                        if (removed instanceof Chest) {
                            ((Chest) removed).setCanvas(null);
                        }
                    }
                }
            }
        });
    }

}
