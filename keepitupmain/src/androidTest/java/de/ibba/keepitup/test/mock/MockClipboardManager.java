package de.ibba.keepitup.test.mock;

import de.ibba.keepitup.ui.clipboard.IClipboardManager;
import de.ibba.keepitup.util.NumberUtil;

public class MockClipboardManager implements IClipboardManager {

    private String data;

    public MockClipboardManager() {
        this.data = null;
    }

    @Override
    public boolean hasData() {
        return data != null;
    }

    @Override
    public boolean hasNumericIntegerData() {
        return hasData() && NumberUtil.isValidLongValue(data);
    }

    @Override
    public String getData() {
        return data;
    }

    @Override
    public void putData(String data) {
        this.data = data;
    }
}
