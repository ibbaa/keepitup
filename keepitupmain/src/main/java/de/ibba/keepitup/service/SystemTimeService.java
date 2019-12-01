package de.ibba.keepitup.service;

public class SystemTimeService implements ITimeService {

    @Override
    public long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }
}
