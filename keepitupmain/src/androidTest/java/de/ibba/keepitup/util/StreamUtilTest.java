package de.ibba.keepitup.util;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.google.common.base.Charsets;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;

import static org.junit.Assert.assertEquals;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class StreamUtilTest {

    @Test
    public void testInputStreamToString() throws Exception {
        ByteArrayInputStream stream = new ByteArrayInputStream("Test".getBytes(Charsets.US_ASCII));
        assertEquals("Test", StreamUtil.inputStreamToString(stream, Charsets.US_ASCII));
    }
}
