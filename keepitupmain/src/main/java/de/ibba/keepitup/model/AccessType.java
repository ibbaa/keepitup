package de.ibba.keepitup.model;

public enum AccessType {

    PING(1);

    private final int code;

    AccessType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static AccessType forCode(int code) {
        AccessType[] values = AccessType.values();
        for (AccessType value : values) {
            if (code == value.getCode()) {
                return value;
            }
        }
        return null;
    }
}
