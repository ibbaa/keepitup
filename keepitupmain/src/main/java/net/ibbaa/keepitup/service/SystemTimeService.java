package net.ibbaa.keepitup.service;

public class SystemTimeService implements ITimeService {

    @Override
    public long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }
}
