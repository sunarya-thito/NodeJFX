package thito.nodejfx.parameter.converter;

public class NumberTypeCaster implements TypeCaster<Number> {

    @Override
    public Number fromSafeObject(Object obj) {
        if (obj instanceof Number) {
            return (Number) obj;
        }
        if (obj instanceof Character) {
            return (int) ((Character) obj).charValue();
        }
        if (obj instanceof String) {
            String string = (String) obj;
            try {
                if (string.contains(".")) {
                    return Double.parseDouble(string);
                } else if (string.startsWith("0x")) {
                    return Long.valueOf(string, 16);
                } else if (string.startsWith("0b")) {
                    return Long.valueOf(string, 2);
                } else {
                    return Long.valueOf(string);
                }
            } catch (Throwable t) {
            }
        }
        if (obj instanceof Boolean) {
            return (Boolean) obj ? 1 : 0;
        }
        if (obj instanceof Enum) {
            return ((Enum<?>) obj).ordinal();
        }
        return 0;
    }

    @Override
    public Object toSafeObject(Number obj) {
        return obj;
    }
}
