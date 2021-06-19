package thito.nodejfx;

import javafx.animation.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.util.*;

import java.util.*;

public class ObfuscatedText extends Text {
    static char[] obfuscation = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
    };
    private StringProperty wrapped = new SimpleStringProperty();
    private Timeline timeline = new Timeline(new KeyFrame(Duration.millis(16), event -> obfuscate()));

    public ObfuscatedText() {
        timeline.setCycleCount(-1);
        ChangeListener<Boolean> show = (obs, old, val) -> {
            if (val) {
                timeline.play();
            } else {
                timeline.stop();
            }
        };
        ChangeListener<Window> win = (obs, old, val) -> {
            if (old != null) {
                old.showingProperty().removeListener(show);
            }
            if (val == null) {
                timeline.stop();
            } else {
                val.showingProperty().addListener(show);
                if (val.isShowing()) {
                    timeline.play();
                }
            }
        };
        sceneProperty().addListener((obs, old, val) -> {
            if (old != null) {
                Window x = old.getWindow();
                if (x != null) x.showingProperty().removeListener(show);
                old.windowProperty().removeListener(win);
            }
            if (val == null) {
                timeline.stop();
            } else {
                val.windowProperty().addListener(win);
                if (val.getWindow() != null && val.getWindow().isShowing()) {
                    timeline.play();
                }
            }
        });
        if (getScene() != null) {
            getScene().windowProperty().addListener(win);
            if (getScene().getWindow() != null && getScene().getWindow().isShowing()) {
                timeline.play();
            }
        }
    }

    private void obfuscate() {
        char[] chars = new char[wrapped.get().length()];
        Random random = new Random();
        for (int i = 0; i < chars.length; i++) {
            if (!Character.isAlphabetic(wrapped.get().charAt(i))) {
                chars[i] = wrapped.get().charAt(i);
                continue;
            }
            chars[i] = obfuscation[random.nextInt(obfuscation.length)];
        }
        setText(new String(chars));
    }

    public StringProperty wrappedProperty() {
        return wrapped;
    }
}
