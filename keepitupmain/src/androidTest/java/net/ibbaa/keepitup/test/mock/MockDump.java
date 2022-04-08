/*
 * Copyright (c) 2022. Alwin Ibba
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

import net.ibbaa.keepitup.logging.IDump;
import net.ibbaa.keepitup.logging.IDumpSource;

import java.util.ArrayList;
import java.util.List;

public class MockDump implements IDump {

    private final List<DumpCall> dumpCalls;

    public MockDump() {
        dumpCalls = new ArrayList<>();
    }

    @Override
    public void dump(String tag, String message, String baseFileName, IDumpSource source) {
        dumpCalls.add(new DumpCall(tag, message, baseFileName, source));
    }

    public void reset() {
        dumpCalls.clear();
    }

    public boolean wasDumpCalled() {
        return numberDumpCalls() > 0;
    }

    public int numberDumpCalls() {
        return dumpCalls.size();
    }

    public DumpCall getDumpCall(int index) {
        return dumpCalls.get(index);
    }

    public class DumpCall {

        private final String tag;
        private final String message;
        private final String baseFileName;
        private final IDumpSource dumpSource;

        public DumpCall(String tag, String message, String baseFileName, IDumpSource dumpSource) {
            this.tag = tag;
            this.message = message;
            this.baseFileName = baseFileName;
            this.dumpSource = dumpSource;
        }

        public String getTag() {
            return tag;
        }

        public String getMessage() {
            return message;
        }

        public String getBaseFileName() {
            return baseFileName;
        }

        public IDumpSource getDumpSource() {
            return dumpSource;
        }
    }
}
