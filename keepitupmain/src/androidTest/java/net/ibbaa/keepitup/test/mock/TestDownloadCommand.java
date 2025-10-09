/*
 * Copyright (c) 2025 Alwin Ibba
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
import android.os.ParcelFileDescriptor;

import androidx.documentfile.provider.DocumentFile;

import net.ibbaa.keepitup.model.AccessTypeData;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.service.IDocumentManager;
import net.ibbaa.keepitup.service.IFileManager;
import net.ibbaa.keepitup.service.network.DownloadCommand;

import java.io.FileOutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Response;

public class TestDownloadCommand extends DownloadCommand {

    private final Map<String, Response> responses;
    private FileOutputStream outputStream;
    private IFileManager fileManager;
    private IDocumentManager documentManager;

    public TestDownloadCommand(Context context, NetworkTask networkTask, AccessTypeData data, URL url, String folder, boolean delete, ConnectToAddress connectToAddress) {
        super(context, networkTask, data, url, folder, delete, connectToAddress);
        responses = new HashMap<>();
        reset();
    }

    public void reset() {
        responses.clear();
        fileManager = null;
    }

    public void addResponse(String url, Response response) {
        responses.put(url, response);
    }

    public void setOutputStream(FileOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void setFileManager(IFileManager fileManager) {
        this.fileManager = fileManager;
    }

    public void setDocumentManager(IDocumentManager documentManager) {
        this.documentManager = documentManager;
    }

    @Override
    protected Response openResponse(URL url) {
        if (url == null) {
            return null;
        }
        return responses.get(url.toString());
    }

    @Override
    protected ParcelFileDescriptor getDownloadFileDescriptor(DocumentFile documentDownloadFile) {
        return null;
    }

    @Override
    protected FileOutputStream getOutputStream(ParcelFileDescriptor documentFileDescriptor) {
        return outputStream;
    }

    @Override
    protected IFileManager getFileManager() {
        return fileManager != null ? fileManager : super.getFileManager();
    }

    @Override
    protected IDocumentManager getDocumentManager() {
        return documentManager != null ? documentManager : super.getDocumentManager();
    }
}
