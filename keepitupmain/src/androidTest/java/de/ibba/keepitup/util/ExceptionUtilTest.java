package de.ibba.keepitup.util;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class ExceptionUtilTest {

    @Test
    public void testGetRootCause() {
        Exception testException = null;
        try {
            testException = new Exception("Test");
            throw testException;
        } catch (Exception exc) {
            assertEquals(testException, ExceptionUtil.getRootCause(exc));
        }
        try {
            testException = new Exception("Test");
            throw new RuntimeException(testException);
        } catch (Exception exc) {
            assertEquals(testException, ExceptionUtil.getRootCause(exc));
        }
        try {
            testException = new Exception("Test");
            throw new Exception(new RuntimeException(testException));
        } catch (Exception exc) {
            assertEquals(testException, ExceptionUtil.getRootCause(exc));
        }
    }

    @Test
    public void testGetLogableMessage() {
        try {
            throw new NullPointerException("Null");
        } catch (Exception exc) {
            assertEquals("NullPointerException: Null", ExceptionUtil.getLogableMessage(exc));
        }
        try {
            throw new IllegalArgumentException("Test");
        } catch (Exception exc) {
            assertEquals("IllegalArgumentException: Test", ExceptionUtil.getLogableMessage(exc));
        }
        try {
            throw new Exception("");
        } catch (Exception exc) {
            assertEquals("java.lang.Exception", ExceptionUtil.getLogableMessage(exc));
        }
    }

}
