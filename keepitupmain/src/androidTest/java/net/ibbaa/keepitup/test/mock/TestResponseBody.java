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

import androidx.annotation.NonNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;

public class TestResponseBody extends ResponseBody {

    private final InputStream bodyStream;

    public TestResponseBody(InputStream bodyStream) {
        this.bodyStream = bodyStream;
    }

    @Override
    public MediaType contentType() {
        return MediaType.parse("application/octet-stream");
    }

    @Override
    public long contentLength() {
        return bodyStream != null ? -1 : 0; // unbekannte Länge, blockiert nicht
    }

    @NonNull
    @Override
    public BufferedSource source() {
        return Okio.buffer(new ForwardingSource(Okio.source(bodyStream != null ? bodyStream : new ByteArrayInputStream(new byte[0]))) {
            @Override
            public long read(@NonNull Buffer sink, long byteCount) throws IOException {
                return super.read(sink, byteCount);
            }
        });
    }
}
