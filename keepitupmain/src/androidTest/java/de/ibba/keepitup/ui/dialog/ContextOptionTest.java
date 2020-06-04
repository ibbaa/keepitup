package de.ibba.keepitup.ui.dialog;

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
