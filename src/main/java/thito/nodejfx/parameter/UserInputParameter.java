package thito.nodejfx.parameter;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import thito.nodejfx.parameter.converter.TypeCaster;

public interface UserInputParameter<T> {
    ObjectProperty<Object> valueProperty();
    BooleanProperty disableInputProperty();
    ObjectProperty<TypeCaster<T>> typeCaster();
}
