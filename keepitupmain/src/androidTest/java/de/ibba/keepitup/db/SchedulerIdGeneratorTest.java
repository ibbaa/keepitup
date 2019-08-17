package de.ibba.keepitup.db;

import android.database.sqlite.SQLiteDatabase;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.ibba.keepitup.model.AccessType;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.test.mock.TestRegistry;
import de.ibba.keepitup.test.mock.TestSchedulerIdGenerator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class SchedulerIdGeneratorTest {

    private NetworkTaskDAO networkTaskDAO;
    private SQLiteDatabase db;

    @Before
    public void beforeEachTestMethod() {
        networkTaskDAO = new NetworkTaskDAO(TestRegistry.getContext());
        networkTaskDAO.deleteAllNetworkTasks();
        db = new DBOpenHelper(TestRegistry.getContext()).getWritableDatabase();
    }

    @After
    public void afterEachTestMethod() {
        networkTaskDAO.deleteAllNetworkTasks();
        db.close();
    }

    @Test
    public void testCreateUniqueSchedulerId() {
        SchedulerIdGenerator idGenerator = new SchedulerIdGenerator(TestRegistry.getContext());
        SchedulerIdGenerator.SchedulerId schedulerId1 = idGenerator.createUniqueSchedulerId(db);
        SchedulerIdGenerator.SchedulerId schedulerId2 = idGenerator.createUniqueSchedulerId(db);
        assertTrue(schedulerId1.isValid());
        assertTrue(schedulerId2.isValid());
        assertNotEquals(schedulerId1.getId(), schedulerId2.getId());
        assertNotEquals(SchedulerIdGenerator.ERROR_SCHEDULER_ID, schedulerId1.getId());
        assertNotEquals(SchedulerIdGenerator.ERROR_SCHEDULER_ID, schedulerId2.getId());
    }

    @Test
    public void testCreateUniqueSchedulerIdCounterExpired() {
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        TestSchedulerIdGenerator idGenerator = new TestSchedulerIdGenerator(TestRegistry.getContext(), task.getSchedulerId());
        SchedulerIdGenerator.SchedulerId schedulerId = idGenerator.createUniqueSchedulerId(db);
        assertFalse(schedulerId.isValid());
    }

    private NetworkTask getNetworkTask() {
        NetworkTask task = new NetworkTask();
        task.setId(0);
        task.setIndex(1);
        task.setSchedulerId(0);
        task.setAddress("127.0.0.1");
        task.setPort(80);
        task.setAccessType(AccessType.PING);
        task.setInterval(15);
        task.setOnlyWifi(false);
        task.setNotification(true);
        task.setRunning(true);
        return task;
    }
}
