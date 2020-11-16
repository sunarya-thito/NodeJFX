package thito.nodejfx.event;

import javafx.event.Event;
import javafx.event.EventType;
import thito.nodejfx.NodeParameter;

public class NodeLinkEvent extends Event {
    public static final EventType<NodeLinkEvent>
    NODE_LINKED_EVENT = new EventType<>("NODE_LINKED_EVENT"),
    NODE_LINKING_EVENT = new EventType<>("NODE_LINKING_EVENT"),
    NODE_UNLINKED_EVENT = new EventType<>("NODE_UNLINKED_EVENT")
    ;

    private NodeParameter nodeSource, nodeTarget;
    public NodeLinkEvent(EventType<? extends Event> eventType, NodeParameter source, NodeParameter target) {
        super(eventType);
        this.nodeSource = source;
        this.nodeTarget = target;
    }

    public NodeParameter getNodeOutput() {
        return nodeSource;
    }

    public NodeParameter getNodeInput() {
        return nodeTarget;
    }
}
