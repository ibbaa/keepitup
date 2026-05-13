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

import net.ibbaa.keepitup.model.SNMPVersion;
import net.ibbaa.keepitup.service.network.SNMPAccess;

import java.net.InetAddress;

public class MockSNMPAccess extends SNMPAccess {

    private WalkResult walkResult;
    private WalkResult walkInterfacesDescrResult;
    private WalkResult walkInterfacesTypeResult;
    private WalkResult walkInterfacesOperStatusResult;

    public MockSNMPAccess(Context context) {
        super(context, InetAddress.getLoopbackAddress(), 161, SNMPVersion.V2C, "public", false);
    }

    public void setWalkResult(WalkResult walkResult) {
        this.walkResult = walkResult;
    }

    public void setWalkInterfacesDescrResult(WalkResult walkInterfacesDescrResult) {
        this.walkInterfacesDescrResult = walkInterfacesDescrResult;
    }

    public void setWalkInterfacesTypeResult(WalkResult walkInterfacesTypeResult) {
        this.walkInterfacesTypeResult = walkInterfacesTypeResult;
    }

    public void setWalkInterfacesOperStatusResult(WalkResult walkInterfacesOperStatusResult) {
        this.walkInterfacesOperStatusResult = walkInterfacesOperStatusResult;
    }

    @Override
    public WalkResult walkSystem() {
        return walkResult;
    }

    @Override
    public WalkResult walkInterfacesDescr() {
        return walkInterfacesDescrResult;
    }

    @Override
    public WalkResult walkInterfacesType() {
        return walkInterfacesTypeResult;
    }

    @Override
    public WalkResult walkInterfacesOperStatus() {
        return walkInterfacesOperStatusResult;
    }
}
