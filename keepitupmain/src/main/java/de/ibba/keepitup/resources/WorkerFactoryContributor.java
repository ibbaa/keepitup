package de.ibba.keepitup.resources;

import android.content.Context;
import android.util.Log;

import java.util.Objects;

import de.ibba.keepitup.R;

public class WorkerFactoryContributor {

    private final Context context;

    public WorkerFactoryContributor(Context context) {
        this.context = context;
    }

    public WorkerFactory createWorkerFactory() {
        String factoryClassName = context.getResources().getString(R.string.worker_factory_implementation);
        Log.d(WorkerFactoryContributor.class.getName(), "Worker factory class name is " + factoryClassName);
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            if (classloader == null) {
                classloader = this.getClass().getClassLoader();
            }
            Class<?> factoryClass = Objects.requireNonNull(classloader).loadClass(factoryClassName);
            Log.d(WorkerFactoryContributor.class.getName(), "Loaded worker factory class is " + factoryClass.getName());
            return (WorkerFactory) factoryClass.newInstance();
        } catch (Exception exc) {
            Log.e(WorkerFactoryContributor.class.getName(), "Error creating worker factory", exc);
            throw new RuntimeException(exc);
        }
    }
}
