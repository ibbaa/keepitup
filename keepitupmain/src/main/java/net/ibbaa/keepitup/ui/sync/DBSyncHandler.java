/*
 * Copyright (c) 2025 Alwin Ibba
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

import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.Syncable;
import net.ibbaa.keepitup.ui.IntervalHandler;

import java.util.ArrayList;
import java.util.List;

public class DBSyncHandler<T extends Syncable<T>> {

    public enum Action {
        INSERT,
        UPDATE,
        DELETE
    }

    public List<ActionWrapper<T>> retrieveSyncList(List<T> newList, List<T> oldList) {
        List<T> newSyncList = new ArrayList<>(newList);
        List<T> oldSyncList = new ArrayList<>(oldList);
        List<ActionWrapper<T>> actionList = new ArrayList<>();
        for (T object : newSyncList) {
            Log.d(DBSyncHandler.class.getName(), "Processing " + object);
            if (object.id() < 0) {
                actionList.add(new ActionWrapper<>(object, Action.INSERT));
            } else {
                T oldObject = findById(object.id(), oldSyncList);
                Log.d(IntervalHandler.class.getName(), "Found old object = " + oldObject);
                if (oldObject != null) {
                    if (!object.isEqual(oldObject)) {
                        actionList.add(new ActionWrapper<>(object, Action.UPDATE));
                    }
                    oldSyncList.remove(oldObject);
                } else {
                    Log.e(IntervalHandler.class.getName(), "No object with id " + object.id() + " found");
                }
            }
        }
        for (T object : oldSyncList) {
            actionList.add(new ActionWrapper<>(object, Action.DELETE));
        }
        return actionList;
    }

    private T findById(long id, List<T> objects) {
        Log.d(DBSyncHandler.class.getName(), "findById for id " + id);
        for (T object : objects) {
            if (id == object.id()) {
                return object;
            }
        }
        return null;
    }

    public record ActionWrapper<T>(T object, Action action) {
    }
}
