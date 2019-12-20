package de.ibba.keepitup.logging;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import de.ibba.keepitup.BuildConfig;

public class Dump {

    private static final ReentrantReadWriteLock dumpLock = new ReentrantReadWriteLock();

    private static IDump dump;

    public static void initialize(IDump dump) {
        dumpLock.writeLock().lock();
        Dump.dump = dump;
        dumpLock.writeLock().unlock();
    }

    public static IDump getDump() {
        dumpLock.readLock().lock();
        try {
            return dump;
        } finally {
            dumpLock.readLock().unlock();
        }
    }

    public static void dump(String tag, String message, String baseFileName, IDumpSource source) {
        if (BuildConfig.DEBUG) {
            IDump dump = getDump();
            if (dump != null) {
                dump.dump(tag, message, baseFileName, source);
            }
        }
    }

    public static void dump(String tag, String message, IDumpSource source) {
        dump(tag, message, null, source);
    }

    public static void dump(IDumpSource source) {
        dump(null, null, null, source);
    }
}
