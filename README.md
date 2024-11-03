# Keep it up

<i>Keep it up</i> is a simple network monitoring app for Android.

It checks network services periodically by sending a ping, connecting to a specific port or downloading a site. It can also be used to ensure a connection with a timeout will be kept alive.

<b>Features</b>

- Create multiple separate network tasks
- One task monitors one network service at a specified interval
- A task sends a specified number of pings, performs a specified number of connections attempts or downloads a file or a site
- Successful and unsuccessful attempts are logged
- Downloaded files can automatically be deleted after a successful download or be kept
- Notifications can be sent on failure or when a network service goes down or up
- A network task can be restricted to WiFi connections, i.e. it does not perform any action on mobile networks with potentially limited download data
- The configuration can be exported and imported as a JSON data file
- Intervals can be defined during which all background work is suspended for battery saving

<b>Permissions</b>

- <i>RECEIVE_BOOT_COMPLETED</i> for restarting running networks tasks on device boot
- <i>ACCESS_NETWORK_STATE</i> for checking network accessibility
- <i>INTERNET</i> for accessing the internet
- <i>WAKE_LOCK</i> to keep the device awake while executing a task
- <i>FOREGROUND_SERVICE</i> to start the foreground service for running tasks
- <i>USE_EXACT_ALARM</i> to start networks tasks after expiry of intervals (Android 13+)
- <i>SCHEDULE_EXACT_ALARM</i> to start networks tasks after expiry of intervals (Android 11 and 12)
- <i>POST_NOTIFICATIONS</i> for sending notifications

## Installation

The app requires Android 5.0 (API level 21) and should run with all subsequent versions, however it's not tested with all versions and devices. 

The preferred way is the installation from F-Droid:

