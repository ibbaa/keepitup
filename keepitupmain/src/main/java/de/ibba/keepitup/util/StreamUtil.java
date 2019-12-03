package de.ibba.keepitup.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class StreamUtil {

    private static final int BUFFER_SIZE_1024 = 1024;
    private static final int BUFFER_SIZE_4096 = 4096;

    public static String inputStreamToString(InputStream stream, Charset charset) throws Exception {
        byte[] buffer = new byte[BUFFER_SIZE_1024];
        StringBuilder stringBuilder = new StringBuilder();
        int read;
        while ((read = stream.read(buffer, 0, BUFFER_SIZE_1024)) >= 0) {
            String data = new String(buffer, 0, read, charset);
            stringBuilder.append(data);
        }
        return stringBuilder.toString();
    }

    public static boolean inputStreamToOutputStream(InputStream inputStream, OutputStream outputStream, Interrupt interrupt) throws Exception {
        int read;
        if (interrupt == null) {
            interrupt = () -> true;
        }
        byte[] buffer = new byte[BUFFER_SIZE_4096];
        while ((read = inputStream.read(buffer, 0, BUFFER_SIZE_4096)) >= 0 && interrupt.shouldContinue()) {
            outputStream.write(buffer, 0, read);
        }
        return read < 0;
    }

    @FunctionalInterface
    public interface Interrupt {
        boolean shouldContinue();
    }
}
