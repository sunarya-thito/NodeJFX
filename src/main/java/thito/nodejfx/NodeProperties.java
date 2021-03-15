package thito.nodejfx;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

public class NodeProperties {
    public ObjectProperty<KeyCombination>
    HOTKEY_DELETE = keys("DELETE"),
    HOTKEY_PAN_MICRO_UP = keys("UP"),
    HOTKEY_PAN_MICRO_DOWN = keys("DOWN"),
    HOTKEY_PAN_MICRO_LEFT = keys("LEFT"),
    HOTKEY_PAN_MICRO_RIGHT = keys("RIGHT"),
    HOTKEY_PAN_MACRO_UP = keys("Ctrl + UP"),
    HOTKEY_PAN_MACRO_DOWN = keys("Ctrl + DOWN"),
    HOTKEY_PAN_MACRO_LEFT = keys("Ctrl + LEFT"),
    HOTKEY_PAN_MACRO_RIGHT = keys("Ctrl + RIGHT"),
    HOTKEY_ZOOM_MICRO_UP = keys("Alt + UP"),
    HOTKEY_ZOOM_MICRO_DOWN = keys("Alt + DOWN"),
    HOTKEY_ZOOM_MACRO_UP = keys("Alt + Ctrl + UP"),
    HOTKEY_ZOOM_MACRO_DOWN = keys("Alt + Ctrl + DOWN"),
    HOTKEY_SELECT_ALL = keys("Ctrl + A");

    public IntegerProperty
    MICRO_PAN = new SimpleIntegerProperty(10),
    MACRO_PAN = new SimpleIntegerProperty(50),
    MICRO_ZOOM = new SimpleIntegerProperty(2),
    MACRO_ZOOM = new SimpleIntegerProperty(10);
    static SimpleObjectProperty<KeyCombination> keys(String name) {
//        System.out.println(new KeyCodeCombination(KeyCode.E, KeyCombination.ModifierValue.DOWN, KeyCombination.ModifierValue.UP, KeyCombination.ModifierValue.ANY, KeyCombination.ModifierValue.DOWN, KeyCombination.ModifierValue.UP).getName());
        return new SimpleObjectProperty<>(KeyCodeCombination.keyCombination(name));
    }
}
