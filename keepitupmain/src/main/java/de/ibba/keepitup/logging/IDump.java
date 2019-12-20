package de.ibba.keepitup.logging;

public interface IDump {

    void dump(String tag, String message, String baseFileName, IDumpSource source);
}
