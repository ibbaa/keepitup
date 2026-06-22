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

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.model.AccessTypeData;
import net.ibbaa.keepitup.model.Header;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.service.network.DownloadCommand;
import net.ibbaa.keepitup.service.network.DownloadCommandResult;
import net.ibbaa.keepitup.service.network.DownloadConnectResult;
import net.ibbaa.keepitup.util.StringUtil;
import net.ibbaa.keepitup.util.URLUtil;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class MockDownloadCommand extends DownloadCommand {

    private final Context context;
    private final DownloadCommandResult downloadCommandResult;
    private final RuntimeException exception;
    private final boolean block;
    private final CountDownLatch latch;
    private URL url;
    private List<ConnectToAddress> connectToAddresses;
    private List<Header> headers;
    private String folder;

    public MockDownloadCommand(Context context, NetworkTask networkTask, AccessTypeData data, URL url, String folder, boolean delete, List<ConnectToAddress> connectToAddresses, DownloadCommandResult downloadCommandResult) {
        super(context, networkTask, data, url, folder, delete);
        this.context = context;
        this.url = url;
        this.connectToAddresses = connectToAddresses;
        this.folder = folder;
        this.exception = null;
        this.downloadCommandResult = downloadCommandResult;
        this.block = false;
        this.latch = new CountDownLatch(1);
    }

    public MockDownloadCommand(Context context, NetworkTask networkTask, AccessTypeData data, URL url, String folder, boolean delete, List<ConnectToAddress> connectToAddresses, RuntimeException exception) {
        super(context, networkTask, data, url, folder, delete);
        this.context = context;
        this.url = url;
        this.connectToAddresses = connectToAddresses;
        this.folder = folder;
        this.exception = exception;
        this.downloadCommandResult = null;
        this.block = false;
        this.latch = new CountDownLatch(1);
    }

    public MockDownloadCommand(Context context, NetworkTask networkTask, AccessTypeData data, URL url, String folder, boolean delete, List<ConnectToAddress> connectToAddresses, DownloadCommandResult downloadCommandResult, boolean block) {
        super(context, networkTask, data, url, folder, delete);
        this.context = context;
        this.url = url;
        this.connectToAddresses = connectToAddresses;
        this.folder = folder;
        this.exception = null;
        this.downloadCommandResult = downloadCommandResult;
        this.block = block;
        this.latch = new CountDownLatch(1);
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public List<ConnectToAddress> getConnectToAddresses() {
        return connectToAddresses;
    }

    public void setConnectToAddresses(List<ConnectToAddress> connectToAddresses) {
        this.connectToAddresses = connectToAddresses;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public void setHeaders(List<Header> headers) {
        this.headers = headers;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public void waitUntilReady() {
        try {
            latch.await();
        } catch (InterruptedException exc) {
            // Do nothing
        }
    }

    @Override
    @SuppressWarnings({"LoopConditionNotUpdatedInsideLoop"})
    public DownloadCommandResult call() {
        if (connectToAddresses != null) {
            for (ConnectToAddress connectToAddress : connectToAddresses) {
                if (connectToAddress.resolvedAddress() == null) {
                    String connectMessage = connectToAddress.resolveMessage();
                    if (!StringUtil.isEmpty(connectMessage)) {
                        connectMessage = context.getResources().getString(R.string.text_connect_connect_to_error) + " " + connectMessage;
                    }
                    DownloadConnectResult connectResult = new DownloadConnectResult(URLUtil.removeIPv6Brackets(url.getHost()), URLUtil.getPort(url), null, connectToAddress.resolve().getTargetPort(), connectMessage, Collections.emptyList(), false);
                    return new DownloadCommandResult(url, List.of(connectResult), false, false, false, true, false, Collections.emptyList(), Collections.emptyList(), null, 0, null);
                }
            }
        }
        while (block) {
            try {
                Thread.sleep(1000);
                latch.countDown();
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
