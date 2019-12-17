# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-dontobfuscate
-keep public class de.ibba.keepitup.ui.validation.* {
    public <methods>;
}
-keep public class de.ibba.keepitup.resources.SystemServiceFactory
-keep public class de.ibba.keepitup.resources.SystemWorkerFactory
-keep public class de.ibba.keepitup.service.PingNetworkTaskWorker {
    public <methods>;
}
-keep public class de.ibba.keepitup.service.ConnectNetworkTaskWorker {
    public <methods>;
}
-keep public class de.ibba.keepitup.service.DownloadNetworkTaskWorker {
    public <methods>;
}
-dontwarn com.google.**
-dontwarn java.lang.ClassValue
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
}