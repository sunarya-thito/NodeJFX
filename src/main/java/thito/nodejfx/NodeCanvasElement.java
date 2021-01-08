package thito.nodejfx;

import javafx.collections.ObservableSet;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Parent;

public interface NodeCanvasElement {
    NodeCanvas getCanvas();
    ObservableSet<NodeGroup> getGroups();
    void setSelected(boolean selected);
    boolean isSelected();
    double getLayoutX();
    double getLayoutY();
    void setLayoutX(double x);
    void setLayoutY(double y);
    Parent getParent();
    Point2D sceneToLocal(double x, double y);
    Point2D localToScene(double x, double y);
    void delete();
//    void setDragOffset(double x, double y);
    javafx.scene.Node getComponent();
    Bounds getBoundsInParent();
    Bounds getExactBounds();
    NodeContext.DragInfo getDragInfo();
    ElementState getState();
    void loadState(ElementState state);
}
