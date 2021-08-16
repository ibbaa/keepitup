package net.ibbaa.keepitup.test.mock;

import net.ibbaa.keepitup.ui.clipboard.IClipboardManager;
import net.ibbaa.keepitup.util.NumberUtil;

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

    public void clearData() {
        this.data = null;
    }
}
