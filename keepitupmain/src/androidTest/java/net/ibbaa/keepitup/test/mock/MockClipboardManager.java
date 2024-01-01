/*
 * Copyright (c) 2024. Alwin Ibba
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

import net.ibbaa.keepitup.ui.clipboard.IClipboardManager;
import net.ibbaa.keepitup.util.NumberUtil;

public class MockClipboardManager implements IClipboardManager {

    private String data;

    public MockClipboardManager() {
        this.data = null;
    }

    @Override
    public boolean hasData() {
        return data != null;
    }

    @Override
    public boolean hasNumericIntegerData() {
        return hasData() && NumberUtil.isValidLongValue(data);
    }

    @Override
    public String getData() {
        return data;
    }

    @Override
    public void putData(String data) {
        this.data = data;
    }

    public void clearData() {
        this.data = null;
    }
}
