package thito.nodejfx;

import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Translate;
import javafx.util.Callback;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public interface NodeContext {
    Color
    BACKGROUND_CANVAS = Color.rgb(35, 35, 35),
    BACKGROUND_CANVAS_GRID = Color.rgb(45, 45, 45),
    BACKGROUND_CANVAS_GRID_MACRO = Color.rgb(25, 25, 25),
    BACKGROUND_NODE = Color.rgb(50, 50, 50, 0.5f),
    BACKGROUND_NODE_SELECTED = Color.rgb(50, 50, 50, 0.7f),
    BACKGROUND_LINKER = Color.rgb(128, 128, 128),
    BACKGROUND_CONTROL = Color.rgb(20, 20, 20),
    BACKGROUND_GROUP = Color.rgb(100, 100, 100),
    BACKGROUND_GROUP_BORDER = Color.rgb(150, 150, 150),
    BACKGROUND_GROUP_SELECTED = Color.WHITE,
    BACKGROUND_GROUP_HOVER = Color.LIGHTBLUE,
//    SHADOW_GROUP_SELECTED = Color.rgb(200, 200, 200),
    SHADOW_NODE = Color.rgb(25, 25, 25),
    SHADOW_CONTROL = Color.rgb(10, 10, 10),
    BACKGROUND_SEPARATOR = Color.rgb(150, 150, 150, 0.5f);
    ;
    Font
            FONT_NODE = Font.font(null, FontWeight.BLACK, -1);
    int
            HEIGHT_NODE = 50;

    static Color randomColor() {
        Random random = new Random();
        return Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble());
    }

    static Color randomMinimumColor(int minR, int minG, int minB, double alpha) {
        Random random = new Random();
        return Color.rgb(
                minR + random.nextInt(255 - minR),
                minG + random.nextInt(255 - minG),
                minB + random.nextInt(255 - minB),
                alpha
        );
    }

    static Color randomBrightColor(double alpha) {
        return randomMinimumColor(100, 100, 100, alpha);
    }
    static DragInfo makeDraggable(javafx.scene.Node node, Translate affected, MouseButton button) {
        final DragInfo dragDelta = new DragInfo(node);
        node.addEventHandler(MouseEvent.MOUSE_PRESSED, me -> {
            if (me.getButton() == button) {
                dragDelta.defaultCursor = node.getCursor();
                dragDelta.x = me.getX();
                dragDelta.y = me.getY();
            }
        });
        node.addEventHandler(MouseEvent.MOUSE_DRAGGED, me -> {
            if (!dragDelta.enableDrag.get()) return;
            if (button == null || me.getButton() == button) {
                node.setCursor(dragDelta.cursor.get());
                if (dragDelta.movementX.get()) {
                    affected.setX(affected.getX() + (me.getX() - dragDelta.x));
                }
                if (dragDelta.movementY.get()) {
                    affected.setY(affected.getY() + (me.getY() - dragDelta.y));
                }
                dragDelta.x = me.getX();
                dragDelta.y = me.getY();
                me.consume();
            }
        });
        node.addEventHandler(MouseEvent.MOUSE_RELEASED, me -> {
            node.setCursor(dragDelta.defaultCursor);
        });
        return dragDelta;
    }
    static DragInfo makeDraggable(javafx.scene.Node node) {
        final DragInfo dragDelta = new DragInfo(node);
        node.addEventHandler(MouseEvent.MOUSE_PRESSED, me -> {
            dragDelta.defaultCursor = node.getCursor();
            dragDelta.x = me.getX();
            dragDelta.y = me.getY();
        });
        node.addEventHandler(MouseEvent.MOUSE_DRAGGED, me -> {
            if (!dragDelta.enableDrag.get()) return;
            node.setCursor(dragDelta.cursor.get());
            if (dragDelta.movementX.get()) {
                node.setLayoutX(node.getLayoutX() + (me.getX() - dragDelta.x));
            }
            if (dragDelta.movementY.get()) {
                node.setLayoutY(node.getLayoutY() + (me.getY() - dragDelta.y));
            }
            me.consume();
        });
        node.addEventHandler(MouseEvent.MOUSE_RELEASED, me -> {
            node.setCursor(dragDelta.defaultCursor);
        });
        return dragDelta;
    }
    static DragInfo makeDraggable(javafx.scene.Node node, NodeCanvasElement element) {
        final DragInfo dragDelta = new DragInfo(node);
        node.addEventHandler(MouseEvent.MOUSE_ENTERED, me -> {
            dragDelta.defaultCursor = node.getCursor();
            node.setCursor(dragDelta.cursor.get());
        });
        node.addEventHandler(MouseEvent.MOUSE_EXITED, me -> {
            node.setCursor(dragDelta.defaultCursor);
        });
        node.addEventHandler(MouseEvent.MOUSE_PRESSED, me -> {
            dragDelta.dragging = true;
            NodeCanvas canvas = element.getCanvas();
            if (canvas != null) {
                for (NodeCanvasElement selected : new ArrayList<>(canvas.getSelectedNodes())) {
                    DragInfo info = selected.getDragInfo();
                    if (info == null || selected.getParent() == null) continue;
                    Point2D loc = selected.getParent().localToScene(selected.getLayoutX(), selected.getLayoutY());
                    info.setOffset(me.getSceneX() - loc.getX(), me.getSceneY() - loc.getY());
                }
            }
        });
        node.addEventHandler(MouseEvent.MOUSE_DRAGGED,  me -> {
            if (!dragDelta.enableDrag.get() || !dragDelta.dragging) return;
            node.setCursor(dragDelta.cursor.get());
            NodeCanvas canvas = element.getCanvas();
            if (canvas != null) {
                for (NodeCanvasElement selected : new ArrayList<>(canvas.getSelectedNodes())) {
                    if (selected.getParent() == null) continue;
                    DragInfo dragInfo = selected.getDragInfo();
                    if (dragInfo == null) continue;
                    Point2D local = selected.getParent().sceneToLocal(me.getSceneX() - dragInfo.x, me.getSceneY() - dragInfo.y);
                    if (dragInfo.movementX.get()) {
                        selected.setLayoutX(local.getX());
                    }
                    if (dragInfo.movementY.get()) {
                        selected.setLayoutY(local.getY());
                    }
                }
            }
            me.consume();
        });
        node.addEventHandler(MouseEvent.MOUSE_RELEASED, me -> {
            dragDelta.dragging = false;
        });
        return dragDelta;
    }

    static java.awt.Color toAWTColor(Color color) {
        return new java.awt.Color((int) (color.getRed() * 255), (int) (color.getGreen() * 255), (int) (color.getBlue() * 255));
    }

    static BufferedImage generateGrid(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        java.awt.Color back = toAWTColor(BACKGROUND_CANVAS);
        java.awt.Color grid = toAWTColor(BACKGROUND_CANVAS_GRID);
        java.awt.Color gridMacro = toAWTColor(BACKGROUND_CANVAS_GRID_MACRO);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (x <= 1 || y <= 1) {
                    image.setRGB(x, y, gridMacro.getRGB());
                } else if (x % (width / 5) == 0 || y % (height / 5) == 0) {
                    image.setRGB(x, y, grid.getRGB());
                } else {
                    image.setRGB(x, y, back.getRGB());
                }
            }
        }
        return image;
    }

    static Font resizeFont(Font font, double size) {
        if (font == null) font = Font.getDefault();
        String style = font.getStyle();
        if (style != null) {
            String[] styleSplit = style.split(" ");
            if (styleSplit.length == 1) {
                return Font.font(font.getFamily(), FontWeight.findByName(styleSplit[0]), size);
            } else if (styleSplit.length == 2) {
                return Font.font(font.getFamily(), FontWeight.findByName(styleSplit[0]), FontPosture.findByName(styleSplit[1]), size);
            }
        }
        return Font.font(font.getFamily(), size);
    }

    static <T> void iterateLater(List<T> list, Consumer<T> iterate) {
        List<T> copiedList = new ArrayList<>(list);
        Platform.runLater(() -> copiedList.forEach(iterate));
    }

    class DragInfo {
        private BooleanProperty enableDrag = new SimpleBooleanProperty(true);
        private Cursor defaultCursor;
        private ObjectProperty<Cursor> cursor = new SimpleObjectProperty<>(Cursor.MOVE);
        private BooleanProperty movementX = new SimpleBooleanProperty(true);
        private BooleanProperty movementY = new SimpleBooleanProperty(true);
        private double x;
        private double y;
        private javafx.scene.Node node;
        private boolean dragging;

        public ObjectProperty<Cursor> getCursor() {
            return cursor;
        }

        public BooleanProperty getMovementX() {
            return movementX;
        }

        public BooleanProperty getMovementY() {
            return movementY;
        }

        public BooleanProperty getEnableDrag() {
            return enableDrag;
        }

        public DragInfo(javafx.scene.Node node) {
            this.node = node;
        }

        public void setOffset(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
}
