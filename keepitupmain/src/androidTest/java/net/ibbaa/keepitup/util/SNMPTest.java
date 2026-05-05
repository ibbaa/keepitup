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

package net.ibbaa.keepitup.util;

import net.ibbaa.keepitup.model.SNMPVersion;
import net.ibbaa.keepitup.service.network.SNMPAccess;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.Test;

import java.net.InetAddress;
import java.util.Map;

public class SNMPTest {

    @Test
    public void testAccess() throws Exception {
        SNMPAccess access = new SNMPAccess(TestRegistry.getContext(), InetAddress.getByName("arbz-switch.ibbaa.lan"), 161, SNMPVersion.V2C, "", false);
        SNMPAccess.WalkResult walkResult = access.walk("1.3.6.1.2.1.1");
        Map<String, String> result = walkResult.result();
    }
}
