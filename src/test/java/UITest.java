import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import thito.nodejfx.*;
import thito.nodejfx.parameter.*;

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

    public static final NodeParameterType[] TYPES = new NodeParameterType[8];

    public static void main(String[] args) {
        UITest.launch(UITest.class, args);
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

//        new JMetro(Style.DARK).setParent(editor);

        Scene scene = new Scene(editor, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
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
