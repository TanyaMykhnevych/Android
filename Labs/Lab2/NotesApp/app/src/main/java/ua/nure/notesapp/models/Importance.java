package ua.nure.notesapp.models;

public enum Importance {
    ALL(0), LOW(1), NORMAL(2), HIGH(3);

    private final int value;
    Importance(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
