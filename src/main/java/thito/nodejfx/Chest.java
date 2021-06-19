package thito.nodejfx;

import javafx.animation.*;
import javafx.beans.property.*;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.util.*;
import thito.nodejfx.parameter.type.*;

public class Chest extends Pane {
    public static final char COLOR_CHAR = '&';
    static {
        Font.loadFont(Chest.class.getResourceAsStream("1_Minecraft-Regular.otf"), 14);
        Font.loadFont(Chest.class.getResourceAsStream("2_Minecraft-Italic.otf"), 14);
        Font.loadFont(Chest.class.getResourceAsStream("3_Minecraft-Bold.otf"), 14);
        Font.loadFont(Chest.class.getResourceAsStream("4_Minecraft-BoldItalic.otf"), 14);
    }
    private static Font font = Font.font("Minecraft", 18);
    private NodeParameter dummy = new NodeParameter() {
        @Override
        protected void initialize(NodeLink x) {
            super.initialize(x);
            x.getStyle().getComponent().setEffect(null);
        }
    };
    private Pane areaDummy = new Pane();
    private static Image image = new Image(Chest.class.getResource("generic_54.png").toExternalForm());
    private ImageView view = new ImageView();
    private IntegerProperty rows = new SimpleIntegerProperty();
    private int cropTop;
    private int cropBottom = 125;
    private Colorizer title = new Colorizer(font, COLOR_CHAR);
    private TextFlow titleLabel = title.getTextFlow();
    private Label inventory = new Label("Inventory");
    private ChestSlot[] slots = new ChestSlot[9 * 6];
    private NodeCanvas canvas;
    private Node node = new Node();
    private NodeDragListener listener;

    public Chest(NodeCanvas canvas) {
        areaDummy.setPrefHeight(20);
        areaDummy.setPrefWidth(20);
        areaDummy.layoutXProperty().bind(dummy.layoutXProperty().subtract(10));
        areaDummy.layoutYProperty().bind(dummy.layoutYProperty().subtract(10));
        listener = new NodeDragListener(dummy, false, areaDummy);
        dummy.layoutXProperty().bind(widthProperty().subtract(20));
        dummy.layoutYProperty().bind(titleLabel.layoutYProperty().add(titleLabel.heightProperty().divide(2)));
        dummy.initialize(node);
        dummy.getAllowInput().set(false);
        dummy.getOutputType().set(JavaParameterType.getType(String.class));
        dummy.setOutputShape(NodeLinkShape.CIRCLE_SHAPE);
        dummy.getOutputShape().getComponent().setEffect(new DropShadow(5, Color.color(0, 0, 0, 0.5)));
        view.setSmooth(false);
        NodeContext.makeDraggable(this, canvas);
        inventory.setFont(font);
        setBackground(Background.EMPTY);
        rows.addListener(obs -> update());
//        setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
        getChildren().addAll(view, titleLabel, inventory, areaDummy, dummy);
        for (int i = 0; i < slots.length; i++) {
            ChestSlot slot = new ChestSlot(this);
            slot.setLayoutX(16 + 36 * (i % 9));
            slot.setLayoutY(36 + 36 * (i / 9));
            slots[i] = slot;
        }
        rows.set(6);
        title.stringProperty().set("&a&b&c&l&mExample &ktext&r &1Cool Chest &o&nYES!");
        layoutYProperty().addListener(obs -> {
            node.setLayoutY(getLayoutY());
            node.getLayoutY();
        });
        layoutXProperty().addListener(obs -> {
            node.setLayoutX(getLayoutX());
            node.getLayoutX();
        });
        dummy.maxHeightProperty().set(0);
        dummy.maxWidthProperty().set(0);
        dummy.minWidthProperty().set(0);
        dummy.minHeightProperty().set(0);
        titleLabel.setLayoutX(18);
        titleLabel.setLayoutY(11);
        inventory.setLayoutX(18);

    }

    public Pane getAreaDummy() {
        return areaDummy;
    }

    public IntegerProperty rowsProperty() {
        return rows;
    }

    public NodeParameter getDummy() {
        return dummy;
    }

    public void setCanvas(NodeCanvas canvas) {
        this.canvas = canvas;
        node.initialize(canvas);
    }

    public Node getNode() {
        return node;
    }

    public NodeCanvas getCanvas() {
        return canvas;
    }

    public StringProperty titleProperty() {
        return title.stringProperty();
    }

    public Label getInventoryLabel() {
        return inventory;
    }

    public ChestSlot[] getSlots() {
        return slots;
    }

    public void update() {
        for (int i = 0; i < slots.length; i++) {
            int slotY = i / 9;
            if (slotY < rows.get()) {
                if (!getChildren().contains(slots[i])) {
                    slots[i].getDummy().initialize(node);
                    getChildren().add(slots[i]);
                }
            } else {
                slots[i].getDummy().destroy(node);
                getChildren().remove(slots[i]);
            }
        }

        PixelReader reader = image.getPixelReader();
        cropTop = 17 + 18 * rows.get();
        WritableImage result = new WritableImage(176 * 2, 222 * 2 - (cropBottom - cropTop + 1) * 2);
        PixelWriter writer = result.getPixelWriter();
        inventory.setLayoutY((cropTop + 1) * 2);
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                if (j >= cropTop && j < cropBottom) continue;
                int argb = reader.getArgb(i, j);
                int offsetY = j;
                if (j > cropBottom) offsetY = offsetY - cropBottom + cropTop - 1;
                int x = i * 2;
                int y = offsetY * 2;
                if (x >= 0 && y >= 0 && x < result.getWidth() && y < result.getHeight()) {
                    writer.setArgb(x, y, argb);
                    if (x + 1 < result.getWidth()) {
                        writer.setArgb(x + 1, y, argb);
                    }
                    if (y + 1 < result.getHeight()) {
                        writer.setArgb(x, y + 1, argb);
                    }
                    if (x + 1 < result.getWidth() && y + 1 < result.getHeight()) {
                        writer.setArgb(x + 1, y + 1, argb);
                    }
                }
            }
        }
        view.imageProperty().set(result);
    }

}
