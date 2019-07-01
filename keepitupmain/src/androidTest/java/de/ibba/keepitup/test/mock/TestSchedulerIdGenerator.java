package de.ibba.keepitup.test.mock;

import android.content.Context;

import de.ibba.keepitup.db.SchedulerIdGenerator;

public class TestSchedulerIdGenerator extends SchedulerIdGenerator {

    private final int id;

    public TestSchedulerIdGenerator(Context context, int id) {
        super(context);
        this.id = id;
    }

    @Override
    public int createSchedulerId() {
        return id;
    }
}
