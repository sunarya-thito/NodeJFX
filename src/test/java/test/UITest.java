package test;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.stage.Stage;
import thito.nodejfx.*;
import thito.nodejfx.Node;
import thito.nodejfx.event.*;
import thito.nodejfx.parameter.*;
import thito.nodejfx.parameter.type.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class UITest extends Application {

//    public static NodeParameterType newType(String name, Color col) {
//        return new NodeParameterType() {
//            private SimpleObjectProperty<Color> color = new SimpleObjectProperty<>(col);
//            @Override
//            public String name() {
//                return name;
//            }
//
//            @Override
//            public boolean allowMultipleAssigner() {
//                return true;
//            }
//
//            @Override
//            public SimpleObjectProperty<Color> colorProperty() {
//                return color;
//            }
//
//            @Override
//            public boolean isAssignableFrom(NodeParameterType other) {
//                return true;
//            }
//        };
//    }

    public static <T, X> Iterable<T> collectAll(Iterable<X> x, Function<X, Iterable<T>> collector) {
        return () -> new Iterator<T>() {
            private Iterator<T> current;
            private Iterator<X> iterator = x.iterator();
            private Iterator<T> current() {
                if (current == null) {
                    if (iterator.hasNext()) {
                        System.out.println("next iterator");
                        current = collector.apply(iterator.next()).iterator();
                    }
                } else {
                    if (!current.hasNext()) {
                        if (iterator.hasNext()) {
                            System.out.println("next iterator");
                            current = collector.apply(iterator.next()).iterator();
                        } else {
                            current = null;
                        }
                    }
                }
                return current;
            }
            @Override
            public boolean hasNext() {
                Iterator<T> current = current();
                return current != null && current.hasNext();
            }

            @Override
            public void remove() {
                Iterator<T> current = current();
                if (current != null) {
                    current.next();
                }
            }

            @Override
            public T next() {
                return current().next();
            }
        };
    }
    public static final NodeParameterType[] TYPES = new NodeParameterType[8];

    static int total = 0;
    public static void main(String[] args) {
        System.setProperty("prism.forceGPU", "true");
        UITest.launch(UITest.class, args);
//        List<DS> x = new ArrayList<>();
//        for (int i = 0; i < 5; i++) x.add(new DS());
//        for (String all : collectAll(x, c -> c.allString)) {
//            System.out.println(all);
//        }
    }

    public static class DS {
        List<String> allString = new ArrayList<>();

        public DS() {
            for (int i = 0; i < 5; i++) allString.add(total+++ " : " +UUID.randomUUID().toString());
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        NodeEditor editor = new NodeEditor();
        NodeViewport viewport = editor.getViewport();

        NodeCanvas canvas = viewport.getCanvas();
        canvas.nodeLinkStyleProperty().set(NodeLinkStyle.BEZIER_STYLE);
        editor.getCanvas().getSelectionContainer().getMode().set(ToolMode.SELECT);

        NodeContext.resizeFont(Font.font(null, FontWeight.BLACK, FontPosture.ITALIC, 125), 10);

        for (int i = 0; i < 2; i++) {
            canvas.getNodes().add(createNode(i % 5 + 1));
        }

        Chest chest = new Chest(canvas);
        chest.addEventHandler(ScrollEvent.SCROLL, event -> {
            if (event.getDeltaY() > 0) {
                chest.rowsProperty().set(chest.rowsProperty().get() + 1);
            } else {
                chest.rowsProperty().set(chest.rowsProperty().get() - 1);
            }
            event.consume();
        });
        Image image = new Image(Chest.class.getResource("DIAMOND.png").toExternalForm());
        for (ChestSlot slot : chest.getSlots()) {
            slot.addEventHandler(ScrollEvent.SCROLL, event -> {
                if (event.getDeltaY() > 0) {
                    slot.amountProperty().set(slot.amountProperty().get() + 1);
                } else {
                    slot.amountProperty().set(slot.amountProperty().get() - 1);
                }
                event.consume();
            });
            ImageView view = new ImageView(image);
            view.fitWidthProperty().set(32);
            view.fitHeightProperty().set(32);
            slot.amountProperty().addListener((obs, old, val) -> {
                if (val.intValue() > 0) {
                    slot.getItemContainer().setCenter(view);
                } else {
                    slot.getItemContainer().setCenter(null);
                }
            });
        }
        canvas.getChestContainer().getChildren().add(chest);

        Scene scene = new Scene(editor, 800, 600);
        primaryStage.setScene(scene);
        for (Screen screen : Screen.getScreens()) {
            if (screen == Screen.getPrimary()) continue;
            moveToScreen(screen, primaryStage);
            break;
        }
        primaryStage.show();
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