[<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png"
    alt="Get it on F-Droid"
    height="80">](https://f-droid.org/packages/net.ibbaa.keepitup)
    
From F-Droid the app will be updated automatically.

You can also download the files from here on Github. The releases are tagged. Please download one of the provided apk files. You have to allow the installation from unknown sources. With older Android versions (7.x and lower) you have to allow this globally in the security settings. With recent versions of Android this can be configured per app and you have to allow it for the file manager or browser that is used for installation.

Two apk files are provided:

`keepitup-debug.apk` and `keepitup-release.apk`

The debug variant contains debug information and provides some logging features in the system settings. The release variant is optimized and runs faster. It is recommended to use this version aside from development purposes.

Please keep in mind that the version here on Github is signed with a different key than the version on F-Droid. You will get an error if you try to install the Github version and the F-Droid version is already installed (and vice versa). You have to deinstall the app first to do that.

## Signature

The apk files released here on Github are self signed. To verfiy the signature you can use the `apksigner` tool that comes with Android SDK:

`apksigner verify --verbose --print-certs ./keepitup-debug.apk`

`apksigner verify --verbose --print-certs ./keepitup-release.apk`

The output should include

```
Number of signers: 1
Signer #1 certificate DN: CN=Alwin Ibba, O=ibbaa
Signer #1 certificate SHA-256 digest: 8d006bf85e3d2d823939a107378358944178ed39e5ab534dbfa028f7cce1e3af
Signer #1 certificate SHA-1 digest: d65a046585d434c785fda70216cc0d892893dd3d
Signer #1 certificate MD5 digest: 711a19fc541a1c6c898bc866cd8d09e1
Signer #1 key algorithm: RSA
Signer #1 key size (bits): 2048
Signer #1 public key SHA-256 digest: 2acba358e06962a9cdb106a1b788f131a5cb8ab06180a9078e62a1be66fa0d65
Signer #1 public key SHA-1 digest: d8eeb46370f1ff2f0548d075319e07e90763117e
Signer #1 public key MD5 digest: 0020b7336f5edb8b3a82d62d7c239842
```

## Power consumption

The app uses exact alarms to trigger network task execution. Power consumption during waiting is as high as for a calendar app waiting for reminders to trigger. During execution the power consumption is higher, of course, and short execution intervals cause more overall battery drain.

The app works best if you disable battery optimization. If battery optimization is active, network tasks execution may be unreliable especially for short intervals, i.e. they may trigger less often and the trigger time may not be exact. There is a link in the app leading to the Android battery settings for the app. Of course, with disabled battery optimization power consumption may be higher.

It is possible to define suspension intervals in the settings during which all background work is suspended. There is still one active alarm to wake up the app and resume task execution. Except for that alarm the app is idle during suspension. It is possible to define multiple suspension intervals but each interval must be at least 30 min. However, many short intervals do not make much sense for battery saving.

## Build

### Local

For the local build you need the Android SDK installed on your machine. It is recommended to install Android Studio. Gradle takes the SDK location from a file `local.properties` with the property `sdk.dir=mylocation` pointing to the location.

Call `./gradlew assemble` to trigger the build.

The two output apk files are located under

`./keepitupmain/build/outputs/apk/debug` for the debug build variant and `./keepitupmain/build/outputs/apk/release` for the release build variant.

The resulting release apk is named `keepitup-release-unsigned.apk` and is unsigned.

The resulting debug apk is named `keepitup-debug.apk` and is signed with the debug default key.

You can use the script `./signing.sh` for signing the files, but some preparations are necessary (see below).

### Docker

For the docker build you don't need any Android tools. Simply call `./docker_build.sh`. The output directories are the same as for the local build. The docker build signs the files. You must provide a keystore (see below) or the build will fail.

The resulting release apk is named `keepitup-release.apk` and is signed with the provided key.

The resulting debug apk is named `keepitup-debug.apk` and is signed with the provided key.

The `docker_build.sh` script works for Linux. Docker must be installed of course. There is no script for Windows at the moment but it should not be difficult to create one.

### Signing

You have to provide your own signing keys if you build *Keep it up* by yourself. The script `./signing.sh` can be used for signing. The docker build calls this script after the build. The script calls the tool `apksigner` that ships with the Android SDK. Unfortunately the location of this tool is not fixed and does vary from version to version. So you have to provide the location on your machine in the environment variable `BUILD_TOOLS_PATH`. Usually `apksigner` is located under `android-sdk/build-tools/version`.

The docker build does use the `apksigner` tool that is provided with the container, so for the docker build it's not necessary to set `BUILD_TOOLS_PATH`.

Furthermore you need a keystore with proper keys for signing. You can create a keystore with the following command:

`keytool -genkey -v -keystore keepitup.jks -alias keepitupkey -keyalg RSA -keysize 2048 -storepass keepitup -keypass keepitup -validity 20000`

`keytool` ships with the Java JDK, not with the Android SDK.

The keystore file is named `keepitup.jks` with a key pair `keepitupkey` and the store and key password `keepitup`.

If you follow this naming convention and put the `keepitup.jks` in a directory named `signing` (the directory must reside in the project root), then the script `./signing.sh` will find the signing information. Alternatively you can provide the information with the four environment variables

```
KEEPITUP_KEYSTORE_FILE for the path to the keystore file (default ./signing/keepitup.jks)
KEEPITUP_KEY_ALIAS for the key alias (default keepitupkey)
KEEPITUP_KEYSTORE_PASS for the keystore password (default keepitup)
KEEPITUP_KEY_PASS for the key password (default keepitup)
```
## Logging

### Debug

The debug variant provides a feature to write debug log files that can be enabled in the system settings. The files are written to the app specific storage under `Android/data/net.ibbaa.keepitup/files/syslog`. The amount of log data is extensive. The files are rotated every 10 MByte and archived as a zip file in the same directory every 50 log files. However, nothing will ever be deleted automatically, so this feature can fill up disk space.

It is possible to write a complete dump of the internal database to the app specific storage whenever the database content changes before and after the change. This feature can be enabled in the system settings. Every table is written to a separate text file. The dump files are archived every 50 files but will not be deleted. This feature should be used with care because the performance penalty is massive.

The release variant does not provide these features but writes error messages to Android system log. The log amount of the release variant is marginal.

### Network tasks

Successful and unsuccessful executions of every task are logged to the internal database with additional data and an execution message. Every 100 log entries the oldest one will be deleted. The log entries can manually be deleted and are automatically deleted when the corresponding network task is deleted. The export configuration feature writes a JSON file which also contains all log messages of all network tasks.

Additionally it is possible to write the network task logs to app specific storage as a file. The directory can be configured in the settings with a suitable default. The feature is disabled by default and can be enabled in the settings. Every task writes its own set of log files which are rotated every 1 MByte and will be archived every 20 files. The oldes zip archive will automatically be deleted every 10 archive files. However, on network task deletion, the corresponding logs will not be deleted.

## File access

<i>Keep it up</i> does access the device storage for reading and writing files:

- Debug log files and databse dumps (only debug variant)
- Network task logs (optional)
- Downloaded files or sites can be kept (optional)
- Import and export of configuration

Beginning with Android 11 persistent storage access has been restricted. Scoped storage was introduced in order to avoid cluttering of the external storage with arbitrary app files. Each app has its own scoped storage with full read and write access. <i>Keep it up</i> did respect this and only uses the `Android/data/net.ibbaa.keepitup/` folder as a storage. Following versions of Android more and more restricted the access to the app specific scoped storage for other apps and with Android 14 even usual file managers cannot access the files under `Android/data/net.ibbaa.keepitup/` (there are ways around this restriction, but all of them can be considered hacks or workarounds). It does not make much sense to put log files or downloaded content there if access is prohibited. Starting with version 1.6.0 <i>Keep it up</i> provides a feature to chose arbitrary storage locations by using the storage access framework, which is the intended way for doing this in newer versions of Android. This feature is available for devices running Android 10+ and is disabled by default but can be enabled in the system settings. This has some disadvantages. The storage access framework provides system screens to chose the file location which are cumbersome to use and permissions can be withdrawn by the system at any time, so writing log files may fail due to missing write privileges. Usually the permissions are not withdrawn while the app is in use but only if the app is not used for a while. <i>Keep it up</i> detects this and sends a notification to renew the permission (which requires user interaction) but in the meantime, nothing can be written. Depending on how this works, this feature may become the default in future versions and the old file dialogs may be removed. This feature is available for everything besides the debug logs, which are only available in the debug veriant and never have been configurable. In the development environment one can usually access the restricted storage locations.
