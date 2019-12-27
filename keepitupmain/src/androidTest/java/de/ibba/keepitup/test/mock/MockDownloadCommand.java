package de.ibba.keepitup.test.mock;

import android.content.Context;

import java.io.File;
import java.net.URL;

import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.service.network.DownloadCommand;
import de.ibba.keepitup.service.network.DownloadCommandResult;

public class MockDownloadCommand extends DownloadCommand {

    private final DownloadCommandResult downloadCommandResult;

    public MockDownloadCommand(Context context, NetworkTask networkTask, URL url, File folder, boolean delete, DownloadCommandResult downloadCommandResult) {
        super(context, networkTask, url, folder, delete);
        this.downloadCommandResult = downloadCommandResult;
    }

    @Override
    public DownloadCommandResult call() {
        return downloadCommandResult;
    }
}
