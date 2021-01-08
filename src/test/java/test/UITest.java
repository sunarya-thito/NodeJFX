package test;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.stage.Stage;
import thito.nodejfx.*;
import thito.nodejfx.parameter.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
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
//        UITest.launch(UITest.class, args);
        List<DS> x = new ArrayList<>();
        for (int i = 0; i < 5; i++) x.add(new DS());
        for (String all : collectAll(x, c -> c.allString)) {
            System.out.println(all);
        }
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

        NodeContext.resizeFont(Font.font(null, FontWeight.BLACK, FontPosture.ITALIC, 125), 10);

        for (int i = 0; i < 5; i++) {
            canvas.getNodes().add(createNode(i % 5 + 1));
        }

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
                new StringParameter("String"),
                new NumberParameter("Number"),
                new EnumParameter<>("Enum", Test.class),
                new BooleanParameter("Boolean"),
                new CharacterParameter("Character")
        );
        return node;
    }

    public enum Test {
        THIS, IS, AN, EXAMPLE, ENUM;
    }
}