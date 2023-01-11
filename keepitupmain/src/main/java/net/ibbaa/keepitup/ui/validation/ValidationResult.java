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

package net.ibbaa.keepitup.ui.validation;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.NonNull;

import java.util.Objects;

public class ValidationResult {

    private final boolean validationSuccessful;
    private final String fieldName;
    private final String message;

    public ValidationResult(boolean validationSuccessful, String fieldName, String message) {
        this.validationSuccessful = validationSuccessful;
        this.fieldName = fieldName;
        this.message = message;
    }

    public ValidationResult(PersistableBundle bundle) {
        this(new Bundle(bundle));
    }

    public ValidationResult(Bundle bundle) {
        this.validationSuccessful = bundle.getInt("validationSuccessful") >= 1;
        this.fieldName = bundle.getString("fieldName");
        this.message = bundle.getString("message");
    }

    public boolean isValidationSuccessful() {
        return validationSuccessful;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getMessage() {
        return message;
    }

    public PersistableBundle toPersistableBundle() {
        PersistableBundle bundle = new PersistableBundle();
        bundle.putInt("validationSuccessful", validationSuccessful ? 1 : 0);
        bundle.putString("fieldName", fieldName);
        bundle.putString("message", message);
        return bundle;
    }

    public Bundle toBundle() {
        return new Bundle(toPersistableBundle());
    }

    public boolean isEqual(ValidationResult other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        return validationSuccessful == other.validationSuccessful && Objects.equals(fieldName, other.fieldName) && Objects.equals(message, other.message);
    }

    @NonNull
    @Override
    public String toString() {
        return "ValidationResult{" +
                "validationSuccessful=" + validationSuccessful +
                ", fieldName='" + fieldName + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
