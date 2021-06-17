package thito.nodejfx.internal;

import javafx.beans.property.*;

import java.util.concurrent.atomic.*;

public class SuperBinding {
    public static void bindBidirectional(Property property, Property other, Class<?> hint) {
        AtomicBoolean updating = new AtomicBoolean();
        property.addListener((obs, old, val) -> {
            if (updating.get()) return;
            updating.set(true);
            other.setValue(cast(val, hint));
            updating.set(false);
        });
        other.addListener((obs, old, val) -> {
            if (updating.get()) return;
            updating.set(true);
            property.setValue(cast(val, hint));
            updating.set(false);
        });
    }

    public static Object cast(Object source, Class<?> hint) {
        if (hint == Long.class || hint == long.class) {
            if (source instanceof Number) {
                return ((Number) source).longValue();
            }
            if (source != null) {
                try {
                    return Long.valueOf(source.toString());
                } catch (Throwable t) {
                }
            }
            return 0L;
        }
        if (hint == Integer.class || hint == int.class) {
            if (source instanceof Number) {
                return ((Number) source).intValue();
            }
            if (source != null) {
                try {
                    return Integer.valueOf(source.toString());
                } catch (Throwable t) {
                }
            }
            return 0;
        }
        if (hint == Double.class || hint == double.class) {
            if (source instanceof Number) {
                return ((Number) source).doubleValue();
            }
            if (source != null) {
                try {
                    return Double.valueOf(source.toString());
                } catch (Throwable t) {
                }
            }
            return 0d;
        }
        if (hint == Float.class || hint == float.class) {
            if (source instanceof Number) {
                return ((Number) source).floatValue();
            }
            if (source != null) {
                try {
                    return Float.valueOf(source.toString());
                } catch (Throwable t) {
                }
            }
            return 0f;
        }
        if (hint == Short.class || hint == short.class) {
            if (source instanceof Number) {
                return ((Number) source).shortValue();
            }
            if (source != null) {
                try {
                    return Short.valueOf(source.toString());
                } catch (Throwable t) {
                }
            }
            return Short.valueOf((short) 0);
        }
        if (hint == Byte.class || hint == byte.class) {
            if (source instanceof Number) {
                return ((Number) source).byteValue();
            }
            if (source != null) {
                try {
                    return Byte.valueOf(source.toString());
                } catch (Throwable t) {
                }
            }
            return Byte.valueOf((byte)0);
        }
        if (hint == String.class) {
            if (source != null) {
                return source.toString();
            } else {
                return null;
            }
        }
        if (hint == Number.class) {
            if (source instanceof Number) {
                return source;
            }
            return 0;
        }
        return source;
    }
}
