package thito.nodejfx.parameter.converter;

public class BooleanTypeCaster implements TypeCaster<Boolean> {
    @Override
    public Boolean fromSafeObject(Object obj) {
        if (obj instanceof Boolean) {
            return (Boolean) obj;
        }
        if (obj instanceof String) {
            return Boolean.parseBoolean((String) obj);
        }
        if (obj instanceof Number) {
            return ((Number) obj).intValue() == 1;
        }
        return false;
    }

    @Override
    public Object toSafeObject(Boolean obj) {
        return obj;
    }
}
