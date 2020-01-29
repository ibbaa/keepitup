package de.ibba.keepitup.test.mock;

import java.util.Calendar;
import java.util.GregorianCalendar;

import de.ibba.keepitup.service.ITimeService;

public class MockTimeService implements ITimeService {

    private long timestamp;
    private long timestamp2;
    private boolean toggle;

    public MockTimeService() {
        Calendar calendar = new GregorianCalendar(1970, Calendar.JANUARY, 1, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 1);
        timestamp = calendar.getTimeInMillis();
        timestamp2 = calendar.getTimeInMillis();
        toggle = false;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setTimestamp2(long timestamp2) {
        this.timestamp2 = timestamp2;
    }

    @Override
    public long getCurrentTimestamp() {
        long returnedTimestamp = toggle ? timestamp2 : timestamp;
        toggle = !toggle;
        return returnedTimestamp;
    }
}
