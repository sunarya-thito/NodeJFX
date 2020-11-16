package thito.nodejfx.control;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.util.StringConverter;
import thito.nodejfx.NodeLink;
import thito.nodejfx.NodeLinkStyle;
import thito.nodejfx.NodeRegistry;
import thito.nodejfx.NodeViewport;

import java.util.Map;

public class LinkStyleViewportControl extends BorderPane {
    private boolean editing;
    public LinkStyleViewportControl(NodeViewport viewport) {
        ObservableList<Map.Entry<NodeLinkStyle, String>> styleList = FXCollections.observableArrayList(NodeRegistry.getRegisteredStyleMap().entrySet());
        ComboBox<Map.Entry<NodeLinkStyle, String>> styles = new ComboBox<>(styleList);
        styles.converterProperty().set(new StringConverter<Map.Entry<NodeLinkStyle, String>>() {
            @Override
            public String toString(Map.Entry<NodeLinkStyle, String> object) {
                return object.getValue();
            }

            @Override
            public Map.Entry<NodeLinkStyle, String> fromString(String string) {
                return null;
            }
        });

        styles.valueProperty().addListener((obs, oldVal, newVal) -> {
            editing = true;
            viewport.getCanvas().nodeLinkStyleProperty().set(newVal.getKey());
            editing = false;
        });
        if (!styleList.isEmpty()) {
            styles.setValue(styleList.get(0));
        }
        viewport.getCanvas().nodeLinkStyleProperty().addListener((obs, oldVal, newVal) -> {
            if (!editing) {
                for (Map.Entry<NodeLinkStyle, String> entry : styleList) {
                    if (newVal.equals(entry.getKey())) {
                        styles.setValue(entry);
                        break;
                    }
                }
            }
        });
        setAlignment(styles, Pos.CENTER);
        setCenter(styles);
    }
}
