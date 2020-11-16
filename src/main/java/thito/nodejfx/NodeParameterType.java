package thito.nodejfx;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;

public interface NodeParameterType {
    NodeParameterType DEFAULT_TYPE = new NodeParameterType() {

        ObjectProperty<Color> color = new ReadOnlyObjectWrapper<>(Color.WHITE);

        @Override
        public String name() {
            return "DEFAULT_TYPE";
        }

        @Override
        public ObjectProperty<Color> inputColorProperty() {
            return color;
        }

        @Override
        public ObjectProperty<Color> outputColorProperty() {
            return color;
        }

        @Override
        public boolean isAssignableFrom(NodeParameterType other) {
            return other == this;
        }

    };

    String name();

    ObjectProperty<Color> inputColorProperty();

    ObjectProperty<Color> outputColorProperty();

    boolean isAssignableFrom(NodeParameterType other);

}
