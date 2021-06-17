package thito.nodejfx.parameter;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.SetChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.*;
import javafx.util.StringConverter;
import thito.nodejfx.*;
import thito.nodejfx.internal.*;
import thito.nodejfx.parameter.converter.TypeCaster;
import thito.nodejfx.parameter.type.JavaParameterType;

public class NumberParameter extends NodeParameter implements UserInputParameter<Number> {

    private ObjectProperty<Object> value = new SimpleObjectProperty<>(0);
    private ObjectProperty<TypeCaster<Number>> typeCaster = new SimpleObjectProperty<>(TypeCaster.NUMBER_TYPE_CASTER);

    private Label fieldText;
    private Spinner<Number> input;
    private BorderPane box = new BorderPane();

    public NumberParameter(String fieldName, Class<?> typeNumber) {
        fieldText = new Label(fieldName);
        fieldText.setTextFill(Color.WHITE);
        input = new Spinner<>();
        input.setValueFactory(SpinnerValueFactoryKit.getValueFactory(typeNumber));
        input.setEditable(true);
        BorderPane.setMargin(fieldText, new Insets(0, 20, 0, 0));
        BorderPane.setAlignment(fieldText, Pos.CENTER);
        BorderPane.setAlignment(input, Pos.CENTER_LEFT);
        box.setLeft(fieldText);
        box.setRight(input);
        getContainer().getChildren().add(box);
        getInputType().set(JavaParameterType.getType(Number.class));
        getOutputType().set(JavaParameterType.getType(Number.class));
        SuperBinding.bindBidirectional(input.getValueFactory().valueProperty(), value, typeNumber);
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
    public ObjectProperty<Object> valueProperty() {
        return value;
    }

    @Override
    public ObjectProperty<TypeCaster<Number>> typeCaster() {
        return typeCaster;
    }
}
