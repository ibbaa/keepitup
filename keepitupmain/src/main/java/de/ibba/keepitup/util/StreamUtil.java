package de.ibba.keepitup.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import de.ibba.keepitup.logging.Log;

public class StreamUtil {

    private static final int BUFFER_SIZE_1024 = 1024;
    private static final int BUFFER_SIZE_4096 = 4096;
    private static final int PART_SIZE = 1024 * 1024 * 10;

    public static String inputStreamToString(InputStream stream, Charset charset) throws Exception {
        Log.d(StreamUtil.class.getName(), "inputStreamToString");
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
        Log.d(StreamUtil.class.getName(), "inputStreamToOutputStream");
        int read = 0;
        if (interrupt == null) {
            interrupt = () -> true;
        }
        byte[] buffer = new byte[BUFFER_SIZE_4096];
        int partsProgress = 0;
        int bytesDownloaded = 0;
        Log.d(StreamUtil.class.getName(), "Startíng download.");
        while (interrupt.shouldContinue() && (read = inputStream.read(buffer, 0, BUFFER_SIZE_4096)) >= 0) {
            outputStream.write(buffer, 0, read);
            bytesDownloaded += read;
            if (bytesDownloaded / PART_SIZE > partsProgress) {
                partsProgress++;
                Log.d(StreamUtil.class.getName(), "Download progress: " + bytesDownloaded + " bytes.");
            }
        }
        Log.d(StreamUtil.class.getName(), "Bytes downloaded: " + bytesDownloaded);
        Log.d(StreamUtil.class.getName(), "Finished download.");
        boolean success = read < 0;
        if (success) {
            Log.d(StreamUtil.class.getName(), "Download was successful.");
        } else if (!interrupt.shouldContinue()) {
            Log.d(StreamUtil.class.getName(), "Download was interrupted.");
        } else {
            Log.d(StreamUtil.class.getName(), "Download was not successful for an unknown reason.");
        }
        return success;
    }

    @FunctionalInterface
    public interface Interrupt {
        boolean shouldContinue();
    }
}
