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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.NetworkTask;

import org.junit.Test;
import org.junit.runner.RunWith;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class SNMPUtilTest {

    @Test
    public void testValidateCommunity() {
        assertTrue(SNMPUtil.validateCommunity("public"));
        assertTrue(SNMPUtil.validateCommunity("private"));
        assertTrue(SNMPUtil.validateCommunity("!"));
        assertTrue(SNMPUtil.validateCommunity("~"));
        assertTrue(SNMPUtil.validateCommunity("abc123!@#~"));
        assertTrue(SNMPUtil.validateCommunity(""));
        assertFalse(SNMPUtil.validateCommunity("public private"));
        assertFalse(SNMPUtil.validateCommunity(" "));
        assertFalse(SNMPUtil.validateCommunity("public\tprivate"));
        assertFalse(SNMPUtil.validateCommunity("public\nprivate"));
        assertFalse(SNMPUtil.validateCommunity("public" + (char) 0x7F));
        assertFalse(SNMPUtil.validateCommunity("publicä"));
    }

    @Test
    public void testValidateName() {
        assertTrue(SNMPUtil.validateName(""));
        assertTrue(SNMPUtil.validateName("Alpha"));
        assertTrue(SNMPUtil.validateName("name with spaces"));
        assertTrue(SNMPUtil.validateName("name-with-dashes_and_underscores"));
        assertTrue(SNMPUtil.validateName("name with special chars !@#$%^&*()"));
        assertTrue(SNMPUtil.validateName("nameä"));
        assertTrue(SNMPUtil.validateName("名前"));
        assertFalse(SNMPUtil.validateName("name\twith\ttabs"));
        assertFalse(SNMPUtil.validateName("name\nwith\nnewline"));
        assertFalse(SNMPUtil.validateName("name\rwith\rcarriage"));
        assertFalse(SNMPUtil.validateName("name" + (char) 0x00));
        assertFalse(SNMPUtil.validateName("name" + (char) 0x1F));
        assertFalse(SNMPUtil.validateName("name" + (char) 0x7F));
    }

    @Test
    public void testValidateOID() {
        assertTrue(SNMPUtil.validateOID("1.2"));
        assertTrue(SNMPUtil.validateOID("0.0"));
        assertTrue(SNMPUtil.validateOID("1.3.6.1.2.1"));
        assertTrue(SNMPUtil.validateOID("1.3.6.1.2.1.1.1.0"));
        assertTrue(SNMPUtil.validateOID("100.200.300"));
        assertFalse(SNMPUtil.validateOID(""));
        assertFalse(SNMPUtil.validateOID("1"));
        assertFalse(SNMPUtil.validateOID("1."));
        assertFalse(SNMPUtil.validateOID(".1.2"));
        assertFalse(SNMPUtil.validateOID("1.2."));
        assertFalse(SNMPUtil.validateOID("1.a.2"));
        assertFalse(SNMPUtil.validateOID("abc"));
        assertFalse(SNMPUtil.validateOID("1.2.3a"));
        assertFalse(SNMPUtil.validateOID("1 2 3"));
        assertFalse(SNMPUtil.validateOID("1.2 "));
    }

    @Test
    public void testIsSNMPTask() {
        assertFalse(SNMPUtil.isSNMPTask(null));
        NetworkTask task = new NetworkTask();
        task.setAccessType(null);
        assertFalse(SNMPUtil.isSNMPTask(task));
        task.setAccessType(AccessType.PING);
        assertFalse(SNMPUtil.isSNMPTask(task));
        task.setAccessType(AccessType.CONNECT);
        assertFalse(SNMPUtil.isSNMPTask(task));
        task.setAccessType(AccessType.DOWNLOAD);
        assertFalse(SNMPUtil.isSNMPTask(task));
        task.setAccessType(AccessType.SNMP);
        assertTrue(SNMPUtil.isSNMPTask(task));
    }
}
