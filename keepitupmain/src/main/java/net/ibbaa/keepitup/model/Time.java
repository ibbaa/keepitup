/*
 * Copyright (c) 2025 Alwin Ibba
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

package net.ibbaa.keepitup.model;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.NonNull;

import net.ibbaa.keepitup.util.NumberUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Time {

    private int hour;
    private int minute;

    public Time() {
        this.hour = 0;
        this.minute = 0;
    }

    public Time(PersistableBundle bundle) {
        this(new Bundle(bundle));
    }

    public Time(Bundle bundle) {
        this();
        this.hour = bundle.getInt("hour");
        this.minute = bundle.getInt("minute");
    }

    public Time(Map<String, ?> map) {
        this();
        if (NumberUtil.isValidIntValue(map.get("hour"))) {
            this.hour = NumberUtil.getIntValue(map.get("hour"), 0);
        }
        if (NumberUtil.isValidIntValue(map.get("minute"))) {
            this.minute = NumberUtil.getIntValue(map.get("minute"), 0);
        }
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public PersistableBundle toPersistableBundle() {
        PersistableBundle bundle = new PersistableBundle();
        bundle.putInt("hour", hour);
        bundle.putInt("minute", minute);
        return bundle;
    }

    public Bundle toBundle() {
        return new Bundle(toPersistableBundle());
    }

    public Map<String, ?> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("hour", hour);
        map.put("minute", minute);
        return map;
    }

    public boolean isEqual(Time other) {
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        if (hour != other.hour) {
            return false;
        }
        return Objects.equals(minute, other.minute);
    }

    public boolean isValid() {
        return hour >= 0 && hour <= 23 && minute >= 0 && minute <= 59;
    }

    public boolean isBefore(Time other) {
        if (hour == other.hour) {
            return minute < other.minute;
        }
        return hour < other.hour;
    }

    public boolean isAfter(Time other) {
        if (hour == other.hour) {
            return minute > other.minute;
        }
        return hour > other.hour;
    }

    @NonNull
    @Override
    public String toString() {
        return "Time{" +
                "hour=" + hour +
                ", minute=" + minute +
                '}';
    }
}
