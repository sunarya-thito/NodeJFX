package thito.nodejfx.parameter.converter;

public class StringTypeCaster implements TypeCaster<String> {
    @Override
    public String fromSafeObject(Object obj) {
        String string = obj == null ? null : obj instanceof String ? (String) obj : obj.toString();
        if (string != null) {
            string = string.replace("\\\\", "\\");
        }
        return string;
    }

    @Override
    public Object toSafeObject(String obj) {
        return obj;
    }
}
