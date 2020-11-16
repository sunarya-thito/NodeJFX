package thito.nodejfx.internal;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;

public class ModeButton extends Button {
    private ContextMenu menu;
    public ModeButton() {
        super();
        initialize();
    }

    public ModeButton(String text) {
        super(text);
        initialize();
    }

    public ModeButton(String text, Node graphic) {
        super(text, graphic);
        initialize();
    }

    protected void initialize() {
        menu = new ContextMenu();
        menu.getItems().add(new MenuItem("Test"));
        setOnDragDetected(event -> {
            System.out.println("drag detected");
        });
        setOnDragDone(event -> {
            System.out.println("drag done");
        });
        setOnDragEntered(event -> {
            System.out.println("drag entered");
        });
        setOnDragOver(event -> {
            System.out.println("drag over");
        });
        setOnDragExited(event -> {
            System.out.println("drag exited");
        });
    }

    protected boolean show() {
        if (menu.isShowing()) return false;
        Point2D point = localToScreen(0, 0);
        menu.show(this, point.getX() + getWidth(), point.getY());
        return true;
    }
}
