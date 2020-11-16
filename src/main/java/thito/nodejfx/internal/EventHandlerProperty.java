package thito.nodejfx.internal;

import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.event.EventHandler;

public class EventHandlerProperty<T extends Event> extends SimpleObjectProperty<EventHandler<T>> implements EventHandler<T> {
    @Override
    public void handle(T event) {
        EventHandler<T> handler = get();
        if (handler != null) {
            handler.handle(event);
        }
    }
}
