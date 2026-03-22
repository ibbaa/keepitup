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

package net.ibbaa.keepitup.ui.sync;

import android.content.Context;

import net.ibbaa.keepitup.db.HeaderDAO;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.Header;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HeaderSyncHandler {

    public final static Object LOCK = HeaderSyncHandler.class;

    private static List<Header> headers;

    private final HeaderDAO headerDAO;

    public HeaderSyncHandler(Context context) {
        this.headerDAO = new HeaderDAO(context);
    }

    public List<Header> getHeaders(long networkTaskId) {
        Log.d(HeaderSyncHandler.class.getName(), "getHeaders for networkTaskId " + networkTaskId);
        return headerDAO.readHeadersForNetworkTask(networkTaskId);
    }

    public List<Header> removeInvalidHeaders(List<Header> headers) {
        Log.d(HeaderSyncHandler.class.getName(), "removeInvalidHeaders");
        List<Header> invalidHeaders = getInvalidHeaders(headers);
        headers.removeAll(invalidHeaders);
        return invalidHeaders;
    }

    public List<Header> getInvalidHeaders(List<Header> headers) {
        Log.d(HeaderSyncHandler.class.getName(), "getInvalidHeaders");
        headers = headers != null ? headers : Collections.emptyList();
        List<Header> invalidHeaders = new ArrayList<>(headers.size());
        for (Header currentHeader : headers) {
            if (!currentHeader.isValueValid()) {
                invalidHeaders.add(currentHeader);
            }
        }
        return invalidHeaders;
    }

    public List<Header> getGlobalHeadersCopyForNetworkTask(long networkTaskId) {
        Log.d(HeaderSyncHandler.class.getName(), "getGlobalHeadersCopyForNetworkTask");
        List<Header> globalHeaders = getGlobalHeaders();
        List<Header> copyGlobalHeaders = new ArrayList<>(globalHeaders.size());
        for (Header currentHeader : globalHeaders) {
            Header header = new Header(currentHeader);
            header.setNetworkTaskId(networkTaskId);
            copyGlobalHeaders.add(header);
        }
        return copyGlobalHeaders;
    }

    public List<Header> getGlobalHeaders() {
        Log.d(HeaderSyncHandler.class.getName(), "getGlobalHeaders");
        synchronized (LOCK) {
            if (headers == null) {
                headers = headerDAO.readGlobalHeaders();
            }
            return headers;
        }
    }

    public void reset() {
        Log.d(HeaderSyncHandler.class.getName(), "reset");
        synchronized (LOCK) {
            headers = null;
        }
    }

    public DBSyncResult synchronizeHeaders(long networkTaskId, List<Header> newHeaders) {
        Log.d(HeaderSyncHandler.class.getName(), "synchronizeHeaders for networkTaskId " + networkTaskId);
        try {
            newHeaders = excludeNonApplicable(newHeaders, networkTaskId);
            List<Header> dbHeaders = new ArrayList<>(networkTaskId < 0 ? getGlobalHeaders() : getHeaders(networkTaskId));
            DBSyncHandler<Header> syncHandler = new DBSyncHandler<>();
            List<DBSyncHandler.ActionWrapper<Header>> headerActions = syncHandler.retrieveSyncList(newHeaders, dbHeaders);
            for (DBSyncHandler.ActionWrapper<Header> actionWrapper : headerActions) {
                if (DBSyncHandler.Action.INSERT.equals(actionWrapper.action())) {
                    insertHeader(actionWrapper.object());
                } else if (DBSyncHandler.Action.UPDATE.equals(actionWrapper.action())) {
                    updateHeader(actionWrapper.object());
                } else if (DBSyncHandler.Action.DELETE.equals(actionWrapper.action())) {
                    deleteHeader(actionWrapper.object());
                } else {
                    Log.e(HeaderSyncHandler.class.getName(), "Unknown action " + actionWrapper.action());
                }
            }
            return new DBSyncResult(true, !headerActions.isEmpty());
        } catch (Exception exc) {
            Log.e(HeaderSyncHandler.class.getName(), "Error synchronizing headers.", exc);
            return new DBSyncResult(false, true);
        }
    }

    private List<Header> excludeNonApplicable(List<Header> newHeaders, long networkTaskId) {
        List<Header> headers = new ArrayList<>();
        for (Header header : newHeaders) {
            if (networkTaskId < 0 && header.getNetworkTaskId() < 0) {
                headers.add(header);
            } else if (networkTaskId >= 0 && header.getNetworkTaskId() == networkTaskId) {
                headers.add(header);
            }
        }
        return headers;
    }

    private void insertHeader(Header header) {
        Log.d(HeaderSyncHandler.class.getName(), "insertHeader, header) = " + header);
        headerDAO.insertHeader(header);
    }

    private void updateHeader(Header header) {
        Log.d(HeaderSyncHandler.class.getName(), "updateHeader, header = " + header);
        headerDAO.updateHeader(header);
    }

    private void deleteHeader(Header header) {
        Log.d(HeaderSyncHandler.class.getName(), "deleteHeader, header = " + header);
        headerDAO.deleteHeader(header);
    }
}
