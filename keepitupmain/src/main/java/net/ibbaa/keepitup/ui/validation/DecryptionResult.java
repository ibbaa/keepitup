/*
 * Copyright (c) 2026 Alwin Ibba
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

package net.ibbaa.keepitup.ui.validation;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.NonNull;

import java.util.Objects;

public class DecryptionResult {

    private final String networkTask;
    private final String message;

    public DecryptionResult(String networkTask, String message) {
        this.networkTask = networkTask;
        this.message = message;
    }

    public DecryptionResult(PersistableBundle bundle) {
        this(new Bundle(bundle));
    }

    public DecryptionResult(Bundle bundle) {
        this.networkTask = bundle.getString("networkTask");
        this.message = bundle.getString("message");
    }

    public String getNetworkTask() {
        return networkTask;
    }

    public String getMessage() {
        return message;
    }

    public PersistableBundle toPersistableBundle() {
        PersistableBundle bundle = new PersistableBundle();
        bundle.putString("networkTask", networkTask);
        bundle.putString("message", message);
        return bundle;
    }

    public Bundle toBundle() {
        return new Bundle(toPersistableBundle());
    }

    public boolean isEqual(DecryptionResult other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        return Objects.equals(networkTask, other.networkTask) && Objects.equals(message, other.message);
    }

    @NonNull
    @Override
    public String toString() {
        return "DecryptionResult{" +
                "networkTask='" + networkTask + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
