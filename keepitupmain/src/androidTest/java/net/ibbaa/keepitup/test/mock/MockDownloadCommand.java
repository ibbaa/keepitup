/*
 * Copyright (c) 2022. Alwin Ibba
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
import net.ibbaa.keepitup.service.network.DownloadCommand;
import net.ibbaa.keepitup.service.network.DownloadCommandResult;

import java.io.File;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

public class MockDownloadCommand extends DownloadCommand {

    private final DownloadCommandResult downloadCommandResult;
    private final RuntimeException exception;
    private final boolean block;
    private final CountDownLatch latch;

    public MockDownloadCommand(Context context, NetworkTask networkTask, URL url, File folder, boolean delete, DownloadCommandResult downloadCommandResult) {
        super(context, networkTask, url, folder, delete);
        this.exception = null;
        this.downloadCommandResult = downloadCommandResult;
        this.block = false;
        this.latch = new CountDownLatch(1);
    }

    public MockDownloadCommand(Context context, NetworkTask networkTask, URL url, File folder, boolean delete, RuntimeException exception) {
        super(context, networkTask, url, folder, delete);
        this.exception = exception;
        this.downloadCommandResult = null;
        this.block = false;
        this.latch = new CountDownLatch(1);
    }

    public MockDownloadCommand(Context context, NetworkTask networkTask, URL url, File folder, boolean delete, DownloadCommandResult downloadCommandResult, boolean block) {
        super(context, networkTask, url, folder, delete);
        this.exception = null;
        this.downloadCommandResult = null;
        this.block = block;
        this.latch = new CountDownLatch(1);
    }

    public void waitUntilReady() {
        try {
            latch.await();
        } catch (InterruptedException exc) {
            // Do nothing
        }
    }

    @Override
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
