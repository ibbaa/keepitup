/*
 * Copyright (c) 2024. Alwin Ibba
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ibbaa.keepitup.test.mock;

import android.content.Context;

import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.service.IFileManager;
import net.ibbaa.keepitup.service.network.DownloadCommand;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;

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
