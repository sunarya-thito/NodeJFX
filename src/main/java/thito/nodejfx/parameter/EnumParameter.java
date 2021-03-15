package thito.nodejfx.parameter;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.SetChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.*;
import javafx.util.StringConverter;
import thito.nodejfx.NodeParameter;
import thito.nodejfx.parameter.converter.EnumTypeCaster;
import thito.nodejfx.parameter.converter.TypeCaster;
import thito.nodejfx.parameter.type.JavaParameterType;

public class EnumParameter<T extends Enum<T>> extends NodeParameter implements UserInputParameter<T> {

    private ObjectProperty<Object> value = new SimpleObjectProperty<>();
    private ObjectProperty<TypeCaster<T>> typeCaster = new SimpleObjectProperty<>();

    private Label fieldText;
    private ComboBox<T> input;
    private BorderPane box = new BorderPane();
    public EnumParameter(String fieldName, Class<T> enumClass) {
        fieldText = new Label(fieldName);
        fieldText.setTextFill(Color.WHITE);
        typeCaster.set(new EnumTypeCaster<>(enumClass));
        ObservableList<T> values = FXCollections.observableArrayList(enumClass.getEnumConstants());
        values.add(0, null);
        input = new ComboBox<>(values);
        input.setEditable(false);
        BorderPane.setMargin(fieldText, new Insets(0, 20, 0, 0));
        BorderPane.setAlignment(fieldText, Pos.CENTER);
        BorderPane.setAlignment(input, Pos.CENTER_LEFT);
        box.setLeft(fieldText);
        box.setRight(input);
        getContainer().getChildren().add(box);
        getInputType().set(JavaParameterType.getType(enumClass));
        getOutputType().set(JavaParameterType.getType(enumClass));
        input.setConverter(new StringConverter<T>() {
            @Override
            public String toString(T object) {
                Object x = typeCaster.get().toSafeObject(object);
                return x instanceof Enum ? ((Enum<?>) x).name() : TypeCaster.toString(x);
            }

            @Override
            public T fromString(String string) {
                return typeCaster.get().fromSafeObject(string);
            }
        });
        TypeCaster.bindBidirectional(value, input.valueProperty(), typeCaster);
        getUnmodifiableInputLinks().addListener((SetChangeListener<NodeParameter>) change -> {
            if (change.wasRemoved()) {
                valueProperty().unbind();
            }
            if (change.wasAdded()) {
                NodeParameter parameter = change.getElementAdded();
                if (parameter instanceof UserInputParameter) {
                    valueProperty().bind(((UserInputParameter) parameter).valueProperty());
                }
            }
            input.setDisable(!change.getSet().isEmpty());
        });
        getMultipleInputAssigner().set(false);
        getMultipleOutputAssigner().set(true);
    }

    @Override
    public Label getLabel() {
        return fieldText;
    }

    @Override
    public BooleanProperty disableInputProperty() {
        return input.disableProperty();
    }

    @Override
    public ObjectProperty<Object> valueProperty() {
        return value;
    }

    @Override
    public ObjectProperty<TypeCaster<T>> typeCaster() {
        return typeCaster;
    }
}
