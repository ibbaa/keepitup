/*
 * Copyright (c) 2024. Alwin Ibba
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

package net.ibbaa.keepitup.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import net.ibbaa.keepitup.logging.Log;

public class GenericActivityResultLauncher {

    final private ComponentActivity activity;
    final private Consumer<Uri> callback;
    ActivityResultLauncher<Intent> activityResultLauncher;

    public GenericActivityResultLauncher(ComponentActivity activity, Consumer<Uri> callback) {
        this.activity = activity;
        this.callback = callback;
        init();
    }

    public void init() {
        Log.d(GenericActivityResultLauncher.class.getName(), "init");
        activityResultLauncher = activity.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::onActivityResult);
    }

    public void launch(Intent intent) {
        Log.d(GenericActivityResultLauncher.class.getName(), "launch");
        activityResultLauncher.launch(intent);
    }

    private void onActivityResult(ActivityResult result) {
        Log.d(GenericActivityResultLauncher.class.getName(), "onActivityResult, result is " + result);
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent intent = result.getData();
            if (intent != null) {
                callback.accept(intent.getData());
            }
        }
    }

    @FunctionalInterface
    public interface Consumer<S> {
        @SuppressWarnings({"unused"})
        void accept(S result);
    }
}
