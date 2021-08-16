package net.ibbaa.keepitup.model;

public enum AccessType {

    PING(1, false),
    CONNECT(2, true),
    DOWNLOAD(3, false);

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
