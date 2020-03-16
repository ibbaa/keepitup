package de.ibba.keepitup.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public class NetworkTaskProcessPool {

    private Map<Integer, List<Future<?>>> futurePool;

    public NetworkTaskProcessPool() {
        this.futurePool = new HashMap<>();
    }

    public synchronized void pool(int schedulerId, Future<?> future) {
        cleanUp();
        List<Future<?>> futureList = futurePool.get(schedulerId);
        if (futureList == null) {
            futureList = new ArrayList<>();
            futurePool.put(schedulerId, futureList);
        }
        futureList.add(future);
    }

    public synchronized void cancel(int schedulerId) {
        List<Future<?>> futureList = futurePool.get(schedulerId);
        if (futureList == null) {
            return;
        }
        for (Future<?> currentFuture : futureList) {
            currentFuture.cancel(true);
        }
        futurePool.remove(schedulerId);
    }

    public void cleanUp() {
        Iterator<Integer> keyIterator = futurePool.keySet().iterator();
        while (keyIterator.hasNext()) {
            cleanUp(keyIterator.next());
        }
    }

    public void cleanUp(int schedulerId) {
        List<Future<?>> futureList = futurePool.get(schedulerId);
        if (futureList == null) {
            return;
        }
        List<Future<?>> cleanedFutureList = new ArrayList<>();
        for (Future<?> currentFuture : futureList) {
            if (!currentFuture.isDone() && !currentFuture.isCancelled()) {
                cleanedFutureList.add(currentFuture);
            }
        }
        if (cleanedFutureList.isEmpty()) {
            futurePool.remove(schedulerId);
        } else {
            futurePool.put(schedulerId, cleanedFutureList);
        }
    }
}
