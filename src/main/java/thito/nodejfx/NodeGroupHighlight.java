package thito.nodejfx;

import javafx.beans.binding.Bindings;
import javafx.scene.shape.Rectangle;

public class NodeGroupHighlight extends Rectangle {

    public NodeGroupHighlight(NodeGroup nodeGroup) {
        xProperty().bind(Bindings.add(nodeGroup.getBorderPane().layoutXProperty(), nodeGroup.layoutXProperty()));
        yProperty().bind(Bindings.add(nodeGroup.getBorderPane().layoutYProperty(), nodeGroup.layoutYProperty()));
        widthProperty().bind(nodeGroup.getBorderPane().widthProperty());
        heightProperty().bind(nodeGroup.getBorderPane().heightProperty());
        setOpacity(0.5);
        fillProperty().bind(nodeGroup.groupColorProperty());
    }

}
