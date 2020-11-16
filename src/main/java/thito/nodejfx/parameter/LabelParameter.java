package thito.nodejfx.parameter;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import thito.nodejfx.NodeParameter;

public class LabelParameter extends NodeParameter {
    private Label label;
    public LabelParameter(String text) {
        this();
        label.setText(text);
    }

    public LabelParameter() {
        this.label = new Label();
        getContainer().getChildren().add(label);
        getAllowInput().set(false);
        getAllowOutput().set(false);
    }

    public Label getLabel() {
        return label;
    }

    @Override
    protected Pos defaultAlignment() {
        return Pos.CENTER;
    }
}
