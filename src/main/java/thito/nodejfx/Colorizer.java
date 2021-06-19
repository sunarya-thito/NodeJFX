package thito.nodejfx;

import javafx.beans.property.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;

import java.util.*;

public class Colorizer {
    private StringProperty string = new SimpleStringProperty();
    private TextFlow flow = new TextFlow();
    private char colorChar;
    private Font font;

    public Colorizer(Font font, char colorChar) {
        this.font = font;
        this.colorChar = colorChar;
        stringProperty().addListener((obs, old, val) -> {
            flow.getChildren().clear();
            if (val != null) {
                update();
            }
        });
    }

    public StringProperty stringProperty() {
        return string;
    }

    public TextFlow getTextFlow() {
        return flow;
    }

    private int index;

    private void update() {
        index = 0;
        TextColor lastColor = TextColor.WHITE;
        boolean bold = false;
        boolean italic = false;
        boolean underline = false;
        boolean strikethrough = false;
        while (index < string.get().length()) {
            TextColor color = eatColor();
            List<Style> styles = new ArrayList<>();
            Style style;
            while ((style = eatStyle()) != null) styles.add(style);
            String text = eatText();
            if (!text.isEmpty()) {
                Text component = new Text(text);
                component.setFont(font);
                if (color == null) {
                    component.setFill(lastColor.color);
                } else {
                    bold = false;
                    italic = false;
                    underline = false;
                    strikethrough = false;
                    component.setFill(color.color);
                }
                for (Style st : styles) {
                    if (st == Style.RESET) {
                        component = new Text(text);
                        component.setFill((color = TextColor.WHITE).color);
                        bold = false;
                        italic = false;
                        underline = false;
                        strikethrough = false;
                    } else if (st == Style.BOLD) {
                        bold = true;
                    } else if (st == Style.ITALIC) {
                        italic = true;
                    } else if (st == Style.STRIKETHROUGH) {
                        strikethrough = true;
                    } else if (st == Style.UNDERLINE) {
                        underline = true;
                    } else if (st == Style.MAGIC) {
                        Paint fill = component.getFill();
                        component = new ObfuscatedText();
                        component.setText(text);
                        component.setFill(fill);
                        ((ObfuscatedText) component).wrappedProperty().set(text);
                    }
                }
                if (underline) {
                    component.setUnderline(true);
                }
                if (strikethrough) {
                    component.setStrikethrough(true);
                }
                if (bold) {
                    if (italic) {
                        component.setFont(Font.font(font.getFamily(), FontWeight.BOLD, FontPosture.ITALIC, font.getSize()));
                    } else {
                        component.setFont(Font.font(font.getFamily(), FontWeight.BOLD, FontPosture.REGULAR, font.getSize()));
                    }
                } else {
                    if (italic) {
                        component.setFont(Font.font(font.getFamily(), FontWeight.NORMAL, FontPosture.ITALIC, font.getSize()));
                    } else {
                        component.setFont(Font.font(font.getFamily(), FontWeight.NORMAL, FontPosture.REGULAR, font.getSize()));
                    }
                }
                flow.getChildren().add(component);
            }
            if (color != null) {
                lastColor = color;
            }
        }
    }

    private TextColor eatColor() {
        if (index + 1 < string.get().length()) {
            char c = string.get().charAt(index);
            if (c == colorChar) {
                index++;
                TextColor color = TextColor.byChar(string.get().charAt(index));
                if (color == null) {
                    Style style = Style.byChar(string.get().charAt(index));
                    if (style != null) {
                        index--;
                        return null;
                    }
                }
                index++;
                return color;
            }
        }
        return null;
    }

    private Style eatStyle() {
        if (index + 1 < string.get().length()) {
            char c = string.get().charAt(index);
            if (c == colorChar) {
                Style style = Style.byChar(string.get().charAt(index + 1));
                if (style != null) index += 2;
                return style;
            }
        }
        return null;
    }

    private String eatText() {
        String string = "";
        for (int i = index; i < this.string.get().length(); i++) {
            char c = this.string.get().charAt(i);
            if (c == colorChar && i + 1 < this.string.get().length()) {
                break;
            }
            string += c;
            index++;
        }
        return string;
    }

    static Color color(int rgb) {
        return Color.rgb((rgb >> 16) & 255, (rgb >> 8) & 255, rgb & 255);
    }

    public enum TextColor {
        BLACK('0', 0),
        DARK_BLUE('1', 0x0000AA),
        DARK_GREEN('2', 0x00AA00),
        DARK_AQUA('3', 0x00AAAA),
        DARK_RED('4', 0xAA0000),
        DARK_PURPLE('5', 0xAA00AA),
        GOLD('6', 0xFFAA00),
        GRAY('7', 0xAAAAAA),
        DARK_GRAY('8', 0x555555),
        BLUE('9', 0x5555FF),
        GREEN('a', 0x55FF55),
        AQUA('b', 0x55FFFF),
        RED('c', 0xFF5555),
        LIGHT_PURPLE('d', 0xFF55FF),
        YELLOW('e', 0xFFFF55),
        WHITE('f', 0xFFFFFF);
        static TextColor[] VALUES = values();
        private char ch;
        private Color color;
        TextColor(char ch, int color) {
            this.ch = ch;
            this.color = color(color);
        }
        public static TextColor byChar(char c) {
            for (TextColor color : VALUES) if (color.ch == c) return color;
            return null;
        }
    }

    public enum Style {
        MAGIC('k'),
        BOLD('l'),
        STRIKETHROUGH('m'),
        UNDERLINE('n'),
        ITALIC('o'),
        RESET('r');
        static Style[] VALUES = values();
        private char ch;
        Style(char ch) {
            this.ch = ch;
        }
        public static Style byChar(char c) {
            for (Style color : VALUES) if (color.ch == c) return color;
            return null;
        }
    }
}
