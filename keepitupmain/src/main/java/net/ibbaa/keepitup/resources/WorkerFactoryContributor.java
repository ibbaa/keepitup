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

package net.ibbaa.keepitup.resources;

import android.content.Context;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;

import java.util.Objects;

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
