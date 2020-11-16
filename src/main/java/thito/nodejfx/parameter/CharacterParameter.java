package thito.nodejfx.parameter;

import javafx.beans.property.*;
import javafx.collections.SetChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.util.StringConverter;
import thito.nodejfx.NodeParameter;
import thito.nodejfx.parameter.converter.TypeCaster;
import thito.nodejfx.parameter.type.JavaParameterType;

public class CharacterParameter extends NodeParameter implements UserInputParameter<Character> {
    private Label fieldText;
    private TextField input;
    private BorderPane box = new BorderPane();
    private ObjectProperty<Object> value = new SimpleObjectProperty<>();
    private ObjectProperty<TypeCaster<Character>> typeCaster = new SimpleObjectProperty<>(TypeCaster.CHARACTER_TYPE_CASTER);
    public CharacterParameter(String fieldName) {
        fieldText = new Label(fieldName);
        input = new TextField();
        getContainer().getChildren().add(box);
        BorderPane.setMargin(fieldText, new Insets(0, 20, 0, 0));
        BorderPane.setAlignment(fieldText, Pos.CENTER);
        BorderPane.setAlignment(input, Pos.CENTER_LEFT);
        box.setLeft(fieldText);
        box.setRight(input);
        getInputType().set(JavaParameterType.getCastableType(Character.class));
        getOutputType().set(JavaParameterType.getCastableType(Character.class));
        input.textProperty().bindBidirectional(value, new StringConverter<Object>() {
            @Override
            public String toString(Object object) {
                return TypeCaster.toString(typeCaster.get().fromSafeObject(object));
            }

            @Override
            public Object fromString(String string) {
                return typeCaster.get().fromSafeObject(string);
            }
        });
        getUnmodifiableInputLinks().addListener((SetChangeListener<NodeParameter>) change -> {
            if (change.wasRemoved()) {
                valueProperty().unbind();
            }
            if (change.wasAdded()) {
                NodeParameter parameter = change.getElementAdded();
                if (parameter instanceof UserInputParameter) {
                    valueProperty().bind(((UserInputParameter) parameter).valueProperty());
                }
            }
            input.setDisable(!change.getSet().isEmpty());
        });
        getMultipleInputAssigner().set(false);
        getMultipleOutputAssigner().set(true);
    }

    @Override
    public ObjectProperty<TypeCaster<Character>> typeCaster() {
        return typeCaster;
    }

    @Override
    public ObjectProperty<Object> valueProperty() {
        return value;
    }

    public Label getFieldText() {
        return fieldText;
    }

    public TextField getInput() {
        return input;
    }
}
