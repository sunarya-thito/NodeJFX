package thito.nodejfx;

public enum ToolMode {
    SELECT("Select Mode"), GROUPING("Grouping Mode");
    String displayName;
    ToolMode(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
