package de.ibba.keepitup.service;

import java.security.SecureRandom;

public class SchedulerIdGenerator {

    private final static SecureRandom randomGenerator = new SecureRandom();

    public static int createSchedulerId() {
        return randomGenerator.nextInt();
    }
}
