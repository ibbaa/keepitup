package de.ibba.keepitup.resources;

import android.content.Context;

import java.util.Objects;

import de.ibba.keepitup.R;
import de.ibba.keepitup.logging.Log;

public class ServiceFactoryContributor {

    private final Context context;

    public ServiceFactoryContributor(Context context) {
        this.context = context;
    }

    public ServiceFactory createServiceFactory() {
        String factoryClassName = context.getResources().getString(R.string.service_factory_implementation);
        Log.d(ServiceFactoryContributor.class.getName(), "Service factory class name is " + factoryClassName);
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            if (classloader == null) {
                classloader = this.getClass().getClassLoader();
            }
            Class<?> factoryClass = Objects.requireNonNull(classloader).loadClass(factoryClassName);
            Log.d(ServiceFactoryContributor.class.getName(), "Loaded service factory class is " + factoryClass.getName());
            return (ServiceFactory) factoryClass.newInstance();
        } catch (Exception exc) {
            Log.e(ServiceFactoryContributor.class.getName(), "Error creating service factory", exc);
            throw new RuntimeException(exc);
        }
    }
}
