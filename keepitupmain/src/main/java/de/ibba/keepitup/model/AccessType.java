package de.ibba.keepitup.model;

public enum AccessType {

    PING(1, true),
    CONNECT(2, false);

    private final int code;
    private final boolean needsPort;

    AccessType(int code, boolean needsPort) {
        this.code = code;
        this.needsPort = needsPort;
    }

    public int getCode() {
        return code;
    }

    public boolean needsPort() {
        return needsPort;
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
