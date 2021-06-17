package thito.nodejfx.parameter;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.scene.*;
import javafx.scene.control.*;
import thito.nodejfx.parameter.converter.TypeCaster;

public interface UserInputParameter<T> {
    ObjectProperty<Object> valueProperty();
    BooleanProperty disableInputProperty();
    ObjectProperty<TypeCaster<T>> typeCaster();
    void setName(String name);
    Label getLabel();
    Node getInputComponent();
}
