package net.ibbaa.keepitup.ui.validation;

import android.graphics.Color;
import android.widget.EditText;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import net.ibbaa.keepitup.test.mock.TestRegistry;

import static org.junit.Assert.assertEquals;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class TextColorValidatingWatcherTest {

    @Test
    public void testChangeTextColor() {
        EditText testEditText = new EditText(TestRegistry.getContext());
        TextColorValidatingWatcher watcher = new TextColorValidatingWatcher(testEditText, this::validateTrue, Color.BLACK, Color.RED);
        watcher.afterTextChanged(null);
        assertEquals(Color.BLACK, testEditText.getCurrentTextColor());
        watcher = new TextColorValidatingWatcher(testEditText, this::validateFalse, Color.BLACK, Color.RED);
        watcher.afterTextChanged(null);
        assertEquals(Color.RED, testEditText.getCurrentTextColor());
    }

    @SuppressWarnings({"SameReturnValue"})
    private boolean validateTrue(EditText editText) {
        return true;
    }

    @SuppressWarnings({"SameReturnValue"})
    private boolean validateFalse(EditText editText) {
        return false;
    }
}
