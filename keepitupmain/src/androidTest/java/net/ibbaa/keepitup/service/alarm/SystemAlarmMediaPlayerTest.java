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

package net.ibbaa.keepitup.service.alarm;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class SystemAlarmMediaPlayerTest {

    @Test
    public void testIsPlaying() {
        SystemAlarmMediaPlayer mediaPlayer = new SystemAlarmMediaPlayer(TestRegistry.getContext());
        assertFalse(mediaPlayer.isPlaying());
        mediaPlayer.playAlarm();
        assertTrue(mediaPlayer.isPlaying());
        mediaPlayer.stopAlarm();
        assertFalse(mediaPlayer.isPlaying());
    }
}
