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

import net.ibbaa.keepitup.model.SNMPAuthInfo;
import net.ibbaa.keepitup.model.SNMPTransport;
import net.ibbaa.keepitup.model.SNMPVersion;
import net.ibbaa.keepitup.ui.sync.SNMPScanResult;
import net.ibbaa.keepitup.ui.sync.SNMPScanTask;
import net.ibbaa.keepitup.ui.sync.UITaskResultDispatcher;

public class MockSNMPScanTask extends SNMPScanTask {

    private final SNMPScanResult mockResult;

    public MockSNMPScanTask(UITaskResultDispatcher<SNMPScanResult> dispatcher, Context context, SNMPScanResult mockResult) {
        super(dispatcher, context, -1, "test", 161, SNMPVersion.V2C, SNMPTransport.UDP, createAuthInfo());
        this.mockResult = mockResult;
    }

    private static SNMPAuthInfo createAuthInfo() {
        SNMPAuthInfo authInfo = new SNMPAuthInfo();
        authInfo.setCommunity("public");
        return authInfo;
    }

    @Override
    protected SNMPScanResult runInBackground() {
        return mockResult;
    }
}
