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
    private final boolean block;

    public MockDownloadCommand(Context context, NetworkTask networkTask, URL url, File folder, boolean delete, DownloadCommandResult downloadCommandResult) {
        super(context, networkTask, url, folder, delete);
        this.exception = null;
        this.downloadCommandResult = downloadCommandResult;
        this.block = false;
    }

    public MockDownloadCommand(Context context, NetworkTask networkTask, URL url, File folder, boolean delete, RuntimeException exception) {
        super(context, networkTask, url, folder, delete);
        this.exception = exception;
        this.downloadCommandResult = null;
        this.block = false;
    }

    public MockDownloadCommand(Context context, NetworkTask networkTask, URL url, File folder, boolean delete, DownloadCommandResult downloadCommandResult, boolean block) {
        super(context, networkTask, url, folder, delete);
        this.exception = null;
        this.downloadCommandResult = null;
        this.block = block;
    }

    @Override
    public DownloadCommandResult call() {
        while (block) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException exc) {
                return downloadCommandResult;
            }
        }
        if (exception != null) {
            throw exception;
        }
        return downloadCommandResult;
    }
}
