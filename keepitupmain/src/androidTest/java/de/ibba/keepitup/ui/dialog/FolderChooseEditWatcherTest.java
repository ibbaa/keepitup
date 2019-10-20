package de.ibba.keepitup.ui.dialog;

import android.text.SpannableStringBuilder;
import android.widget.TextView;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.ibba.keepitup.test.mock.TestRegistry;

import static org.junit.Assert.assertEquals;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class FolderChooseEditWatcherTest {

    @Test
    public void testChangeAbsoluteFolderText() {
        TextView testAbsoluteFolderText = new TextView(TestRegistry.getContext());
        testAbsoluteFolderText.setText("test");
        FolderChooseEditWatcher watcher = new FolderChooseEditWatcher("root", testAbsoluteFolderText);
        watcher.afterTextChanged(null);
        assertEquals("test", testAbsoluteFolderText.getText());
        watcher.afterTextChanged(new SpannableStringBuilder(""));
        assertEquals("root", testAbsoluteFolderText.getText());
        watcher.afterTextChanged(new SpannableStringBuilder("test"));
        assertEquals("root/test", testAbsoluteFolderText.getText());
    }
}
