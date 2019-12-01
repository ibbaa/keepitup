package de.ibba.keepitup.test.mock;

import java.util.Calendar;
import java.util.GregorianCalendar;

import de.ibba.keepitup.service.ITimeService;

public class MockTimeService implements ITimeService {

    private long timestamp;

    public MockTimeService() {
        Calendar calendar = new GregorianCalendar(1970, Calendar.JANUARY, 1, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 1);
        timestamp = calendar.getTimeInMillis();
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public long getCurrentTimestamp() {
        return timestamp;
    }
}
