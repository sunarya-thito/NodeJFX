package thito.nodejfx.parameter.type;

import javafx.beans.property.*;
import javafx.scene.paint.*;
import thito.nodejfx.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

public class CompoundType implements NodeParameterType {

    private Set<Class<?>> classes = new HashSet<>();

    private static int countDimensions(GenericArrayType type) {
        Type x = type.getGenericComponentType();
        if (x instanceof GenericArrayType) {
            return countDimensions((GenericArrayType) x) + 1;
        }
        return 1;
    }

    private static Class<?> objectArray(int dimensions) {
        char[] cx = new char[dimensions];
        Arrays.fill(cx, '[');
        try {
            return Class.forName(new String(cx)+"Ljava.lang.Object;", false, null);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public CompoundType scan(Type type) {
        if (type instanceof WildcardType) {
            for (Type upper : ((WildcardType) type).getUpperBounds()) {
                if (upper instanceof Class) {
                    addClass((Class<?>) upper);
                } else {
                    addClass(Object.class);
                }
            }
            return this;
        }
        if (type instanceof Class) {
            addClass((Class<?>) type);
            return this;
        }
        if (type instanceof ParameterizedType) {
            if (((ParameterizedType) type).getRawType() instanceof Class) {
                addClass((Class<?>) ((ParameterizedType) type).getRawType());
                return this;
            }
        }
        if (type instanceof GenericArrayType) {
            addClass(objectArray(countDimensions((GenericArrayType) type)));
            return this;
        }
        addClass(Object.class);
        return this;
    }

    public Set<Class<?>> getClasses() {
        return classes;
    }

    public void addClass(Class<?> type) {
        Color color = JavaParameterType.getType(type).inputColorProperty().get();
        Color current = this.color.get();
        if (current == null) {
            this.color.set(color);
            classes.add(type);
            return;
        }
        double r = (current.getRed() + color.getRed()) / 2;
        double g = (current.getGreen() + color.getGreen()) / 2;
        double b = (current.getBlue() + color.getBlue()) / 2;
        this.color.set(Color.color(
                Math.min(1, Math.max(0, r)),
                Math.min(1, Math.max(0, g)),
                Math.min(1, Math.max(0, b))
        ));
        classes.add(type);
    }

    private SimpleObjectProperty<Color> color = new SimpleObjectProperty();

    @Override
    public String name() {
        return classes.stream().map(x -> x.getName()).collect(Collectors.joining(" & "));
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
    public boolean isAssignableFrom(NodeParameterType nodeParameterType) {
        if (nodeParameterType instanceof JavaParameterType) {
            for (Class<?> cl : classes) {
                if (isAssignableFrom(((JavaParameterType<?>) nodeParameterType).getType(), cl) || isAssignableFrom(cl, ((JavaParameterType<?>) nodeParameterType).getType())) {
                    return true;
                }
            }
        }
        if (nodeParameterType instanceof CompoundType) {
            for (Class<?> cl : classes) {
                for (Class<?> cl2 : ((CompoundType) nodeParameterType).classes) {
                    if (isAssignableFrom(cl, cl2) || isAssignableFrom(cl2, cl)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isAssignableFrom(Class<?> target, Class<?> from) {
        return target.isAssignableFrom(from) || (from == String.class && (target.isPrimitive() || Number.class.isAssignableFrom(target) || target == Boolean.class || target == Character.class));
    }
}
