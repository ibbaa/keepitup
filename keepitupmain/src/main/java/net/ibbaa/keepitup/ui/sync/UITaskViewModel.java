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

import androidx.lifecycle.ViewModel;

import net.ibbaa.keepitup.resources.SystemSetupResult;

public class UITaskViewModel extends ViewModel {

    private final UITaskResultDispatcher<SystemSetupResult> exportDispatcher;
    private final UITaskResultDispatcher<SystemSetupResult> importDispatcher;
    private final UITaskResultDispatcher<Boolean> purgeDispatcher;

    public UITaskViewModel() {
        exportDispatcher = new UITaskResultDispatcher<>();
        importDispatcher = new UITaskResultDispatcher<>();
        purgeDispatcher = new UITaskResultDispatcher<>();
    }

    public UITaskResultDispatcher<SystemSetupResult> getExportDispatcher() {
        return exportDispatcher;
    }

    public UITaskResultDispatcher<SystemSetupResult> getImportDispatcher() {
        return importDispatcher;
    }

    public UITaskResultDispatcher<Boolean> getPurgeDispatcher() {
        return purgeDispatcher;
    }
}
