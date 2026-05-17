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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.text.InputType;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import net.ibbaa.keepitup.model.Header;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.model.SNMPItem;
import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.keepitup.ui.validation.CredentialInfo;
import net.ibbaa.keepitup.ui.validation.ValidationResult;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;

@SmallTest
@SuppressWarnings({"SequencedCollectionMethodCanBeUsed"})
@RunWith(AndroidJUnit4.class)
public class UIUtilTest {

    @Test
    public void testGetNetworkTaskTitle() {
        NetworkTask task = new NetworkTask();
        task.setName(null);
        task.setIndex(-1);
        assertEquals("Network task", UIUtil.getNetworkTaskTitleName(TestRegistry.getContext(), task, false, false));
        task.setName("");
        assertEquals("Network task", UIUtil.getNetworkTaskTitleName(TestRegistry.getContext(), task, false, false));
        task.setName("Network task");
        assertEquals("Network task", UIUtil.getNetworkTaskTitleName(TestRegistry.getContext(), task, false, false));
        task.setName(null);
        assertEquals("network task", UIUtil.getNetworkTaskTitleName(TestRegistry.getContext(), task, true, false));
        task.setIndex(0);
        task.setName(null);
        assertEquals("Network task 1", UIUtil.getNetworkTaskTitleName(TestRegistry.getContext(), task, false, false));
        assertEquals("network task 1", UIUtil.getNetworkTaskTitleName(TestRegistry.getContext(), task, true, false));
        task.setName("Network task");
        assertEquals("network task 1", UIUtil.getNetworkTaskTitleName(TestRegistry.getContext(), task, true, false));
        task.setName("My Task");
        task.setIndex(-1);
        assertEquals("My Task", UIUtil.getNetworkTaskTitleName(TestRegistry.getContext(), task, false, false));
        assertEquals("My Task", UIUtil.getNetworkTaskTitleName(TestRegistry.getContext(), task, true, false));
        assertEquals("My Task", UIUtil.getNetworkTaskTitleName(TestRegistry.getContext(), task, false, true));
        task.setIndex(2);
        assertEquals("My Task", UIUtil.getNetworkTaskTitleName(TestRegistry.getContext(), task, false, false));
        assertEquals("My Task (network task 3)", UIUtil.getNetworkTaskTitleName(TestRegistry.getContext(), task, false, true));
        assertEquals("My Task (network task 3)", UIUtil.getNetworkTaskTitleName(TestRegistry.getContext(), task, true, true));
    }

    @Test
    public void testSNMPCommunitiesToCredentialInfoList() {
        assertTrue(UIUtil.snmpCommunitiesToCredentialInfoList(TestRegistry.getContext(), null).isEmpty());
        assertTrue(UIUtil.snmpCommunitiesToCredentialInfoList(TestRegistry.getContext(), Collections.emptyList()).isEmpty());
        NetworkTask task1 = new NetworkTask();
        task1.setName("My Task");
        NetworkTask task2 = new NetworkTask();
        task2.setIndex(2);
        List<CredentialInfo> credentialInfoList = UIUtil.snmpCommunitiesToCredentialInfoList(TestRegistry.getContext(), List.of(task1, task2));
        assertEquals(2, credentialInfoList.size());
        assertEquals("My Task", credentialInfoList.get(0).getName());
        assertEquals("SNMP community", credentialInfoList.get(0).getMessage());
        assertEquals("Network task 3", credentialInfoList.get(1).getName());
        assertEquals("SNMP community", credentialInfoList.get(1).getMessage());
    }

    @Test
    public void testHeadersToCredentialInfoList() {
        NetworkTask task = new NetworkTask();
        task.setName("My Task");
        assertTrue(UIUtil.headersToCredentialInfoList(TestRegistry.getContext(), null, Collections.emptyList()).isEmpty());
        assertTrue(UIUtil.headersToCredentialInfoList(TestRegistry.getContext(), task, null).isEmpty());
        assertTrue(UIUtil.headersToCredentialInfoList(TestRegistry.getContext(), task, Collections.emptyList()).isEmpty());
        Header header1 = new Header();
        Header header2 = new Header();
        header1.setName("header1");
        header2.setName("header2");
        List<CredentialInfo> credentialInfoList = UIUtil.headersToCredentialInfoList(TestRegistry.getContext(), task, List.of(header1, header2));
        assertEquals(2, credentialInfoList.size());
        assertEquals("My Task", credentialInfoList.get(0).getName());
        assertEquals("header1 (header)", credentialInfoList.get(0).getMessage());
        assertEquals("My Task", credentialInfoList.get(1).getName());
        assertEquals("header2 (header)", credentialInfoList.get(1).getMessage());
        credentialInfoList = UIUtil.headersToCredentialInfoList(TestRegistry.getContext(), null, List.of(header1, header2));
        assertEquals(2, credentialInfoList.size());
        assertEquals("Default", credentialInfoList.get(0).getName());
        assertEquals("header1 (header)", credentialInfoList.get(0).getMessage());
        assertEquals("Default", credentialInfoList.get(1).getName());
        assertEquals("header2 (header)", credentialInfoList.get(1).getMessage());
    }

