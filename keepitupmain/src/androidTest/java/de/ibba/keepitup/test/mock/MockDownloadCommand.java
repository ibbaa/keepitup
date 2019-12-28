package de.ibba.keepitup.test.mock;

import android.content.Context;

import java.io.File;
import java.net.URL;

import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.service.network.DownloadCommand;
import de.ibba.keepitup.service.network.DownloadCommandResult;

public class MockDownloadCommand extends DownloadCommand {

    private final DownloadCommandResult downloadCommandResult;
    private final RuntimeException exception;

    public MockDownloadCommand(Context context, NetworkTask networkTask, URL url, File folder, boolean delete, DownloadCommandResult downloadCommandResult) {
        super(context, networkTask, url, folder, delete);
        this.exception = null;
        this.downloadCommandResult = downloadCommandResult;
    }

    public MockDownloadCommand(Context context, NetworkTask networkTask, URL url, File folder, boolean delete, RuntimeException exception) {
        super(context, networkTask, url, folder, delete);
        this.exception = exception;
        this.downloadCommandResult = null;
    }

    @Override
    public DownloadCommandResult call() {
        if (exception != null) {
            throw exception;
        }
        return downloadCommandResult;
    }
}
