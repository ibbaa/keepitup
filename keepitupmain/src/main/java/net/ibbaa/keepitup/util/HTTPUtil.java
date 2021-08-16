package net.ibbaa.keepitup.util;

import android.content.Context;

import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;

public class HTTPUtil {

    private static final Pattern CONTENT_DISPOSITION = Pattern.compile("attachment;\\s*filename\\s*=\\s*\"([^\"]*)\"", Pattern.CASE_INSENSITIVE);
    private static final Pattern MIME_TYPE = Pattern.compile("\\s*(\\w*/\\w*)\\s*", Pattern.CASE_INSENSITIVE);

    public static boolean isHTTPConnection(URLConnection connection) {
        return connection instanceof HttpURLConnection;
    }

    public static boolean isHTTPReturnCodeOk(int returnCode) {
        return returnCode == HttpURLConnection.HTTP_OK;
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