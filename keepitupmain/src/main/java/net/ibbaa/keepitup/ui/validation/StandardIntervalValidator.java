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

import android.content.Context;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.Interval;
import net.ibbaa.keepitup.model.Time;

import java.util.Collections;
import java.util.List;

public class StandardIntervalValidator implements IntervalValidator {

    private final net.ibbaa.keepitup.model.validation.IntervalValidator validator;
    private final Context context;
    private final List<Interval> existingIntervals;

    public StandardIntervalValidator(Context context, List<Interval> existingIntervals) {
        this.context = context;
        this.validator = new net.ibbaa.keepitup.model.validation.IntervalValidator(context);
        this.existingIntervals = Collections.unmodifiableList(existingIntervals);
    }

    @Override
    public ValidationResult validateDuration() {
        Log.d(StandardHostPortValidator.class.getName(), "validateDuration");
        boolean result = validator.validateDuration(existingIntervals);
        Log.d(StandardHostPortValidator.class.getName(), "Validation result is " + result);
        String message;
        if (result) {
            message = context.getResources().getString(R.string.validation_successful);
        } else {
            int minDuration = context.getResources().getInteger(R.integer.suspension_interval_min_duration);
            message = context.getResources().getString(R.string.suspension_interval_duration, minDuration);
        }
        return new ValidationResult(result, context.getResources().getString(R.string.suspension_interval_field_name), message);
    }

    @Override
    public ValidationResult validateDuration(Interval interval) {
        Log.d(StandardHostPortValidator.class.getName(), "validateDuration, interval is " + interval);
        boolean result = validator.validateDuration(interval);
        Log.d(StandardHostPortValidator.class.getName(), "Validation result is " + result);
        String message;
        if (result) {
            message = context.getResources().getString(R.string.validation_successful);
        } else {
            int minDuration = context.getResources().getInteger(R.integer.suspension_interval_min_duration);
            message = context.getResources().getString(R.string.suspension_interval_duration, minDuration);
        }
        return new ValidationResult(result, context.getResources().getString(R.string.suspension_interval_field_name), message);
    }

    @Override
    public ValidationResult validateOverlapSorted() {
        Log.d(StandardHostPortValidator.class.getName(), "validateOverlapSorted");
        boolean result = validator.validateOverlapSorted(existingIntervals);
        Log.d(StandardHostPortValidator.class.getName(), "Validation result is " + result);
        String message = getOverlapMessage(result);
        return new ValidationResult(result, context.getResources().getString(R.string.suspension_interval_field_name), message);
    }

    @Override
    public ValidationResult validateOverlap(Interval interval) {
        Log.d(StandardHostPortValidator.class.getName(), "validateOverlap, interval is " + interval);
        boolean result = validator.validateOverlap(interval, existingIntervals);
        Log.d(StandardHostPortValidator.class.getName(), "Validation result is " + result);
        String message = getOverlapMessage(result);
        return new ValidationResult(result, context.getResources().getString(R.string.suspension_interval_field_name), message);
    }

    @Override
    public ValidationResult validateInInterval(Time time) {
        Log.d(StandardHostPortValidator.class.getName(), "validateInInterval, time is " + time);
        boolean result = validator.validateInInterval(time, existingIntervals);
        Log.d(StandardHostPortValidator.class.getName(), "Validation result is " + result);
        String message = getOverlapMessage(result);
        return new ValidationResult(result, context.getResources().getString(R.string.suspension_interval_field_name), message);
    }

    private String getOverlapMessage(boolean result) {
        if (result) {
            return context.getResources().getString(R.string.validation_successful);
        }
        int distance = context.getResources().getInteger(R.integer.suspension_interval_min_distance);
        return context.getResources().getString(R.string.suspension_interval_overlap, distance);
    }
}
