/*
 * Copyright (c) 2022. Alwin Ibba
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

package net.ibbaa.keepitup.util;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import net.ibbaa.keepitup.test.mock.MockFileManager;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class DebugUtilTest {

    private MockFileManager fileManager;

    @Before
    public void beforeEachTestMethod() {
        fileManager = new MockFileManager();
    }

    @Test
    public void testGetFileLogger() {
        fileManager.setExternalDirectory(null, 0);
        assertNull(DebugUtil.getFileLogger(TestRegistry.getContext(), fileManager));
        fileManager.setExternalDirectory(new File("Test"), 0);
        assertNotNull(DebugUtil.getFileLogger(TestRegistry.getContext(), fileManager));
    }

    @Test
    public void testGetFileDump() {
        fileManager.setExternalDirectory(null, 0);
        assertNull(DebugUtil.getFileDump(TestRegistry.getContext(), fileManager));
        fileManager.setExternalDirectory(new File("Test"), 0);
        assertNotNull(DebugUtil.getFileDump(TestRegistry.getContext(), fileManager));
    }
}
