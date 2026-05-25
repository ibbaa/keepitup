# Development

## Build

### Local

For the local build you need the Android SDK installed on your machine. It is
recommended to install Android Studio. Gradle takes the SDK location from a
file `local.properties` with the property `sdk.dir=mylocation` pointing to
the location.

Call `./gradlew assemble` to trigger the build.

The two output apk files are located under

`./keepitupmain/build/outputs/apk/debug` for the debug build variant and
`./keepitupmain/build/outputs/apk/release` for the release build variant.

The resulting release apk is named `keepitup-release-unsigned.apk` and is
unsigned.

The resulting debug apk is named `keepitup-debug.apk` and is signed with the
debug default key.

You can use the script `./signing.sh` for signing the release apk, but some
preparations are necessary (see [Signing](#signing) below).

### Docker

For the Docker build you don't need any Android tools installed locally.
Simply call `./docker_build.sh`. The output directories are the same as for
the local build. The Docker build signs the files automatically. You must
provide a keystore (see [Signing](#signing) below) or the build will fail.

The resulting release apk is named `keepitup-release.apk` and is signed with
the provided key.

The resulting debug apk is named `keepitup-debug.apk` and is signed with the
provided key.

The `docker_build.sh` script works on Linux. Docker must be installed. There
is no script for Windows at the moment, but it should not be difficult to
create one.

### Signing

You have to provide your own signing keys if you build *Keep it up* yourself.
The script `./signing.sh` can be used for signing. The Docker build calls this
script after the build. The script calls the tool `apksigner` that ships with
the Android SDK. The location of this tool varies between SDK versions, so you
have to provide it via the environment variable `BUILD_TOOLS_PATH`. Usually
`apksigner` is located under `android-sdk/build-tools/version`.

The Docker build uses the `apksigner` tool provided with the container, so
`BUILD_TOOLS_PATH` is not required for Docker builds.

You need a keystore with a key pair for signing. You can create one with the
following command:

```shell
keytool -genkey -v -keystore keepitup.jks -alias keepitupkey \
  -keyalg RSA -keysize 2048 -storepass keepitup \
  -keypass keepitup -validity 20000
```

`keytool` ships with the Java JDK, not with the Android SDK.

The keystore file is named `keepitup.jks` with a key pair `keepitupkey` and
the store and key password `keepitup`.

If you follow this naming convention and place `keepitup.jks` in a directory
named `signing` in the project root, the script `./signing.sh` will find the
signing information automatically. Alternatively you can provide the
information via environment variables:

| Variable | Description | Default |
|---|---|---|
| `KEEPITUP_KEYSTORE_FILE` | Path to the keystore file | `./signing/keepitup.jks` |
| `KEEPITUP_KEY_ALIAS` | Key alias | `keepitupkey` |
| `KEEPITUP_KEYSTORE_PASS` | Keystore password | `keepitup` |
| `KEEPITUP_KEY_PASS` | Key password | `keepitup` |

## Verification

To verify the signature of the apk files released on GitHub you can use the
`apksigner` tool that comes with the Android SDK:

```shell
apksigner verify --verbose --print-certs ./keepitup-debug.apk
apksigner verify --verbose --print-certs ./keepitup-release.apk
```

The output should include:

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

Note that the GitHub releases are signed with a different key than the
F-Droid version. Both are legitimate distributions of the same app.

## Security

The app stores credentials encrypted in the internal database. Currently the
following values are considered credentials and stored encrypted:

- HTTP headers of type `Authorization`, including Basic Authentication
- SNMP community strings
- HTTP Basic Authentication passwords

The encryption key is generated at runtime and stored in the device keystore.
The key is non-transferable and cannot be read by any other means. It is
removed when the app is uninstalled.

To transfer credentials to another device or restore them after reinstallation,
the configuration must be exported with encryption enabled and a password
chosen during export. No credentials are included in plaintext exports.

## Debug Logging

The debug variant provides a feature to write debug log files that can be
enabled in the system settings. The files are written to the app specific
storage under `Android/data/net.ibbaa.keepitup/files/syslog`. The amount of
log data is extensive. The files are rotated every 10 MB and archived as a zip
file in the same directory every 50 log files. Nothing is ever deleted
automatically, so this feature can fill up disk space.

It is also possible to write a complete dump of the internal database to app
specific storage whenever the database content changes. This can be enabled in
the system settings. Every table is written to a separate text file. Dump files
are archived every 50 files but are never deleted. This feature should be used
with care as the performance impact is significant.

The release variant does not provide these features but writes error messages
to the Android system log. The log output of the release variant is minimal.
