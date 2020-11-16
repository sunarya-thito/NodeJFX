package thito.nodejfx.parameter;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.SetChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.BorderPane;
import javafx.util.StringConverter;
import thito.nodejfx.NodeParameter;
import thito.nodejfx.parameter.converter.TypeCaster;
import thito.nodejfx.parameter.type.JavaParameterType;

public class NumberParameter extends NodeParameter implements UserInputParameter<Number> {

    private ObjectProperty<Object> value = new SimpleObjectProperty<>();
    private ObjectProperty<TypeCaster<Number>> typeCaster = new SimpleObjectProperty<>(TypeCaster.NUMBER_TYPE_CASTER);

    private Label fieldText;
    private Spinner<Number> input;
    private BorderPane box = new BorderPane();
    public NumberParameter(String fieldName) {
        fieldText = new Label(fieldName);
        input = new Spinner<>(Integer.MIN_VALUE, Integer.MAX_VALUE, 1);
        input.setEditable(true);
        BorderPane.setMargin(fieldText, new Insets(0, 20, 0, 0));
        BorderPane.setAlignment(fieldText, Pos.CENTER);
        BorderPane.setAlignment(input, Pos.CENTER_LEFT);
        box.setLeft(fieldText);
        box.setRight(input);
        getContainer().getChildren().add(box);
        getInputType().set(JavaParameterType.getCastableType(Number.class));
        getOutputType().set(JavaParameterType.getCastableType(Number.class));
        TypeCaster.bindBidirectional(value, input.getValueFactory().valueProperty(), new SimpleObjectProperty<>(new TypeCaster<Number>() {
            @Override
            public Number fromSafeObject(Object obj) {
                return typeCaster.get().fromSafeObject(obj).intValue();
            }

            @Override
            public Object toSafeObject(Number obj) {
                return typeCaster.get().toSafeObject(obj);
            }
        }));
        input.getValueFactory().setConverter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return TypeCaster.toString(typeCaster.get().toSafeObject(object));
            }

            @Override
            public Number fromString(String string) {
                return typeCaster.get().fromSafeObject(string).intValue();
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
    public ObjectProperty<Object> valueProperty() {
        return value;
    }

    @Override
    public ObjectProperty<TypeCaster<Number>> typeCaster() {
        return typeCaster;
    }
}
