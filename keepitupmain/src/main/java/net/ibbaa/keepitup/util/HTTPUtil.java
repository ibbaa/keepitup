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
import net.ibbaa.keepitup.resources.PreferenceManager;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Request;
import okhttp3.Response;

public class HTTPUtil {

    private static final Pattern FILENAME_STAR = Pattern.compile("filename\\*\\s*=\\s*([^']*)''([^;\\s]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern FILENAME_QUOTED = Pattern.compile("filename\\s*=\\s*\"([^\"]+)\"", Pattern.CASE_INSENSITIVE);
    private static final Pattern FILENAME_UNQUOTED = Pattern.compile("filename\\s*=\\s*([^;\\s]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern MIME_TYPE = Pattern.compile("\\s*(\\w*/\\w*)\\s*", Pattern.CASE_INSENSITIVE);

    public static boolean isHTTPReturnCodeRedirect(int returnCode) {
        return returnCode == 301 || returnCode == 302 || returnCode == 307 || returnCode == 308;
    }

    public static boolean isHTTPReturnCodeOk(int returnCode) {
        return returnCode >= 200 && returnCode < 300;
    }

    public static String getContentDisposition(Context context, Response response) {
        return response.header(context.getResources().getString(R.string.http_header_content_disposition));
    }

    public static String getContentType(Context context, Response response) {
        return response.header(context.getResources().getString(R.string.http_header_content_type));
    }

    public static String getLocation(Context context, Response response) {
        return response.header(context.getResources().getString(R.string.http_header_content_location));
    }

    public static void setUserAgent(Context context, Request.Builder builder) {
        PreferenceManager preferenceManager = new PreferenceManager(context);
        builder.header(context.getResources().getString(R.string.http_header_user_agent), preferenceManager.getPreferenceHTTPUserAgent());
    }

    public static void setAcceptHeader(Context context, Request.Builder builder) {
        builder.header(context.getResources().getString(R.string.http_header_accept), context.getResources().getString(R.string.http_header_accept_value));
    }

    public static void setAcceptLanguageHeader(Context context, Locale locale, Request.Builder builder) {
        String headerName = context.getResources().getString(R.string.http_header_accept_language);
        if (locale == null) {
            builder.header(headerName, context.getResources().getString(R.string.http_header_accept_language_default_value));
            return;
        }
        String primaryTag = locale.toLanguageTag();
        String primaryLang = locale.getLanguage();
        StringBuilder langValue = new StringBuilder();
        langValue.append(primaryTag);
        if (!primaryTag.equalsIgnoreCase(primaryLang)) {
            langValue.append(",").append(primaryLang).append(";q=0.9");
        }
        if (!"en".equalsIgnoreCase(primaryLang)) {
            langValue.append(",en;q=0.8");
        }
        builder.header(headerName, langValue.toString());
    }

    public static String getFileNameFromContentDisposition(String contentDisposition) {
        Log.d(HTTPUtil.class.getName(), "getFileNameFromContentDisposition, contentDisposition is " + contentDisposition);
        if (contentDisposition == null) {
            return null;
        }
        try {
            Matcher matcher = FILENAME_STAR.matcher(contentDisposition);
            if (matcher.find()) {
                String encoding = matcher.group(1);
                String encodedFilename = matcher.group(2);
                try {
                    String decoded = URLDecoder.decode(encodedFilename, encoding);
                    return stripPath(decoded);
                } catch (UnsupportedEncodingException exc) {
                    return stripPath(encodedFilename);
                }
            }
            matcher = FILENAME_QUOTED.matcher(contentDisposition);
            if (matcher.find()) {
                return stripPath(matcher.group(1));
            }
            matcher = FILENAME_UNQUOTED.matcher(contentDisposition);
            if (matcher.find()) {
                return stripPath(matcher.group(1));
            }
        } catch (Exception exc) {
            Log.d(HTTPUtil.class.getName(), "Exception parsing content disposition " + contentDisposition, exc);
        }
        return null;
    }

    private static String stripPath(String fileName) {
        if (fileName == null) {
            return null;
        }
        int lastSlash = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));
        return lastSlash >= 0 ? fileName.substring(lastSlash + 1) : fileName;
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
