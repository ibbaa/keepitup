# Keep it up

Keep it up is an app for Android that cecks network services periodically by sending a ping, connecting to a specific port or downloading a site. It can also be used to ensure a connection with a timeout will be kept alive.

You can create multiple separate network tasks. One task monitors one network service at a specified interval. The network tasks can be enabled and disabled separately.

The network task sends a specified number of pings, performs a specified number of connections attempts or downloads a file or a site. The app keeps a log of successful and unsuccessful attempts and can send notifications on failure.

The downloaded files can be automatically deleted after a successful download or be kept.

A network task can be restricted to WiFi connections, i.e. it does not perform any action on mobile networks with potentially limited download data.

The app works best if you disable  battery optimization for the app. If battery optimization is active, networks tasks execution may be unreliable especially with short intervals, i.e. they may trigger less often and the trigger time may not be exact. There is a link in the app leading to the Android battery setting for the app. Of course, with disabled battery optimization power consumption may be higher.

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

## Build

### Local

For the local build you need the Android SDK installed on your machine. It is recommended to install Android Studio. Gradle takes the SDK location from a file `local.properties` with the property `sdk.dir=mylocation` pointing to the location.

Call `./gradlew assemble` to trigger the build.

The two output apk files are located under

`./keepitupmain/build/outputs/apk/debug` for the debug build variant and `./keepitupmain/build/outputs/apk/release` for the release build variant.

### Debug

For the docker build you do need any Android tools. Simply call

`./docker_build.sh`

The output directories are the same as for the local build. 

The script works for Linux. Docker must be installed of course. There is not script for Windows at the moment but it should not be difficult to create one.


