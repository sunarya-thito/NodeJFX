package test;

import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.text.*;
import javafx.stage.*;
import thito.nodejfx.Node;
import thito.nodejfx.*;
import thito.nodejfx.parameter.*;
import thito.nodejfx.parameter.type.*;

public class UITest extends Application {

    public static final NodeParameterType[] TYPES = new NodeParameterType[8];

    static int total = 0;
    public static void main(String[] args) {
        System.setProperty("prism.forceGPU", "true");
        UITest.launch(UITest.class, args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
//        NodeEditor editor = new NodeEditor();
//        NodeViewport viewport = editor.getViewport();
//
//        NodeCanvas canvas = viewport.getCanvas();
//        canvas.nodeLinkStyleProperty().set(NodeLinkStyle.BEZIER_STYLE);
//        editor.getCanvas().getSelectionContainer().getMode().set(ToolMode.SELECT);
//
//        NodeContext.resizeFont(Font.font(null, FontWeight.BLACK, FontPosture.ITALIC, 125), 10);
//
//        for (int i = 0; i < 2; i++) {
//            canvas.getNodes().add(createNode(i % 5 + 1));
//        }
//
//        Chest chest = new Chest(canvas);
//        chest.addEventHandler(ScrollEvent.SCROLL, event -> {
//            if (event.getDeltaY() > 0) {
//                chest.rowsProperty().set(chest.rowsProperty().get() + 1);
//            } else {
//                chest.rowsProperty().set(chest.rowsProperty().get() - 1);
//            }
//            event.consume();
//        });
//        Image image = new Image(Chest.class.getResource("DIAMOND.png").toExternalForm());
//        for (ChestSlot slot : chest.getSlots()) {
//            slot.addEventHandler(ScrollEvent.SCROLL, event -> {
//                if (event.getDeltaY() > 0) {
//                    slot.amountProperty().set(slot.amountProperty().get() + 1);
//                } else {
//                    slot.amountProperty().set(slot.amountProperty().get() - 1);
//                }
//                event.consume();
//            });
//            ImageView view = new ImageView(image);
//            view.fitWidthProperty().set(32);
//            view.fitHeightProperty().set(32);
//            slot.amountProperty().addListener((obs, old, val) -> {
//                if (val.intValue() > 0) {
//                    slot.getItemContainer().setCenter(view);
//                } else {
//                    slot.getItemContainer().setCenter(null);
//                }
//            });
//        }
//        canvas.getChestContainer().getChildren().add(chest);
//
//        Scene scene = new Scene(editor, 800, 600);
//        primaryStage.setScene(scene);
//        for (Screen screen : Screen.getScreens()) {
//            if (screen == Screen.getPrimary()) continue;
//            moveToScreen(screen, primaryStage);
//            break;
//        }
//        primaryStage.show();
    }

    public static void moveToScreen(Screen screen, Stage stage) {
        Rectangle2D bounds = screen.getVisualBounds();
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.centerOnScreen();
    }

    Node createNode(int count) {
        Node node = new Node();
        node.getParameters().addAll(
                new LabelParameter("Test"),
                castableType(new StringParameter("String")),
                castableType(new NumberParameter("Number", Double.class)),
                castableType(new EnumParameter<>("Enum", Test.class)),
                castableType(new BooleanParameter("Boolean")),
                new CharacterParameter("Character")
        );
        return node;
    }

    <T extends NodeParameter> T castableType(T parameter) {
        parameter.getInputType().set(JavaParameterType.getCastableType(((JavaParameterType) parameter.getInputType().get()).getType()));
        parameter.getOutputType().set(JavaParameterType.getCastableType(((JavaParameterType) parameter.getOutputType().get()).getType()));
        return parameter;
    }

    public enum Test {
        THIS, IS, AN, EXAMPLE, ENUM;
    }
}
