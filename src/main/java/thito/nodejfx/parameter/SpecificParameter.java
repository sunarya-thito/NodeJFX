package thito.nodejfx.parameter;

import com.sun.javafx.scene.control.skin.*;
import javafx.beans.property.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import thito.nodejfx.*;
import thito.nodejfx.parameter.converter.*;
import thito.nodejfx.parameter.type.*;

public class SpecificParameter extends NodeParameter implements UserInputParameter<Object> {
    private ObjectProperty<Object> value = new SimpleObjectProperty<>();
    private ObjectProperty<TypeCaster<Object>> typeCaster = new SimpleObjectProperty<>(TypeCaster.PASSTHROUGH_TYPE_CASTER);
    private BooleanProperty disable = new SimpleBooleanProperty();
    private Class<?> type;
    private Label label;
    private Label gr;
    public SpecificParameter(String text, String subtext, Class<?> type) {
        this.label = new Label();
        label.setTextOverrun(OverrunStyle.LEADING_WORD_ELLIPSIS);
        Tooltip tooltip = new Tooltip();
        label.setTooltip(tooltip);
        tooltip.textProperty().bind(label.textProperty());
        this.label.setGraphic(gr = new Label(text));
        gr.setTextFill(Color.WHITE);
        label.setGraphicTextGap(5);
        getContainer().getChildren().add(label);
        this.type = type;
        label.setTextFill(Color.color(1, 1, 1, 0.5));
        getInputType().set(JavaParameterType.getType(type));
        getOutputType().set(JavaParameterType.getType(type));
        label.setText(subtext);
    }

    @Override
    protected void initialize(Node node) {
        super.initialize(node);
    }

    public Label getLabel() {
        return gr;
    }

    @Override
    public BooleanProperty disableInputProperty() {
        return disable;
    }

    @Override
    protected Pos defaultAlignment() {
        return Pos.CENTER;
    }

    @Override
    public ObjectProperty<Object> valueProperty() {
        return value;
    }

    @Override
    public ObjectProperty<TypeCaster<Object>> typeCaster() {
        return typeCaster;
    }
}
