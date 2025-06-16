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

package net.ibbaa.keepitup.util;

import android.content.Context;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;

import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class HTTPUtil {

    private static final Pattern CONTENT_DISPOSITION = Pattern.compile("attachment;\\s*filename\\s*=\\s*\"([^\"]*)\"", Pattern.CASE_INSENSITIVE);
    private static final Pattern MIME_TYPE = Pattern.compile("\\s*(\\w*/\\w*)\\s*", Pattern.CASE_INSENSITIVE);

    public static boolean isHTTPConnection(URLConnection connection) {
        return connection instanceof HttpURLConnection;
    }

    public static boolean isHTTPSConnection(URLConnection connection) {
        return connection instanceof HttpsURLConnection;
    }

    public static boolean isHTTPReturnCodeRedirect(int returnCode) {
        return returnCode == 301 || returnCode == 302 || returnCode == 307 || returnCode == 308;
    }

    public static boolean isHTTPReturnCodeOk(int returnCode) {
        return returnCode >= 200 && returnCode < 300;
    }

    public static String getContentDisposition(Context context, URLConnection connection) {
        return connection.getHeaderField(context.getResources().getString(R.string.http_header_content_disposition));
    }

    public static String getLocation(Context context, URLConnection connection) {
        return connection.getHeaderField(context.getResources().getString(R.string.http_header_content_location));
    }

    public static String getFileNameFromContentDisposition(String contentDisposition) {
        Log.d(HTTPUtil.class.getName(), "getFileNameFromContentDisposition, contentDisposition is " + contentDisposition);
        if (contentDisposition == null) {
            return null;
        }
        try {
            Matcher matcher = CONTENT_DISPOSITION.matcher(contentDisposition);
            if (matcher.find()) {
                String fileName = matcher.group(1);
                if (fileName != null) {
                    int index = fileName.lastIndexOf('/') + 1;
                    if (index > 0) {
                        fileName = fileName.substring(index);
                    }
                }
                Log.d(HTTPUtil.class.getName(), "Extracted file name is " + fileName);
                return fileName;
            }
        } catch (Exception exc) {
            Log.d(HTTPUtil.class.getName(), "Exception parsing content disposition " + contentDisposition, exc);
        }
        return null;
    }

    public static String getMimeTypeFromContentType(String contentType) {
        Log.d(HTTPUtil.class.getName(), "getMimeTypeFromContentType, contentType is " + contentType);
        if (contentType == null) {
            return null;
        }
        try {
            int index = contentType.indexOf(';');
            if (index >= 0) {
                contentType = contentType.substring(0, index);
            }
            Matcher matcher = MIME_TYPE.matcher(contentType);
            if (matcher.matches()) {
                String mimeType = matcher.group(1);
                Log.d(HTTPUtil.class.getName(), "Extracted mime type is " + mimeType);
                return mimeType;
            }
        } catch (Exception exc) {
            Log.d(HTTPUtil.class.getName(), "Exception parsing content type " + contentType, exc);
        }
        return null;
    }
}
