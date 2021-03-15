package thito.nodejfx.parameter.converter;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public interface TypeCaster<T> {
    TypeCaster<String> STRING_TYPE_CASTER = new StringTypeCaster();
    TypeCaster<Number> NUMBER_TYPE_CASTER = new NumberTypeCaster();
    TypeCaster<Boolean> BOOLEAN_TYPE_CASTER = new BooleanTypeCaster();
    TypeCaster<Character> CHARACTER_TYPE_CASTER = new CharacterTypeCaster();
    TypeCaster<Object> PASSTHROUGH_TYPE_CASTER = new TypeCaster<Object>() {
        @Override
        public Object fromSafeObject(Object obj) {
            return obj;
        }

        @Override
        public Object toSafeObject(Object obj) {
            return obj;
        }
    };

    static <T> TypeCaster<T> checkedTypeCaster(Class<T> type) {
        return new TypeCaster<T>() {
            @Override
            public T fromSafeObject(Object obj) {
                return type.isInstance (obj) ? type.cast(obj) : null;
            }

            @Override
            public Object toSafeObject(T obj) {
                return obj;
            }
        };
    }

    T fromSafeObject(Object obj);
    Object toSafeObject(T obj);
    static <T> void bindBidirectional(ObjectProperty<Object> object, Property<T> value, ObjectProperty<TypeCaster<T>> caster) {
        AtomicBoolean changing = new AtomicBoolean();
        object.set(caster.get().toSafeObject(value.getValue()));
        object.addListener((obs, old, val) -> {
            if (changing.get()) return;
            changing.set(true);
            value.setValue(caster.get().fromSafeObject(val));
            changing.set(false);
        });
        value.addListener((obs, old, val) -> {
            if (changing.get()) return;
            changing.set(true);
            object.set(caster.get().toSafeObject(val));
            changing.set(false);
        });
    }
    static String toString(Object object) {
        return object == null ? "<no value>" : object.toString();
    }
}
