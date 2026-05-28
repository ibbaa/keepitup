# Keep it up

<i>Keep it up</i> is a network monitoring app for Android.

It periodically checks the availability and reachability of network services via Ping, TCP Connect, HTTP/HTTPS Download, and SNMP. Each check is logged with its result and duration, and notifications can be sent on failure or on status changes.

<b>Features</b>

- Create multiple independent network tasks, each monitoring one network service at a configurable interval
- Supported types: Ping, TCP Connect, HTTP/HTTPS Download, SNMP (v1/v2c)
- Each task can perform multiple attempts per execution. All results are logged including duration
- SNMP queries the system group at each interval and supports per-interface monitoring with alerting on status changes
- Downloaded files can automatically be deleted after a successful download or be kept
- Notifications on failure, or when a service goes down or comes back up
- Tasks can be restricted to WiFi connections to avoid unnecessary mobile data usage
- Configurable suspension intervals for battery saving
- Configuration can be exported and imported as JSON

<b>Permissions</b>

- <i>RECEIVE_BOOT_COMPLETED</i> for restarting running networks tasks on device boot
- <i>ACCESS_NETWORK_STATE</i> for checking network accessibility
- <i>INTERNET</i> for accessing the internet
- <i>WAKE_LOCK</i> to keep the device awake while executing a task
- <i>FOREGROUND_SERVICE</i> to start the foreground service for running tasks
- <i>FOREGROUND_SERVICE_DATA_SYNC</i> to use the foreground for task scheduling
- <i>FOREGROUND_SERVICE_SPECIAL_USE</i> to use the foreground for task scheduling (Android 15+)
- <i>USE_EXACT_ALARM</i> to start networks tasks after expiry of intervals (Android 13+)
- <i>SCHEDULE_EXACT_ALARM</i> to start networks tasks after expiry of intervals (Android 11 and 12)
- <i>POST_NOTIFICATIONS</i> for sending notifications

## Installation

Starting with version 1.11.0, the app requires Android 6.0 (API level 23).
Version 1.10.0 and below support Android 5.0 (API level 21).

The preferred way is installation from F-Droid:

[<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png"
alt="Get it on F-Droid"
height="80">](https://f-droid.org/packages/net.ibbaa.keepitup)

From F-Droid the app will be updated automatically.

The app can also be downloaded from the [releases page](https://github.com/ibbaa/keepitup/releases) on
GitHub. Please note that the GitHub version is signed with a different key
than the F-Droid version. You cannot install one version while the other is
already installed — you have to uninstall the existing version first.

## Documentation

- [User Manual](MANUAL.md) — detailed description of all features and settings
- [Development](DEVELOPMENT.md) — build instructions, signing and security notes

## License

[Apache License 2.0](LICENSE)
