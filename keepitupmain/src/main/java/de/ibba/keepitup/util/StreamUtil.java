package de.ibba.keepitup.util;

import java.io.InputStream;
import java.nio.charset.Charset;

public class StreamUtil {

    public static String inputStreamToString(InputStream stream, Charset charset) throws Exception {
        byte[] buffer = new byte[1024];
        StringBuilder stringBuilder = new StringBuilder();
        int read;
        while ((read = stream.read(buffer, 0, 1024)) >= 0) {
            String data = new String(buffer, 0, read, charset);
            stringBuilder.append(data);
        }
        return stringBuilder.toString();
    }
}
