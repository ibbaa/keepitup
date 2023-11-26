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

import net.ibbaa.keepitup.model.Interval;
import net.ibbaa.keepitup.model.Time;

import java.util.List;

public interface IntervalValidator {

    ValidationResult validateDuration(Interval interval);

    ValidationResult validateOverlap(Interval interval, List<Interval> existingIntervals);

    ValidationResult validateInInterval(Time time, List<Interval> existingIntervals);
}
