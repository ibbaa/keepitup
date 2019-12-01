package de.ibba.keepitup.util;

import android.content.Context;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.ibba.keepitup.R;
import de.ibba.keepitup.service.FileManager;

public class HTTPUtil {

    private static final Pattern CONTENT_DISPOSITION = Pattern.compile("attachment;\\s*filename\\s*=\\s*\"([^\"]*)\"", Pattern.CASE_INSENSITIVE);
    private static final Pattern MIME_TYPE = Pattern.compile("\\s*(\\w*/\\w*)\\s*", Pattern.CASE_INSENSITIVE);

    public static boolean isHTTPConnection(URLConnection connection) {
        return connection instanceof HttpURLConnection;
    }

    public static String getContentDisposition(Context context, URLConnection connection) {
        return connection.getHeaderField(context.getResources().getString(R.string.http_header_content_disposition));
    }

    public static String getFileNameFromContentDisposition(String contentDisposition) {
        Log.d(FileManager.class.getName(), "getFileNameFromContentDisposition, contentDisposition is " + contentDisposition);
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
                Log.d(FileManager.class.getName(), "Extracted file name is " + fileName);
                return fileName;
            }
        } catch (Exception exc) {
            Log.d(FileManager.class.getName(), "Exception parsing content disposition " + contentDisposition, exc);
        }
        return null;
    }

    public static String getMimeTypeFromContentType(String contentType) {
        Log.d(FileManager.class.getName(), "getMimeTypeFromContentType, contentType is " + contentType);
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
                Log.d(FileManager.class.getName(), "Extracted mime type is " + mimeType);
                return mimeType;
            }
        } catch (Exception exc) {
            Log.d(FileManager.class.getName(), "Exception parsing content type " + contentType, exc);
        }
        return null;
    }
}
