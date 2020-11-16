package thito.nodejfx.parameter.converter;

public class EnumTypeCaster<T extends Enum<T>> implements TypeCaster<T> {

    private Class<T> enumClass;
    private T[] elements;

    public EnumTypeCaster(Class<T> enumClass) {
        this.enumClass = enumClass;
        this.elements = enumClass.getEnumConstants();
    }

    protected T get(String name) {
        for (T x : elements) {
            if (x.name().equals(name)) {
                return x;
            }
        }
        return null;
    }

    @Override
    public T fromSafeObject(Object obj) {
        if (enumClass.isInstance(obj)) {
            return enumClass.cast(obj);
        }
        if (obj instanceof String) {
            return get((String) obj);
        }
        return null;
    }

    @Override
    public Object toSafeObject(T obj) {
        return obj == null ? null : obj.name();
    }
}
