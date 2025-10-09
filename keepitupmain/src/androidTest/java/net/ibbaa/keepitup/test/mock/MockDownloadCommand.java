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

import net.ibbaa.keepitup.model.AccessTypeData;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.service.network.DownloadCommand;
import net.ibbaa.keepitup.service.network.DownloadCommandResult;

import java.net.URL;
import java.util.concurrent.CountDownLatch;

public class MockDownloadCommand extends DownloadCommand {

    private final DownloadCommandResult downloadCommandResult;
    private final RuntimeException exception;
    private final boolean block;
    private final CountDownLatch latch;
    private URL url;
    private ConnectToAddress connectToAddress;
    private String folder;

    public MockDownloadCommand(Context context, NetworkTask networkTask, AccessTypeData data, URL url, String folder, boolean delete, ConnectToAddress connectToAddress, DownloadCommandResult downloadCommandResult) {
        super(context, networkTask, data, url, folder, delete);
        this.url = url;
        this.connectToAddress = connectToAddress;
        this.folder = folder;
        this.exception = null;
        this.downloadCommandResult = downloadCommandResult;
        this.block = false;
        this.latch = new CountDownLatch(1);
    }

    public MockDownloadCommand(Context context, NetworkTask networkTask, AccessTypeData data, URL url, String folder, boolean delete, ConnectToAddress connectToAddress, RuntimeException exception) {
        super(context, networkTask, data, url, folder, delete);
        this.url = url;
        this.connectToAddress = connectToAddress;
        this.folder = folder;
        this.exception = exception;
        this.downloadCommandResult = null;
        this.block = false;
        this.latch = new CountDownLatch(1);
    }

    public MockDownloadCommand(Context context, NetworkTask networkTask, AccessTypeData data, URL url, String folder, boolean delete, ConnectToAddress connectToAddress, DownloadCommandResult downloadCommandResult, boolean block) {
        super(context, networkTask, data, url, folder, delete);
        this.url = url;
        this.connectToAddress = connectToAddress;
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

    public ConnectToAddress getConnectToAddress() {
        return connectToAddress;
    }

    public void setConnectToAddress(ConnectToAddress connectToAddress) {
        this.connectToAddress = connectToAddress;
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