    @Test
    public void testIsInputTypeNumber() {
        assertFalse(UIUtil.isInputTypeNumber(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS));
        assertFalse(UIUtil.isInputTypeNumber(InputType.TYPE_CLASS_TEXT));
        assertTrue(UIUtil.isInputTypeNumber(InputType.TYPE_CLASS_NUMBER));
        assertTrue(UIUtil.isInputTypeNumber(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD | InputType.TYPE_TEXT_VARIATION_URI));
        assertTrue(UIUtil.isInputTypeNumber(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD));
        assertTrue(UIUtil.isInputTypeNumber(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL));
    }

    @Test
    public void testGetEmptyIfNotSet() {
        assertNull(UIUtil.getEmptyIfNotSet(TestRegistry.getContext(), null));
        assertEquals("", UIUtil.getEmptyIfNotSet(TestRegistry.getContext(), ""));
        assertEquals("123", UIUtil.getEmptyIfNotSet(TestRegistry.getContext(), "123"));
        assertEquals("", UIUtil.getEmptyIfNotSet(TestRegistry.getContext(), "not set"));
    }

    @Test
    public void testGetNegativeIfNotSet() {
        assertEquals(-1, UIUtil.getNegativeIfNotSet(TestRegistry.getContext(), null));
        assertEquals(-1, UIUtil.getNegativeIfNotSet(TestRegistry.getContext(), ""));
        assertEquals(-1, UIUtil.getNegativeIfNotSet(TestRegistry.getContext(), "not set"));
        assertEquals(-1, UIUtil.getNegativeIfNotSet(TestRegistry.getContext(), "abc"));
        assertEquals(5, UIUtil.getNegativeIfNotSet(TestRegistry.getContext(), "5"));
    }

    @Test
    public void testGetNotSetIfNegative() {
        assertEquals("not set", UIUtil.getNotSetIfNegative(TestRegistry.getContext(), -5));
        assertEquals("123", UIUtil.getNotSetIfNegative(TestRegistry.getContext(), 123));
        assertEquals("0", UIUtil.getNotSetIfNegative(TestRegistry.getContext(), 0));
    }

    @Test
    public void testSNMPRemovedMonitoredSNMPItemsValidationResultList() {
        assertTrue(UIUtil.snmpRemovedMonitoredSNMPItemsValidationResultList(TestRegistry.getContext(), null).isEmpty());
        assertTrue(UIUtil.snmpRemovedMonitoredSNMPItemsValidationResultList(TestRegistry.getContext(), Collections.emptyList()).isEmpty());
        SNMPItem item1 = new SNMPItem();
        item1.setName("eth0");
        List<ValidationResult> result = UIUtil.snmpRemovedMonitoredSNMPItemsValidationResultList(TestRegistry.getContext(), List.of(item1));
        assertEquals(1, result.size());
        assertFalse(result.get(0).isValidationSuccessful());
        assertEquals("Interfaces", result.get(0).getFieldName());
        assertEquals("eth0", result.get(0).getMessage());
        SNMPItem item2 = new SNMPItem();
        item2.setName("eth1");
        result = UIUtil.snmpRemovedMonitoredSNMPItemsValidationResultList(TestRegistry.getContext(), List.of(item1, item2));
        assertEquals(1, result.size());
        assertFalse(result.get(0).isValidationSuccessful());
        assertEquals("Interfaces", result.get(0).getFieldName());
        assertEquals("eth0, eth1", result.get(0).getMessage());
    }

    @Test
    public void testSNMPWalkErrorsToValidationResultList() {
        assertTrue(UIUtil.snmpWalkErrorsToValidationResultList(TestRegistry.getContext(), null).isEmpty());
        assertTrue(UIUtil.snmpWalkErrorsToValidationResultList(TestRegistry.getContext(), Collections.emptyList()).isEmpty());
        List<ValidationResult> result = UIUtil.snmpWalkErrorsToValidationResultList(TestRegistry.getContext(), List.of("error message"));
        assertEquals(1, result.size());
        assertFalse(result.get(0).isValidationSuccessful());
        assertEquals("Error", result.get(0).getFieldName());
        assertEquals("error message", result.get(0).getMessage());
        result = UIUtil.snmpWalkErrorsToValidationResultList(TestRegistry.getContext(), List.of("first error", "second error"));
        assertEquals(2, result.size());
        assertFalse(result.get(0).isValidationSuccessful());
        assertEquals("Error 1", result.get(0).getFieldName());
        assertEquals("first error", result.get(0).getMessage());
        assertFalse(result.get(1).isValidationSuccessful());
        assertEquals("Error 2", result.get(1).getFieldName());
        assertEquals("second error", result.get(1).getMessage());
    }
}
