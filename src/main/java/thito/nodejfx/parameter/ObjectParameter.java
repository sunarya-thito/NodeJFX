package thito.nodejfx.parameter;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.paint.*;
import thito.nodejfx.NodeParameter;
import thito.nodejfx.parameter.converter.TypeCaster;
import thito.nodejfx.parameter.type.JavaParameterType;

public class ObjectParameter extends NodeParameter implements UserInputParameter<Object> {
    private ObjectProperty<Object> value = new SimpleObjectProperty<>();
    private ObjectProperty<TypeCaster<Object>> typeCaster = new SimpleObjectProperty<>(TypeCaster.PASSTHROUGH_TYPE_CASTER);
    private BooleanProperty disable = new SimpleBooleanProperty();
    private Label label;
    public ObjectParameter(String text) {
        this();
        label.setTextFill(Color.WHITE);
        label.setText(text);
    }

    public ObjectParameter() {
        this.label = new Label();
        getContainer().getChildren().add(label);
        getInputType().set(JavaParameterType.getType(Object.class));
        getOutputType().set(JavaParameterType.getType(Object.class));
    }

    public Label getLabel() {
        return label;
    }

    @Override
    public BooleanProperty disableInputProperty() {
        return disable;
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
