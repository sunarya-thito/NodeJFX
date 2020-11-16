package thito.nodejfx.parameter.type;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import thito.nodejfx.NodeContext;
import thito.nodejfx.NodeLinkShape;
import thito.nodejfx.NodeParameterType;
import thito.nodejfx.parameter.converter.TypeCaster;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavaParameterType<T> implements NodeParameterType {

    private static final Map<Class<?>, JavaParameterType<?>> types = new HashMap<>();

    public static JavaParameterType<?> getCastableType(Class<?> type) {
        return types.computeIfAbsent(type, key -> new CastableParameterType<>(type, NodeContext.randomBrightColor(1)));
    }

    public static JavaParameterType<?> getType(Class<?> type) {
        return types.computeIfAbsent(type, key -> new JavaParameterType<>(type, NodeContext.randomBrightColor(1)));
    }

    public static class CastableParameterType<T> extends JavaParameterType<T> {
        public CastableParameterType(Class<T> type, Color color) {
            super(type, color);
        }

        @Override
        public boolean isAssignableFrom(NodeParameterType other) {
            if (other instanceof CastableParameterType) {
                return true;
            }
            return super.isAssignableFrom(other);
        }
    }

    private Class<T> type;
    private SimpleObjectProperty<Color> color = new SimpleObjectProperty<>();

    public JavaParameterType(Class<T> type, Color color) {
        this.type = type;
        this.color.set(color);
    }

    public Class<T> getType() {
        return type;
    }

    @Override
    public String name() {
        return type.getSimpleName() + " ("+type.getPackage().getName()+")";
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
        return other instanceof JavaParameterType
                && getType().isAssignableFrom(((JavaParameterType<?>) other).getType())
                // for casting support
                && ((JavaParameterType<?>) other).getType().isAssignableFrom(getType());
    }

}
