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

package net.ibbaa.keepitup.model;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.NonNull;

import net.ibbaa.keepitup.util.NumberUtil;

import java.util.HashMap;
import java.util.Map;

public class Interval {

    private Time start;
    private Time end;

    public Interval() {
        this.start = new Time();
        this.end = new Time();
    }

    public Interval(PersistableBundle bundle) {
        this(new Bundle(bundle));
    }

    public Interval(Bundle bundle) {
        this();
        start.setHour(bundle.getInt("hourstart"));
        start.setMinute(bundle.getInt("minutestart"));
        end.setHour(bundle.getInt("hourend"));
        end.setMinute(bundle.getInt("minuteend"));
    }

    public Interval(Map<String, ?> map) {
        this();
        if (NumberUtil.isValidIntValue(map.get("hourstart"))) {
            start.setHour(NumberUtil.getIntValue(map.get("hourstart"), 0));
        }
        if (NumberUtil.isValidIntValue(map.get("minutestart"))) {
            start.setMinute(NumberUtil.getIntValue(map.get("minutestart"), 0));
        }
        if (NumberUtil.isValidIntValue(map.get("hourend"))) {
            end.setHour(NumberUtil.getIntValue(map.get("hourend"), 0));
        }
        if (NumberUtil.isValidIntValue(map.get("minuteend"))) {
            end.setMinute(NumberUtil.getIntValue(map.get("minuteend"), 0));
        }
    }

    public Time getStart() {
        return start;
    }

    public void setStart(Time start) {
        if(start != null) {
            this.start = start;
        } else {
            this.start = new Time();
        }
    }

    public Time getEnd() {
        return end;
    }

    public void setEnd(Time end) {
        if(end != null) {
            this.end = end;
        } else {
            this.end = new Time();
        }
    }

    public PersistableBundle toPersistableBundle() {
        PersistableBundle bundle = new PersistableBundle();
        bundle.putInt("hourstart", start.getHour());
        bundle.putInt("minutestart", start.getMinute());
        bundle.putInt("hourend", end.getHour());
        bundle.putInt("minuteend", end.getMinute());
        return bundle;
    }

    public Bundle toBundle() {
        return new Bundle(toPersistableBundle());
    }

    public Map<String, ?> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("hourstart", start.getHour());
        map.put("minutestart", start.getMinute());
        map.put("hourend", end.getHour());
        map.put("minuteend", end.getMinute());
        return map;
    }

    public boolean isEqual(Interval other) {
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        if (!start.isEqual(other.start)) {
            return false;
        }
        return end.isEqual(other.end);
    }

    public boolean isValid() {
        return start.isValid() && end.isValid() && end.isAfter(start);
    }

    public boolean startsBefore(Interval other) {
        if(!isValid() || !other.isValid()) {
            return false;
        }
        return start.isBefore(other.start);
    }

    public boolean endsAfter(Interval other) {
        if(!isValid() || !other.isValid()) {
            return false;
        }
        return end.isAfter(other.end);
    }

    public boolean isBefore(Interval other) {
        if(!isValid() || !other.isValid()) {
            return false;
        }
        return end.isBefore(other.start);
    }

    public boolean isAfter(Interval other) {
        if(!isValid() || !other.isValid()) {
            return false;
        }
        return start.isAfter(other.end);
    }

    public boolean doesOverlap(Interval other) {
        if(!isValid() || !other.isValid()) {
            return false;
        }
        return !isBefore(other) && !isAfter(other);
    }

    @NonNull
    @Override
    public String toString() {
        return "Interval{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }
}
