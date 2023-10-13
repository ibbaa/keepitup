/*
 * Copyright (c) 2023. Alwin Ibba
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ibbaa.keepitup.logging;

import net.ibbaa.keepitup.BuildConfig;
import net.ibbaa.phonelog.IDump;
import net.ibbaa.phonelog.IDumpSource;

import java.util.concurrent.locks.ReentrantReadWriteLock;

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
