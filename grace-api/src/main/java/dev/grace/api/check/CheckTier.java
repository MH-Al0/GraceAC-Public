package dev.grace.api.check;

public enum CheckTier {
    LOW(1),
    MEDIUM(2),
    HIGH(3),
    CRITICAL(4);

    private final int Level;

    CheckTier(int Level) {
        this.Level = Level;
    }

    public int getLevel() {
        return Level;
    }
}