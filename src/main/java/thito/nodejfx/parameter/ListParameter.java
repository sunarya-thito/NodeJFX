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
import javafx.util.StringConverter;
import thito.nodejfx.NodeParameter;
import thito.nodejfx.parameter.converter.TypeCaster;
import thito.nodejfx.parameter.type.JavaParameterType;

import java.util.Collection;

public class ListParameter<T> extends NodeParameter implements UserInputParameter<T> {
    private ObjectProperty<Object> value = new SimpleObjectProperty<>();
    private ObjectProperty<TypeCaster<T>> typeCaster = new SimpleObjectProperty<>();

    private Label fieldText;
    private ComboBox<T> input;
    private BorderPane box = new BorderPane();
    public ListParameter(String fieldName, Class<T> type, Collection<T> collection, StringConverter<T> converter) {
        fieldText = new Label(fieldName);
        typeCaster.set(TypeCaster.checkedTypeCaster(type));
        ObservableList<T> values = FXCollections.observableArrayList(collection);
        values.add(0, null);
        input = new ComboBox<>(values);
        input.setEditable(true);
        BorderPane.setMargin(fieldText, new Insets(0, 20, 0, 0));
        BorderPane.setAlignment(fieldText, Pos.CENTER);
        BorderPane.setAlignment(input, Pos.CENTER_LEFT);
        box.setLeft(fieldText);
        box.setRight(input);
        input.setConverter(converter);
        getContainer().getChildren().add(box);
        getInputType().set(JavaParameterType.getType(type));
        getOutputType().set(JavaParameterType.getType(type));
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
