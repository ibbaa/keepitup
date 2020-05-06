package de.ibba.keepitup.service;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.ibba.keepitup.test.mock.MockFuture;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class NetworkTaskProcessPoolTest {

    private NetworkTaskProcessPool processPool;

    @Before
    public void beforeEachTestMethod() {
        processPool = new NetworkTaskProcessPool();
    }

    @Test
    public void testPoolCancel() {
        MockFuture<?> future1 = new MockFuture<>();
        MockFuture<?> future2 = new MockFuture<>();
        MockFuture<?> future3 = new MockFuture<>();
        MockFuture<?> future4 = new MockFuture<>();
        processPool.pool(1, future1);
        processPool.pool(1, future2);
        processPool.pool(1, future3);
        processPool.pool(2, future4);
        processPool.cancel(1);
        assertTrue(future1.isCancelled());
        assertTrue(future2.isCancelled());
        assertTrue(future3.isCancelled());
        assertFalse(future4.isCancelled());
        processPool.cancel(2);
        assertTrue(future4.isCancelled());
    }

    @Test
    public void testCleanUp() {
        MockFuture<?> future1 = new MockFuture<>();
        MockFuture<?> future2 = new MockFuture<>();
        MockFuture<?> future3 = new MockFuture<>();
        MockFuture<?> future4 = new MockFuture<>();
        future1.setDone(true);
        future4.setDone(true);
        processPool.pool(1, future1);
        processPool.pool(1, future2);
        processPool.pool(1, future3);
        processPool.pool(1, future4);
        processPool.cancel(1);
        assertFalse(future1.isCancelled());
        assertTrue(future2.isCancelled());
        assertTrue(future3.isCancelled());
        assertTrue(future4.isCancelled());
        future2.setCancelled(false);
        processPool.cancel(1);
        assertFalse(future1.isCancelled());
        assertFalse(future2.isCancelled());
        assertTrue(future3.isCancelled());
        assertTrue(future4.isCancelled());
    }

    @Test
    public void testCleanUpMultipleSchedulerIds() {
        MockFuture<?> future1 = new MockFuture<>();
        MockFuture<?> future2 = new MockFuture<>();
        MockFuture<?> future3 = new MockFuture<>();
        MockFuture<?> future4 = new MockFuture<>();
        future1.setDone(true);
        future2.setDone(true);
        future3.setDone(true);
        future4.setDone(true);
        processPool.pool(1, future1);
        processPool.pool(2, future2);
        processPool.pool(3, future3);
        processPool.pool(4, future4);
        processPool.cancel(1);
        assertFalse(future1.isCancelled());
        assertFalse(future2.isCancelled());
        assertFalse(future3.isCancelled());
        assertFalse(future4.isCancelled());
    }

    @Test
    public void testCleanUpMultipleSchedulerIdsAndFutures() {
        MockFuture<?> future1 = new MockFuture<>();
        MockFuture<?> future2 = new MockFuture<>();
        MockFuture<?> future3 = new MockFuture<>();
        processPool.pool(1, future1);
        processPool.pool(2, future2);
        future1.setDone(true);
        processPool.pool(2, future3);
        processPool.cancel(2);
        assertFalse(future1.isCancelled());
        assertTrue(future2.isCancelled());
        assertTrue(future3.isCancelled());
    }
}
