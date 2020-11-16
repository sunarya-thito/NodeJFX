package thito.nodejfx.control;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.util.StringConverter;
import thito.nodejfx.NodeViewport;

public class ZoomViewportControl extends BorderPane {
    private boolean editing;
    public ZoomViewportControl(NodeViewport viewport) {
        Label label = new Label("Zoom: ");
        Spinner<Integer> zoom = new Spinner<>(10, 150, 0, 3);

        zoom.getValueFactory().setConverter(new StringConverter<Integer>() {
            @Override
            public String toString(Integer object) {
                return object+"%";
            }

            @Override
            public Integer fromString(String string) {
                try {
                    return Integer.valueOf(string.replace("%", ""));
                } catch (Throwable t) {
                    return 0;
                }
            }
        });

        // patch fix: Spinner does not add "%" at the beginning of the stage
        zoom.getValueFactory().setValue(100);
        // end of patch fix

        zoom.setEditable(true);
        zoom.setPrefWidth(80);
        viewport.scaleProperty().addListener((obs, oldVal, newVal) -> {
            if (editing) return;
            zoom.getValueFactory().setValue(newVal.intValue());
        });
        zoom.valueProperty().addListener((obs, oldVal, newVal) -> {
            editing = true;
            viewport.setScale(newVal.intValue());
            editing = false;
        });
        setAlignment(label, Pos.CENTER);
        setAlignment(zoom, Pos.CENTER);
        setLeft(label);
        setCenter(zoom);
        setMinWidth(100);
    }

}
