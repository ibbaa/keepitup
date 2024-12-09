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

import net.ibbaa.keepitup.service.network.ConnectCommand;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

public class TestConnectCommand extends ConnectCommand {

    private List<MockConnectionResult> connectionResults;
    private int call;

    public TestConnectCommand(Context context, InetAddress address, int port, int connectCount, boolean stopOnSuccess) {
        super(context, address, port, connectCount, stopOnSuccess);
        reset();
    }

    public void reset() {
        call = 0;
    }

    public void setConnectionResults(List<MockConnectionResult> connectionResults) {
        this.connectionResults = connectionResults;
    }

    @Override
    protected ConnectionResult connect() throws IOException {
        if (connectionResults != null && call < connectionResults.size()) {
            MockConnectionResult result = connectionResults.get(call);
            call++;
            IOException exception = result.getException();
            if (exception != null) {
                throw exception;
            }
            return result;
        }
        return null;
    }

    public static class MockConnectionResult extends ConnectionResult {
        private final IOException exception;

        public MockConnectionResult(boolean success, long duration, IOException exception) {
            super(success, duration);
            this.exception = exception;
        }

        public IOException getException() {
            return exception;
        }
    }
}
