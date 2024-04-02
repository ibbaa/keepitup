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

package net.ibbaa.keepitup.ui.validation;

import static org.junit.Assert.assertEquals;

import android.graphics.Color;
import android.widget.NumberPicker;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class NumberPickerColorListenerTest {

    @Test
    public void testChangeTextColor() {
        NumberPicker numberPicker1 = new NumberPicker(TestRegistry.getContext());
        NumberPicker numberPicker2 = new NumberPicker(TestRegistry.getContext());
        NumberPickerColorListener listener = new NumberPickerColorListener(Arrays.asList(numberPicker1, numberPicker2), this::validateTrue, Color.BLACK, Color.RED);
        listener.onValueChange(numberPicker1, 1, 0);
        assertEquals(Color.BLACK, numberPicker1.getTextColor());
        assertEquals(Color.BLACK, numberPicker2.getTextColor());
        listener = new NumberPickerColorListener(Arrays.asList(numberPicker1, numberPicker2), this::validateFalse, Color.BLACK, Color.RED);
        listener.onValueChange(numberPicker2, 1, 0);
        assertEquals(Color.RED, numberPicker1.getTextColor());
        assertEquals(Color.RED, numberPicker2.getTextColor());
    }

    @SuppressWarnings({"SameReturnValue", "unused"})
    private boolean validateTrue(NumberPicker numberPicker) {
        return true;
    }

    @SuppressWarnings({"SameReturnValue", "unused"})
    private boolean validateFalse(NumberPicker numberPicker) {
        return false;
    }
}
