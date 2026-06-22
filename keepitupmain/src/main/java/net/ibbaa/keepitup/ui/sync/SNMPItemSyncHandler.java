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

import net.ibbaa.keepitup.db.SNMPItemDAO;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.SNMPItem;

import java.util.List;

public class SNMPItemSyncHandler {

    private final Context context;

    public SNMPItemSyncHandler(Context context) {
        this.context = context;
    }

    public DBSyncResult synchronizeSNMPItems(List<SNMPItem> newSNMPItems, List<SNMPItem> dbSNMPItems) {
        Log.d(SNMPItemSyncHandler.class.getName(), "synchronizeSNMPItems");
        try {
            DBSyncHandler<SNMPItem> syncHandler = new DBSyncHandler<>();
            List<DBSyncHandler.ActionWrapper<SNMPItem>> snmpItemActions = syncHandler.retrieveSyncList(newSNMPItems, dbSNMPItems);
            for (DBSyncHandler.ActionWrapper<SNMPItem> actionWrapper : snmpItemActions) {
                if (DBSyncHandler.Action.INSERT.equals(actionWrapper.action())) {
                    insertSNMPItem(actionWrapper.object());
                } else if (DBSyncHandler.Action.UPDATE.equals(actionWrapper.action())) {
                    updateSNMPItem(actionWrapper.object());
                } else if (DBSyncHandler.Action.DELETE.equals(actionWrapper.action())) {
                    deleteSNMPItem(actionWrapper.object());
                } else {
                    Log.e(SNMPItemSyncHandler.class.getName(), "Unknown action " + actionWrapper.action());
                }
            }
            return new DBSyncResult(true, !snmpItemActions.isEmpty());
        } catch (Exception exc) {
            Log.e(SNMPItemSyncHandler.class.getName(), "Error synchronizing SNMP items.", exc);
            return new DBSyncResult(false, true);
        }
    }

    private void insertSNMPItem(SNMPItem snmpItem) {
        Log.d(SNMPItemSyncHandler.class.getName(), "insertSNMPItem, snmpItem = " + snmpItem);
        SNMPItemDAO snmpItemDAO = new SNMPItemDAO(context);
        snmpItemDAO.insertSNMPItem(snmpItem);
    }

    private void updateSNMPItem(SNMPItem snmpItem) {
        Log.d(SNMPItemSyncHandler.class.getName(), "updateSNMPItem, snmpItem = " + snmpItem);
        SNMPItemDAO snmpItemDAO = new SNMPItemDAO(context);
        snmpItemDAO.updateSNMPItem(snmpItem);
    }

    private void deleteSNMPItem(SNMPItem snmpItem) {
        Log.d(SNMPItemSyncHandler.class.getName(), "deleteSNMPItem, snmpItem = " + snmpItem);
        SNMPItemDAO snmpItemDAO = new SNMPItemDAO(context);
        snmpItemDAO.deleteSNMPItem(snmpItem);
    }
}
