package thito.nodejfx.event;

import javafx.event.Event;
import javafx.event.EventType;
import thito.nodejfx.*;

public class NodeLinkEvent extends Event {
    public static final EventType<NodeLinkEvent>
    NODE_LINKED_EVENT = new EventType<>("NODE_LINKED_EVENT"),
    NODE_LINKING_EVENT = new EventType<>("NODE_LINKING_EVENT"),
    NODE_UNLINKED_EVENT = new EventType<>("NODE_UNLINKED_EVENT"),
    NODE_LINK_CANCEL_EVENT = new EventType<>("NODE_LINK_CANCEL_EVENT")
    ;

    private NodeParameter nodeSource, nodeTarget;
    private NodeLinking linking;
    private NodeLinked linked;
    public NodeLinkEvent(EventType<? extends Event> eventType, NodeLinking linking, NodeLinked linked, NodeParameter source, NodeParameter target) {
        super(eventType);
        this.linked = linked;
        this.linking = linking;
        this.nodeSource = source;
        this.nodeTarget = target;
    }

    public NodeLinked getLinked() {
        return linked;
    }

    public NodeLinking getLinking() {
        return linking;
    }

    public NodeParameter getNodeOutput() {
        return nodeSource;
    }

    public NodeParameter getNodeInput() {
        return nodeTarget;
    }
}
