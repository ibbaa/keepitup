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

import java.util.List;
import java.util.concurrent.Callable;

public class HeaderBulkDeleteTask implements Callable<Header>  {

    private final Context context;
    private final List<Header> headers;

    public HeaderBulkDeleteTask(Context context, List<Header> headers) {
        this.context = context;
        this.headers = headers;
    }

    @Override
    public Header call() throws Exception {
        Log.d(HeaderBulkDeleteTask.class.getName(), "call");
        HeaderDAO headerDAO = new HeaderDAO(context);
        try {
            headerDAO.deleteHeaders(headers);
        } catch (Exception exc) {
            Log.e(HeaderBulkDeleteTask.class.getName(), "Error deleting headers from database", exc);
        }
        return null;
    }
}
