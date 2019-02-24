package de.ibba.keepitup.ui.validation;

import android.graphics.Color;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.widget.EditText;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class TextColorValidatingWatcherTest {

    @Test
    public void testChangeTextColor() {
        EditText testEditText = new EditText(InstrumentationRegistry.getTargetContext());
        TextColorValidatingWatcher watcher = new TextColorValidatingWatcher(testEditText, this::valdiateTrue, Color.BLACK, Color.RED);
        watcher.afterTextChanged(null);
        assertEquals(Color.BLACK, testEditText.getCurrentTextColor());
        watcher = new TextColorValidatingWatcher(testEditText, this::valdiateFalse, Color.BLACK, Color.RED);
        watcher.afterTextChanged(null);
        assertEquals(Color.RED, testEditText.getCurrentTextColor());
    }

    @SuppressWarnings({"unused", "SameReturnValue"})
    private boolean valdiateTrue(EditText editText) {
        return true;
    }

    @SuppressWarnings({"unused", "SameReturnValue"})
    private boolean valdiateFalse(EditText editText) {
        return false;
    }
}
