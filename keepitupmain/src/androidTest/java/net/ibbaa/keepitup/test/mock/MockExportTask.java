package net.ibbaa.keepitup.test.mock;

import android.app.Activity;

import java.io.File;

import net.ibbaa.keepitup.ui.sync.ExportTask;

public class MockExportTask extends ExportTask {

    private final boolean result;

    public MockExportTask(Activity activity, boolean result) {
        super(activity, new File(""), "");
        this.result = result;
    }

    @Override
    protected Boolean runInBackground() {
        return result;
    }
}
