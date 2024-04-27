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

package net.ibbaa.keepitup.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AccessTypeTest {

    @Test
    public void testValueMethods() {
        AccessType type = AccessType.PING;
        assertFalse(type.needsPort());
        assertTrue(type.isPing());
        assertFalse(type.isConnect());
        assertFalse(type.isDownload());
        type = AccessType.CONNECT;
        assertTrue(type.needsPort());
        assertFalse(type.isPing());
        assertTrue(type.isConnect());
        assertFalse(type.isDownload());
        type = AccessType.DOWNLOAD;
        assertFalse(type.needsPort());
        assertFalse(type.isPing());
        assertFalse(type.isConnect());
        assertTrue(type.isDownload());
    }

    @Test
    public void testForCode() {
        assertEquals(AccessType.PING, AccessType.forCode(AccessType.PING.getCode()));
        assertEquals(AccessType.CONNECT, AccessType.forCode(AccessType.CONNECT.getCode()));
        assertEquals(AccessType.DOWNLOAD, AccessType.forCode(AccessType.DOWNLOAD.getCode()));
        assertNull(AccessType.forCode(AccessType.DOWNLOAD.getCode() + 1));
    }
}
