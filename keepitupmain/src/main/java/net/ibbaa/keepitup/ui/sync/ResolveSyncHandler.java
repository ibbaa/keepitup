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

import net.ibbaa.keepitup.db.ResolveDAO;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.Resolve;

import java.util.List;

public class ResolveSyncHandler {

    private final ResolveDAO resolveDAO;

    public ResolveSyncHandler(Context context) {
        this.resolveDAO = new ResolveDAO(context);
    }

    public DBSyncResult synchronizeResolves(long networkTaskId, List<Resolve> newResolves) {
        Log.d(ResolveSyncHandler.class.getName(), "synchronizeResolves for networkTaskId " + networkTaskId);
        try {
            for (int ii = 0; ii < newResolves.size(); ii++) {
                newResolves.get(ii).setIndex(ii);
            }
            resolveDAO.deleteAllResolvesForNetworkTask(networkTaskId);
            resolveDAO.insertResolves(newResolves);
            return new DBSyncResult(true, true);
        } catch (Exception exc) {
            Log.e(ResolveSyncHandler.class.getName(), "Error synchronizing resolve objects.", exc);
            return new DBSyncResult(false, true);
        }
    }
}
