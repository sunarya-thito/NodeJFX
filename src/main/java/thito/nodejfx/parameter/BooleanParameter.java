package thito.nodejfx.parameter;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.SetChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import thito.nodejfx.NodeParameter;
import thito.nodejfx.parameter.converter.TypeCaster;
import thito.nodejfx.parameter.type.JavaParameterType;

public class BooleanParameter extends NodeParameter implements UserInputParameter<Boolean> {
    private Label fieldText;
    private CheckBox input;
    private BorderPane box = new BorderPane();
    private ObjectProperty<Object> value = new SimpleObjectProperty<>();
    private ObjectProperty<TypeCaster<Boolean>> typeCaster = new SimpleObjectProperty<>(TypeCaster.BOOLEAN_TYPE_CASTER);
    public BooleanParameter(String fieldName) {
        fieldText = new Label(fieldName);
        input = new CheckBox();
        getContainer().getChildren().add(box);
        BorderPane.setMargin(fieldText, new Insets(0, 20, 0, 0));
        BorderPane.setAlignment(fieldText, Pos.CENTER);
        BorderPane.setAlignment(input, Pos.CENTER_LEFT);
        box.setLeft(fieldText);
        box.setRight(input);
        getInputType().set(JavaParameterType.getCastableType(Boolean.class));
        getOutputType().set(JavaParameterType.getCastableType(Boolean.class));
        TypeCaster.bindBidirectional(value, input.selectedProperty(), typeCaster);
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
    public ObjectProperty<TypeCaster<Boolean>> typeCaster() {
        return typeCaster;
    }

    @Override
    public ObjectProperty<Object> valueProperty() {
        return value;
    }

    public Label getFieldText() {
        return fieldText;
    }

}
