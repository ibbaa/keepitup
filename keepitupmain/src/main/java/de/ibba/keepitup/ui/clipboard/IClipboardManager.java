package de.ibba.keepitup.ui.clipboard;

public interface IClipboardManager {

    boolean hasData();

    boolean hasNumericIntegerData();

    String getData();

    void putData(String data);
}
