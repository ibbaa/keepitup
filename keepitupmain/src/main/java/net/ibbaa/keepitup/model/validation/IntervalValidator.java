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

package net.ibbaa.keepitup.model.validation;

import android.content.Context;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.Interval;
import net.ibbaa.keepitup.util.TimeUtil;

import java.util.List;

public class IntervalValidator {

    private final Context context;

    public IntervalValidator(Context context) {
        this.context = context;
    }

    public boolean validate(Interval interval, List<Interval> existingIntervals) {
        Log.d(IntervalValidator.class.getName(), "validate interval " + interval);
        return validateDuration(interval) && validateOverlap(interval, existingIntervals);
    }

    public boolean validateDuration(Interval interval) {
        Log.d(IntervalValidator.class.getName(), "validateDuration of interval " + interval);
        int intervalMinDuration = context.getResources().getInteger(R.integer.suspension_interval_min_duration);
        if (!TimeUtil.isDurationMin(interval, intervalMinDuration)) {
            Log.d(NetworkTaskValidator.class.getName(), "Duration is below minimum. Returning false.");
            return false;
        }
        Log.d(NetworkTaskValidator.class.getName(), "Duration is valid. Returning true.");
        return true;
    }

    public boolean validateOverlap(Interval interval, List<Interval> existingIntervals) {
        Log.d(IntervalValidator.class.getName(), "validateDuration of interval " + interval);
        int intervalDistance = context.getResources().getInteger(R.integer.suspension_interval_distance);
        Interval extendedInterval = TimeUtil.extendInterval(interval, intervalDistance);
        Log.d(IntervalValidator.class.getName(), "extendedInterval is " + extendedInterval);
        for (Interval existingInterval : existingIntervals) {
            if (interval.doesOverlap(existingInterval)) {
                Log.d(IntervalValidator.class.getName(), "Interval " + interval + " overlaps interval " + existingInterval + ". Returning false.");
                return false;
            }
            Interval extendedExistingInterval = TimeUtil.extendInterval(existingInterval, intervalDistance);
            if (extendedInterval.doesOverlap(extendedExistingInterval)) {
                Log.d(IntervalValidator.class.getName(), "Extended interval " + extendedInterval + " overlaps extended interval " + extendedExistingInterval + ". Returning false.");
                return false;
            }
        }
        Log.d(NetworkTaskValidator.class.getName(), "Intervals do not overlap. Returning true.");
        return true;
    }
}
