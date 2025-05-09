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

package net.ibbaa.keepitup.test.mock;

import java.io.InputStream;

public class BlockingTestInputStream extends InputStream {

    private final Signal signal;

    public BlockingTestInputStream(Signal signal) {
        this.signal = signal;
    }

    @Override
    public int read() {
        if (signal.doContinue()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException exc) {
                //do nothing
            }
        }
        return 1;
    }

    @FunctionalInterface
    public interface Signal {
        boolean doContinue();
    }
}
