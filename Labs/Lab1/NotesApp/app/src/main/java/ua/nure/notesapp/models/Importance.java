package ua.nure.notesapp.models;

public enum Importance {
    LOW(0), NORMAL(1), HIGH(2);

    private final int value;
    private Importance(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
