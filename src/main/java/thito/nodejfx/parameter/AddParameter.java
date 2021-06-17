package thito.nodejfx.parameter;

import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.paint.*;
import thito.nodejfx.*;
import thito.nodejfx.internal.*;

public class AddParameter extends NodeParameter {

    private CrossButton button = new CrossButton().asAdd();
    public AddParameter() {
        button.setMaxHeight(15);
        button.setMaxWidth(15);
        button.setPrefHeight(15);
        button.setPrefWidth(15);
        getContainer().setPadding(new Insets(10));
        getContainer().getChildren().add(button);
        getAllowInput().set(false);
        getAllowOutput().set(false);
    }

    public CrossButton getButton() {
        return button;
    }

    @Override
    protected Pos defaultAlignment() {
        return Pos.CENTER;
    }
}
