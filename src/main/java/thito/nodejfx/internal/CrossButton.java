package thito.nodejfx.internal;

import javafx.beans.property.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import thito.nodejfx.*;

import java.beans.*;

public class CrossButton extends Pane {
    private Circle circle = new Circle();
    private Rectangle rectangle = new Rectangle();
    private Rectangle rectangle2 = new Rectangle();

    private ObjectProperty<Paint> fill = new SimpleObjectProperty<>(Color.TRANSPARENT);
    private ObjectProperty<Paint> crossFill = new SimpleObjectProperty<>(Color.WHITE);
    private DoubleProperty thickness = new SimpleDoubleProperty(2);

    public CrossButton() {
        rectangle.heightProperty().bind(heightProperty());
        rectangle.widthProperty().bind(thickness);
        rectangle2.heightProperty().bind(thickness);
        rectangle2.widthProperty().bind(widthProperty());
        circle.radiusProperty().bind(widthProperty());
        circle.fillProperty().bind(fill);
        rectangle.fillProperty().bind(crossFill);
        rectangle2.fillProperty().bind(crossFill);
        setPrefHeight(10);
        setPrefWidth(10);
        rectangle.layoutXProperty().bind(widthProperty().subtract(rectangle.widthProperty()).divide(2));
        rectangle2.layoutYProperty().bind(heightProperty().subtract(rectangle2.heightProperty()).divide(2));
        circle.layoutXProperty().bind(circle.radiusProperty().divide(2));
        circle.layoutYProperty().bind(circle.radiusProperty().divide(2));
        getChildren().addAll(circle, rectangle2, rectangle);
    }

    public CrossButton asAdd() {
        addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            fill.set(Color.color(1, 1, 1, 0.3));
            crossFill.set(Color.color(1, 1, 1));
        });
        addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            fill.set(Color.TRANSPARENT);
            crossFill.set(Color.WHITE);
        });
        return this;
    }

    public CrossButton asRemove() {
        addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            fill.set(Color.color(1, 0.2, 0.2, 0.3));
            crossFill.set(Color.color(1, 0.2, 0.2));
        });
        addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            fill.set(Color.TRANSPARENT);
            crossFill.set(Color.WHITE);
        });
        diagonal();
        return this;
    }

    public CrossButton diagonal() {
        setRotate(45);
        return this;
    }

    public Paint getFill() {
        return fill.get();
    }

    public ObjectProperty<Paint> fillProperty() {
        return fill;
    }

    public void setFill(Paint fill) {
        this.fill.set(fill);
    }

    public Paint getCrossFill() {
        return crossFill.get();
    }

    public ObjectProperty<Paint> crossFillProperty() {
        return crossFill;
    }

    public void setCrossFill(Paint crossFill) {
        this.crossFill.set(crossFill);
    }

    public double getThickness() {
        return thickness.get();
    }

    public DoubleProperty thicknessProperty() {
        return thickness;
    }

    public void setThickness(double thickness) {
        this.thickness.set(thickness);
    }
}
