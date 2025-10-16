package main.model;

public enum MobType {
    COMMON("Common"),
    BLUE_CHAMPION("Blue Champion"),
    RED_CHAMPION("Red Champion");

    private String displayName;

    MobType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {

        return displayName;
    }
}
