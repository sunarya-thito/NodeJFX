package thito.nodejfx;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class NodeRegistry {
    private static Map<NodeLinkStyle, String> styleNameMap = new LinkedHashMap<>();

    static {
        styleNameMap.put(NodeLinkStyle.BEZIER_STYLE, "Bezier");
        styleNameMap.put(NodeLinkStyle.PIPE_STYLE, "Pipeline");
        styleNameMap.put(NodeLinkStyle.LINE_STYLE, "Line");
    }

    public static Map<NodeLinkStyle, String> getRegisteredStyleMap() {
        return styleNameMap;
    }

    private NodeRegistry() {}
}
