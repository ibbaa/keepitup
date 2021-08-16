package net.ibbaa.keepitup.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadUtil {

    public static <T> Future<T> exexute(Callable<T> callable) {
        ExecutorService executorService = null;
        try {
            executorService = Executors.newSingleThreadExecutor();
            return executorService.submit(callable);
        } finally {
            if (executorService != null) {
                executorService.shutdown();
            }
        }
    }
}
