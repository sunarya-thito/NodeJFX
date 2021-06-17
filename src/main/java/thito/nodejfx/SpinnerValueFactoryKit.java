package thito.nodejfx;

import javafx.scene.control.*;
import javafx.util.*;

public class SpinnerValueFactoryKit {

    public static SpinnerValueFactory getValueFactory(Class<?> type) {
        if (type == long.class || type == Long.class) {
            return new LongValue();
        }
        if (type == double.class || type == Double.class) {
            return new DoubleValue();
        }
        if (type == float.class || type == Float.class) {
            return new FloatValue();
        }
        if (type == short.class || type == Short.class) {
            return new ShortValue();
        }
        if (type == byte.class || type == Byte.class) {
            return new ByteValue();
        }
        return new IntValue();
    }

    public static class LongValue extends SpinnerValueFactory<Long> {
        public LongValue() {
            setValue(0L);
            setConverter(new StringConverter<Long>() {
                @Override
                public String toString(Long object) {
                    return Long.toString(object);
                }

                @Override
                public Long fromString(String string) {
                    try {
                        int radix = 10;
                        if (string.startsWith("0x")) {
                            radix = 16;
                            string = string.substring(2);
                        } else if (string.startsWith("0o")) {
                            radix = 8;
                            string = string.substring(2);
                        } else if (string.startsWith("0b")) {
                            radix = 2;
                            string = string.substring(2);
                        }
                        return Long.valueOf(string, radix);
                    } catch (Throwable t) {
                        return getValue();
                    }
                }
            });
        }

        @Override
        public void decrement(int steps) {
            setValue(getValue() - steps);
        }

        @Override
        public void increment(int steps) {
            setValue(getValue() + steps);
        }
    }

    public static class DoubleValue extends SpinnerValueFactory<Double> {
        public DoubleValue() {
            setValue(0d);
            setConverter(new StringConverter<Double>() {
                @Override
                public String toString(Double object) {
                    return Double.toString(object);
                }

                @Override
                public Double fromString(String string) {
                    try {
                        return Double.valueOf(string);
                    } catch (Throwable t) {
                        return getValue();
                    }
                }
            });
        }

        @Override
        public void decrement(int steps) {
            setValue(getValue() - steps);
        }

        @Override
        public void increment(int steps) {
            setValue(getValue() + steps);
        }
    }

    public static class FloatValue extends SpinnerValueFactory<Float> {
        public FloatValue() {
            setValue(0f);
            setConverter(new StringConverter<Float>() {
                @Override
                public String toString(Float object) {
                    return Float.toString(object);
                }

                @Override
                public Float fromString(String string) {
                    try {
                        return Float.valueOf(string);
                    } catch (Throwable t) {
                        return getValue();
                    }
                }
            });
        }

        @Override
        public void decrement(int steps) {
            setValue(getValue() - steps);
        }

        @Override
        public void increment(int steps) {
            setValue(getValue() + steps);
        }
    }

    public static class IntValue extends SpinnerValueFactory<Integer> {
        public IntValue() {
            setValue(0);
            setConverter(new StringConverter<Integer>() {
                @Override
                public String toString(Integer object) {
                    return Integer.toString(object);
                }

                @Override
                public Integer fromString(String string) {
                    try {
                        int radix = 10;
                        if (string.startsWith("0x")) {
                            radix = 16;
                            string = string.substring(2);
                        } else if (string.startsWith("0o")) {
                            radix = 8;
                            string = string.substring(2);
                        } else if (string.startsWith("0b")) {
                            radix = 2;
                            string = string.substring(2);
                        }
                        return Integer.valueOf(string, radix);
                    } catch (Throwable t) {
                        return getValue();
                    }
                }
            });
        }

        @Override
        public void decrement(int steps) {
            setValue(getValue() - steps);
        }

        @Override
        public void increment(int steps) {
            setValue(getValue() + steps);
        }
    }

    public static class ByteValue extends SpinnerValueFactory<Byte> {
        public ByteValue() {
            setValue((byte) 0);
            setConverter(new StringConverter<Byte>() {
                @Override
                public String toString(Byte object) {
                    return Integer.toString(object);
                }

                @Override
                public Byte fromString(String string) {
                    try {
                        int radix = 10;
                        if (string.startsWith("0x")) {
                            radix = 16;
                            string = string.substring(2);
                        } else if (string.startsWith("0o")) {
                            radix = 8;
                            string = string.substring(2);
                        } else if (string.startsWith("0b")) {
                            radix = 2;
                            string = string.substring(2);
                        }
                        return Byte.valueOf(string, radix);
                    } catch (Throwable t) {
                        return getValue();
                    }
                }
            });
        }

        @Override
        public void decrement(int steps) {
            setValue((byte)(getValue() - steps));
        }

        @Override
        public void increment(int steps) {
            setValue((byte)(getValue() + steps));
        }
    }

    public static class ShortValue extends SpinnerValueFactory<Short> {
        public ShortValue() {
            setValue((short) 0);
            setConverter(new StringConverter<Short>() {
                @Override
                public String toString(Short object) {
                    return Integer.toString(object);
                }

                @Override
                public Short fromString(String string) {
                    try {
                        int radix = 10;
                        if (string.startsWith("0x")) {
                            radix = 16;
                            string = string.substring(2);
                        } else if (string.startsWith("0o")) {
                            radix = 8;
                            string = string.substring(2);
                        } else if (string.startsWith("0b")) {
                            radix = 2;
                            string = string.substring(2);
                        }
                        return Short.valueOf(string, radix);
                    } catch (Throwable t) {
                        return getValue();
                    }
                }
            });
        }

        @Override
        public void decrement(int steps) {
            setValue((short)(getValue() - steps));
        }

        @Override
        public void increment(int steps) {
            setValue((short)(getValue() + steps));
        }
    }

}
