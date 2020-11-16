package thito.nodejfx;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class NodeSelection extends Pane {

    private NodeCanvas canvas;
    public NodeSelection(NodeCanvas canvas) {
        this.canvas = canvas;
        setBorder(new Border(new BorderStroke(Color.CORNFLOWERBLUE, BorderStrokeStyle.SOLID, null, new BorderWidths(2))));
        Color color = Color.CORNFLOWERBLUE;
        setBackground(new Background(new BackgroundFill(Color.color(color.getRed(), color.getGreen(), color.getBlue(), 0.5f), null, null)));
    }

    public NodeCanvas getCanvas() {
        return canvas;
    }

    /*
    NodeCanvas@5a194579:Point2D [x = 0.0, y = 0.0]
Pane@2c552f59:Point2D [x = 12.239999771118164, y = 1.440000057220459]
NodeViewport@5d938b0a:Point2D [x = 325.239990234375, y = 236.44000244140625]
NodeEditor@49c5470c:Point2D [x = 325.239990234375, y = 236.44000244140625]
     */
    public Rectangle getLocalShape() {
        Rectangle rectangle = new Rectangle(0, 0, getWidth(), getHeight());
        return rectangle;
    }
}
