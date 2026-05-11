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

import org.snmp4j.CommunityTarget;
import org.snmp4j.Snmp;
import org.snmp4j.smi.Variable;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestSNMPAccess extends SNMPAccess {

    private Map<String, Variable> subtreeResults;
    private List<String> subtreeErrors;
    private boolean subtreeEmpty;
    private RuntimeException subtreeException;
    private List<Map<String, Variable>> subtreeResultsSequence;
    private List<Boolean> subtreeEmptySequence;
    private List<List<String>> subtreeErrorsSequence;
    private int callCount;

    public TestSNMPAccess(Context context, InetAddress address, int port, SNMPVersion snmpVersion, String community, boolean ip6) {
        super(context, address, port, snmpVersion, community, ip6);
        reset();
    }

    public void reset() {
        subtreeResults = new HashMap<>();
        subtreeErrors = new ArrayList<>();
        subtreeEmpty = false;
        subtreeException = null;
        subtreeResultsSequence = null;
        subtreeEmptySequence = null;
        subtreeErrorsSequence = null;
        callCount = 0;
    }

    public void setSubtreeResults(Map<String, Variable> subtreeResults) {
        this.subtreeResults = subtreeResults;
    }

    public void setSubtreeErrors(List<String> subtreeErrors) {
        this.subtreeErrors = subtreeErrors;
    }

    public void setSubtreeEmpty(boolean subtreeEmpty) {
        this.subtreeEmpty = subtreeEmpty;
    }

    public void setSubtreeException(RuntimeException subtreeException) {
        this.subtreeException = subtreeException;
    }

    public void setSubtreeResultsSequence(List<Map<String, Variable>> subtreeResultsSequence) {
        this.subtreeResultsSequence = subtreeResultsSequence;
    }

    public void setSubtreeEmptySequence(List<Boolean> subtreeEmptySequence) {
        this.subtreeEmptySequence = subtreeEmptySequence;
    }

    public void setSubtreeErrorsSequence(List<List<String>> subtreeErrorsSequence) {
        this.subtreeErrorsSequence = subtreeErrorsSequence;
    }

    @Override
    protected boolean fetchAndProcessSubtree(Snmp snmp, CommunityTarget<?> target, String oid, Map<String, Variable> results, List<String> errors) {
        if (subtreeException != null) {
            throw subtreeException;
        }
        boolean empty = subtreeEmpty;
        if (subtreeEmptySequence != null && callCount < subtreeEmptySequence.size()) {
            empty = subtreeEmptySequence.get(callCount);
        }
        if (empty) {
            callCount++;
            return false;
        }
        Map<String, Variable> callResults = subtreeResults;
        if (subtreeResultsSequence != null && callCount < subtreeResultsSequence.size()) {
            callResults = subtreeResultsSequence.get(callCount);
        }
        results.putAll(callResults);
        List<String> callErrors = subtreeErrors;
        if (subtreeErrorsSequence != null && callCount < subtreeErrorsSequence.size()) {
            callErrors = subtreeErrorsSequence.get(callCount);
        }
        errors.addAll(callErrors);
        callCount++;
        return true;
    }
}
