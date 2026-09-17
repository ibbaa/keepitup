/*
 * Copyright (c) 2026 Alwin Ibba
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
import net.ibbaa.keepitup.model.Header;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.service.IDocumentManager;
import net.ibbaa.keepitup.service.IFileManager;
import net.ibbaa.keepitup.service.network.CertificateExpiryInfo;
import net.ibbaa.keepitup.service.network.DownloadCommand;

import java.io.FileOutputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;
import okhttp3.Response;

public class TestDownloadCommand extends DownloadCommand {

    private final Map<String, Response> responses;
    private final Map<String, List<CertificateExpiryInfo>> expiryInfos;
    private final Map<String, Request> requests;
    private FileOutputStream outputStream;
    private IFileManager fileManager;
    private IDocumentManager documentManager;

    public TestDownloadCommand(Context context, NetworkTask networkTask, AccessTypeData data, URL url, String folder, boolean delete, List<ConnectToAddress> connectToAddresses, List<Header> headers) {
        super(context, networkTask, data, url, folder, delete, connectToAddresses, headers);
        responses = new HashMap<>();
        expiryInfos = new HashMap<>();
        requests = new HashMap<>();
        reset();
    }

    public void reset() {
        responses.clear();
        expiryInfos.clear();
        requests.clear();
        fileManager = null;
    }

    public void addResponse(String url, Response response) {
        responses.put(url, response);
    }

    public void addResponse(String url, Response response, List<CertificateExpiryInfo> expiryInfo) {
        responses.put(url, response);
        expiryInfos.put(url, expiryInfo);
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

    public Request getRequest(URL url) {
        return requests.get(url.toString());
    }

    @Override
    protected ResponseResult openResponse(URL url, ConnectToAddress connectToAddress) {
        if (url == null) {
            return null;
        }
        Request.Builder requestBuilder = buildRequest(url);
        Request request = requestBuilder.build();
        requests.put(url.toString(), request);
        List<CertificateExpiryInfo> expiryInfo = expiryInfos.getOrDefault(url.toString(), Collections.emptyList());
        return new ResponseResult(responses.get(url.toString()), expiryInfo);
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
