package thito.nodejfx.parameter;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import thito.nodejfx.NodeParameter;
import thito.nodejfx.parameter.converter.TypeCaster;
import thito.nodejfx.parameter.type.JavaParameterType;

public class ObjectParameter extends NodeParameter implements UserInputParameter<Object> {
    private ObjectProperty<Object> value = new SimpleObjectProperty<>();
    private ObjectProperty<TypeCaster<Object>> typeCaster = new SimpleObjectProperty<>(TypeCaster.PASSTHROUGH_TYPE_CASTER);
    private Label label;
    public ObjectParameter(String text) {
        this();
        label.setText(text);
    }

    public ObjectParameter() {
        this.label = new Label();
        getContainer().getChildren().add(label);
        getInputType().set(JavaParameterType.getCastableType(Object.class));
        getOutputType().set(JavaParameterType.getCastableType(Object.class));
    }

    public Label getLabel() {
        return label;
    }

    @Override
    protected Pos defaultAlignment() {
        return Pos.CENTER;
    }

    @Override
    public ObjectProperty<Object> valueProperty() {
        return value;
    }

    @Override
    public ObjectProperty<TypeCaster<Object>> typeCaster() {
        return typeCaster;
    }
}
