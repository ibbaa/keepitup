package net.ibbaa.keepitup.test.mock;

import android.content.Context;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;

import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.service.IFileManager;
import net.ibbaa.keepitup.service.network.DownloadCommand;

public class TestDownloadCommand extends DownloadCommand {

    private URLConnection urlConnection;
    private IFileManager fileManager;

    public TestDownloadCommand(Context context, NetworkTask networkTask, URL url, File folder, boolean delete) {
        super(context, networkTask, url, folder, delete);
        reset();
    }

    public void reset() {
        urlConnection = null;
        fileManager = null;
    }

    public void setURLConnection(URLConnection urlConnection) {
        this.urlConnection = urlConnection;
    }

    public void setFileManager(IFileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Override
    protected URLConnection openConnection() {
        return urlConnection;
    }

    @Override
    protected IFileManager getFileManager() {
        if (fileManager != null) {
            return fileManager;
        }
        return super.getFileManager();
    }
}
