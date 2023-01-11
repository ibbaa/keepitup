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

package net.ibbaa.keepitup.test.mock;

import net.ibbaa.keepitup.service.ITimeService;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class MockTimeService implements ITimeService {

    private long timestamp;
    private long timestamp2;
    private boolean toggle;

    public MockTimeService() {
        Calendar calendar = new GregorianCalendar(1970, Calendar.JANUARY, 1, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 1);
        timestamp = calendar.getTimeInMillis();
        timestamp2 = calendar.getTimeInMillis();
        toggle = false;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setTimestamp2(long timestamp2) {
        this.timestamp2 = timestamp2;
    }

    @Override
    public long getCurrentTimestamp() {
        long returnedTimestamp = toggle ? timestamp2 : timestamp;
        toggle = !toggle;
        return returnedTimestamp;
    }
}
