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

package net.ibbaa.keepitup.ui;

import android.content.Context;
import android.content.res.Resources;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.db.HeaderDAO;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.Header;
import net.ibbaa.keepitup.ui.dialog.HeadersDialog;
import net.ibbaa.keepitup.ui.sync.DBSyncHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HeaderHandler {

    public final static Object LOCK = HeaderHandler.class;

    private static List<Header> headers;

    private final HeaderDAO headerDAO;
    private final GlobalSettingsActivity globalSettingsActivity;
    private final HeadersDialog headerDialog;

    public HeaderHandler(GlobalSettingsActivity globalSettingsActivity, HeadersDialog headerDialog) {
        this.globalSettingsActivity = globalSettingsActivity;
        this.headerDialog = headerDialog;
        this.headerDAO = new HeaderDAO(globalSettingsActivity);
    }

    public HeaderHandler(Context context) {
        this.globalSettingsActivity = null;
        this.headerDialog = null;
        this.headerDAO = new HeaderDAO(context);
    }

    public List<Header> getHeaders(long networkTaskId) {
        Log.d(HeaderHandler.class.getName(), "getHeaders for networkTaskId " + networkTaskId);
        return headerDAO.readHeadersForNetworkTask(networkTaskId);
    }

    public List<Header> getGlobalHeaders() {
        Log.d(HeaderHandler.class.getName(), "getGlobalHeaders");
        synchronized (LOCK) {
            if (headers == null) {
                headers = headerDAO.readGlobalHeaders();
            }
            return headers;
        }
    }

    public void reset() {
        Log.d(HeaderHandler.class.getName(), "reset");
        synchronized (LOCK) {
            headers = null;
        }
    }

    public boolean synchronizeHeaders(long networkTaskId) {
        Log.d(HeaderHandler.class.getName(), "synchronizeHeaders for networkTaskId " + networkTaskId);
        if (headerDialog == null) {
            Log.e(HeaderHandler.class.getName(), "headerDialog is null");
            return false;
        }
        try {
            List<Header> newHeaders = headerDialog.getAdapter().getAllItems();
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
                    Log.e(HeaderHandler.class.getName(), "Unknown action " + actionWrapper.action());
                }
            }
            return !headerActions.isEmpty();
        } catch (Exception exc) {
            Log.e(HeaderHandler.class.getName(), "Error synchronizing headers.", exc);
            if (globalSettingsActivity != null) {
                showMessageDialog(getResources().getString(R.string.text_dialog_general_message_synchronize_headers));
            }
            return true;
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
        Log.d(HeaderHandler.class.getName(), "insertHeader, header) = " + header);
        headerDAO.insertHeader(header);
    }

    private void updateHeader(Header header) {
        Log.d(HeaderHandler.class.getName(), "updateHeader, header = " + header);
        headerDAO.updateHeader(header);
    }

    private void deleteHeader(Header header) {
        Log.d(HeaderHandler.class.getName(), "deleteHeader, header = " + header);
        headerDAO.deleteHeader(header);
    }

    private void showMessageDialog(String errorMessage) {
        Objects.requireNonNull(globalSettingsActivity).showMessageDialog(errorMessage);
    }

    private Resources getResources() {
        return Objects.requireNonNull(globalSettingsActivity).getResources();
    }
}
