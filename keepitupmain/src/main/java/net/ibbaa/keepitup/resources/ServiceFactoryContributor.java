/*
 * Copyright (c) 2022. Alwin Ibba
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

package net.ibbaa.keepitup.resources;

import android.content.Context;

import java.util.Objects;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;

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
