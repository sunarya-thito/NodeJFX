package thito.nodejfx;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class NodeSelectionContainer extends Pane {

    private DoubleProperty
        startX = new SimpleDoubleProperty(),
        startY = new SimpleDoubleProperty(),
        endX = new SimpleDoubleProperty(),
        endY = new SimpleDoubleProperty()
    ;

    private NodeSelection selection;
    private NodeGroup grouping;
    private ObjectProperty<ToolMode> mode = new SimpleObjectProperty<>();
    private Set<NodeCanvasElement> selected = ConcurrentHashMap.newKeySet();
    private NodeCanvas canvas;
    private boolean dragging;
    public NodeSelectionContainer(NodeCanvas canvas) {
        selection = new NodeSelection(canvas);
        setMouseTransparent(true);
        this.canvas = canvas;
    }

    public ObjectProperty<ToolMode> getMode() {
        return mode;
    }

    public boolean isDragging() {
        return dragging;
    }

    public void refresh() {
        double x = Math.min(startX.get(), endX.get());
        double y = Math.min(startY.get(), endY.get());
        double x2 = Math.max(startX.get(), endX.get());
        double y2 = Math.max(startY.get(), endY.get());
        double width = x2 - x;
        double height = y2 - y;
        if (grouping != null) {
            Point2D point = localToParent(x, y);
            Point2D point2 = localToParent(x2, y2);
            x = point.getX();
            y = point.getY();
            x2 = point2.getX();
            y2 = point2.getY();
            grouping.getLeftPos().set(x);
            grouping.getRightPos().set(x2);
            grouping.getTopPos().set(y);
            grouping.getBottomPos().set(y2);
        } else {
            selection.setLayoutX(x);
            selection.setLayoutY(y);
            selection.setPrefWidth(width);
            selection.setPrefHeight(height);
        }
    }

    public void stopDragging() {
        dragging = false;
        getChildren().remove(selection);
        if (grouping != null) {
            grouping = null;
        }
    }

    public NodeCanvas getCanvas() {
        return canvas;
    }

    public void startDragging(double x, double y, boolean addSelection) {
        Point2D point = sceneToLocal(x, y);
        x = point.getX();
        y = point.getY();
        stopDragging();
        dragging = true;
        startX.set(x);
        startY.set(y);
        endX.set(x);
        endY.set(y);
        if (mode.get() == ToolMode.GROUPING) {
            grouping = new NodeGroup();
            getCanvas().getGroups().add(grouping);
            refresh();
            return;
        }
        if (!addSelection) {
            getCanvas().getSelectedNodes().clear();
        }
        refresh();
        getChildren().add(selection);
    }

    public void moveDrag(double x, double y, boolean intersecting) {
        if (!isDragging()) return;
        Point2D point = sceneToLocal(x, y);
        x = point.getX();
        y = point.getY();
        endX.set(x);
        endY.set(y);
        if (grouping == null) {
            refreshSelection(intersecting);
        }
        refresh();
    }

    protected void add(NodeCanvasElement element) {
        if (getCanvas().getSelectedNodes().contains(element)) return;
        selected.add(element);
        getCanvas().getSelectedNodes().add(element);
    }

    protected void remove(NodeCanvasElement element) {
        if (!selected.contains(element)) return;
        selected.remove(element);
        getCanvas().getSelectedNodes().remove(element);
    }

    public void refreshSelection(boolean intersecting) {
        Bounds bounds = selection.localToScene(selection.getBoundsInLocal());
        for (Node node : getCanvas().getNodes()) {
            Bounds target = node.getExactBounds();
            if (intersecting ? bounds.intersects(target) : bounds.contains(target)) {
                add(node);
            } else {
                remove(node);
            }
        }
        for (NodeGroup node : getCanvas().getGroups()) {
            Bounds target = node.getExactBounds();
            if (intersecting ? bounds.intersects(target) : bounds.contains(target)) {
                add(node);
            } else {
                remove(node);
            }
        }
//        for (NodeLink link : getCanvas().getLinkContainer().getLinks()) {
//            if (link instanceof NodeLinked) {
//                NodeLinked.LinkingElement element = ((NodeLinked) link).getLinkingElement();
//                javafx.scene.Node node = element.getComponent();
//                if (node instanceof Shape) {
//                    Shape shape = (Shape) node;
//                    Bounds shapeBounds = shape.getLayoutBounds();
//                    Rectangle selectionShape = selection.getLocalShape();
//                    Shape intersected = Shape.intersect(shape, selectionShape);
//                    Bounds x = intersected.getBoundsInLocal();
//                    if (x.getWidth() > 0 || x.getHeight() > 0) {
//                        if (intersecting) {
//                            inBounds.add(element);
//                        } else {
//                            if (x.getWidth() + 1 >= shapeBounds.getWidth() && x.getHeight() + 1 >= shapeBounds.getHeight()) {
//                                inBounds.add(element);
//                            }
//                        }
//                    }
//                    continue;
//                }
//                Bounds target = ((NodeLinked) link).getLinkingElement().getExactBounds();
//                if (intersecting ? bounds.intersects(target) : bounds.contains(target)) {
//                    inBounds.add(((NodeLinked) link).getLinkingElement());
//                }
//            }
//        }
    }

    public DoubleProperty getStartY() {
        return startY;
    }

    public DoubleProperty getStartX() {
        return startX;
    }

    public DoubleProperty getEndY() {
        return endY;
    }

    public DoubleProperty getEndX() {
        return endX;
    }

    public NodeSelection getSelection() {
        return selection;
    }

}
