package de.ibba.keepitup.logging;

public enum LogLevel {

    DEBUG(1),
    INFO(2),
    ERROR(3);

    private final int level;

    LogLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
