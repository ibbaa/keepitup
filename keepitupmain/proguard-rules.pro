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
-keep public class net.ibbaa.keepitup.ui.validation.* {
    public <methods>;
}
-keep public class net.ibbaa.keepitup.resources.SystemServiceFactory
-keep public class net.ibbaa.keepitup.resources.SystemWorkerFactory
-keep public class net.ibbaa.keepitup.resources.JSONSystemSetup
-keep public class net.ibbaa.keepitup.service.PingNetworkTaskWorker {
    public <methods>;
}
-keep public class net.ibbaa.keepitup.service.ConnectNetworkTaskWorker {
    public <methods>;
}
-keep public class net.ibbaa.keepitup.service.DownloadNetworkTaskWorker {
    public <methods>;
}
-dontwarn com.google.**
-dontwarn java.lang.ClassValue
-assumenosideeffects class net.ibbaa.keepitup.logging.Log {
     public static *** d(...);
 }
-assumenosideeffects class net.ibbaa.keepitup.logging.Dump {
    public static *** dump(...);
}