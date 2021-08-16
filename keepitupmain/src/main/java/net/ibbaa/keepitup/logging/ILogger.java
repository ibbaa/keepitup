package net.ibbaa.keepitup.logging;

public interface ILogger {

    void log(String tag, String message, Throwable throwable, LogLevel level);
}
