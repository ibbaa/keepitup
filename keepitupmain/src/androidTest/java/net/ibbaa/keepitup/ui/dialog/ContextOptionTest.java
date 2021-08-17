/*
 * Copyright (c) 2021. Alwin Ibba
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

package net.ibbaa.keepitup.ui.dialog;

import android.os.Bundle;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class ContextOptionTest {

    @Test
    public void testFromBundle() {
        assertNull(ContextOption.fromBundle(null));
        assertNull(ContextOption.fromBundle(new Bundle()));
        Bundle bundle = new Bundle();
        bundle.putString("abc", "COPY");
        assertNull(ContextOption.fromBundle(bundle));
        bundle.putString("name", "ABC");
        assertNull(ContextOption.fromBundle(bundle));
        bundle.putString("name", "COPY");
        assertEquals(ContextOption.COPY, ContextOption.fromBundle(bundle));
    }

    @Test
    public void testToAndFromBundle() {
        assertEquals(ContextOption.COPY, ContextOption.fromBundle(ContextOption.COPY.toBundle()));
        assertEquals(ContextOption.PASTE, ContextOption.fromBundle(ContextOption.PASTE.toBundle()));
    }
}
