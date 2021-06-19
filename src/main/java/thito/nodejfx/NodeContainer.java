package thito.nodejfx;

import javafx.scene.layout.Pane;

public class NodeContainer extends Pane {
    public NodeContainer() {
        setPickOnBounds(false);
        setManaged(false);
    }
}
