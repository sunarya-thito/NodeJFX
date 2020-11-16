package thito.nodejfx.parameter.converter;

public class CharacterTypeCaster implements TypeCaster<Character> {
    @Override
    public Character fromSafeObject(Object obj) {
        if (obj instanceof Character) return (Character) obj;
        String string = TypeCaster.STRING_TYPE_CASTER.fromSafeObject(obj);
        return string != null && !string.isEmpty() ? string.charAt(0) : (char) 0;
    }

    @Override
    public Object toSafeObject(Character obj) {
        return obj;
    }
}
