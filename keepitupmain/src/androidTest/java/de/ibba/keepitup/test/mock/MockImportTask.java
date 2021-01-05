package de.ibba.keepitup.test.mock;

import android.app.Activity;

import java.io.File;

import de.ibba.keepitup.ui.sync.ImportTask;

public class MockImportTask extends ImportTask {

    private final boolean result;

    public MockImportTask(Activity activity, boolean result) {
        super(activity, new File(""), "");
        this.result = result;
    }

    @Override
    protected Boolean runInBackground() {
        return result;
    }
}
