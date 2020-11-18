package de.ibba.keepitup.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import de.ibba.keepitup.logging.Log;

public class NetworkTaskProcessPool {

    private final Map<Integer, List<Future<?>>> futurePool;

    public NetworkTaskProcessPool() {
        this.futurePool = new HashMap<>();
    }

    public void reset() {
        futurePool.clear();
    }

    public synchronized void pool(int schedulerId, Future<?> future) {
        Log.d(NetworkTaskProcessPool.class.getName(), "pool, schedulerId is " + schedulerId);
        cleanUp();
        List<Future<?>> futureList = futurePool.get(schedulerId);
        Log.d(NetworkTaskProcessPool.class.getName(), "futureList is " + futureList);
        if (futureList == null) {
            futureList = new ArrayList<>();
            futurePool.put(schedulerId, futureList);
        }
        futureList.add(future);
    }

    public synchronized void cancel(int schedulerId) {
        Log.d(NetworkTaskProcessPool.class.getName(), "cancel, schedulerId is " + schedulerId);
        List<Future<?>> futureList = futurePool.get(schedulerId);
        Log.d(NetworkTaskProcessPool.class.getName(), "futureList is " + futureList);
        if (futureList == null) {
            return;
        }
        for (Future<?> currentFuture : futureList) {
            currentFuture.cancel(true);
        }
        futurePool.remove(schedulerId);
    }

    public synchronized void cancelAll() {
        Log.d(NetworkTaskProcessPool.class.getName(), "cancelAll");
        Iterator<Integer> keyIterator = new HashSet<>(futurePool.keySet()).iterator();
        while (keyIterator.hasNext()) {
            List<Future<?>> futureList = futurePool.get(keyIterator.next());
            if (futureList != null) {
                for (Future<?> currentFuture : futureList) {
                    if (currentFuture != null && !currentFuture.isDone() && !currentFuture.isCancelled()) {
                        currentFuture.cancel(true);
                    }
                }
            }
        }
    }

    public synchronized void cleanUp() {
        Log.d(NetworkTaskProcessPool.class.getName(), "cleanUp");
        Iterator<Integer> keyIterator = new HashSet<>(futurePool.keySet()).iterator();
        while (keyIterator.hasNext()) {
            cleanUp(keyIterator.next());
        }
    }

    public synchronized void cleanUp(int schedulerId) {
        Log.d(NetworkTaskProcessPool.class.getName(), "cleanUp, schedulerId is " + schedulerId);
        List<Future<?>> futureList = futurePool.get(schedulerId);
        Log.d(NetworkTaskProcessPool.class.getName(), "futureList is " + futureList);
        if (futureList == null) {
            return;
        }
        List<Future<?>> cleanedFutureList = new ArrayList<>();
        for (Future<?> currentFuture : futureList) {
            if (currentFuture != null && !currentFuture.isDone() && !currentFuture.isCancelled()) {
                cleanedFutureList.add(currentFuture);
            }
        }
        if (cleanedFutureList.isEmpty()) {
            futurePool.remove(schedulerId);
        } else {
            futurePool.put(schedulerId, cleanedFutureList);
        }
    }

    public synchronized boolean hasActive() {
        Log.d(NetworkTaskProcessPool.class.getName(), "hasActive");
        Iterator<Integer> keyIterator = new HashSet<>(futurePool.keySet()).iterator();
        while (keyIterator.hasNext()) {
            List<Future<?>> futureList = futurePool.get(keyIterator.next());
            if (futureList != null) {
                for (Future<?> currentFuture : futureList) {
                    if (currentFuture != null && !currentFuture.isDone() && !currentFuture.isCancelled()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
