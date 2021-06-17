package thito.nodejfx.parameter;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.SetChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.*;
import thito.nodejfx.NodeParameter;
import thito.nodejfx.parameter.converter.TypeCaster;
import thito.nodejfx.parameter.type.JavaParameterType;

public class StringParameter extends NodeParameter implements UserInputParameter<String> {
    private Label fieldText;
    private TextField input;
    private BorderPane box = new BorderPane();
    private ObjectProperty<Object> value = new SimpleObjectProperty<>();
    private ObjectProperty<TypeCaster<String>> typeCaster = new SimpleObjectProperty<>(TypeCaster.STRING_TYPE_CASTER);

    public StringParameter(String fieldName) {
        fieldText = new Label(fieldName);
        fieldText.setTextFill(Color.WHITE);
        input = new TextField();
        getContainer().getChildren().add(box);
        BorderPane.setMargin(fieldText, new Insets(0, 20, 0, 0));
        BorderPane.setAlignment(fieldText, Pos.CENTER);
        BorderPane.setAlignment(input, Pos.CENTER_LEFT);
        box.setLeft(fieldText);
        box.setRight(input);
        getInputType().set(JavaParameterType.getType(String.class));
        getOutputType().set(JavaParameterType.getType(String.class));
        TypeCaster.bindBidirectional(value, input.textProperty(), typeCaster);
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
    public Node getInputComponent() {
        return input;
    }

    @Override
    public void setName(String name) {
        fieldText.setText(name);
    }

    @Override
    public Label getLabel() {
        return fieldText;
    }

    @Override
    public BooleanProperty disableInputProperty() {
        return input.disableProperty();
    }

    @Override
    public ObjectProperty<TypeCaster<String>> typeCaster() {
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
