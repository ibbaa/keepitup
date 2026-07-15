# User Manual

## Network Tasks

The main screen shows a list of all configured network tasks. Each task
monitors one network service at a configurable interval.

### Adding and editing tasks

Tap the **+** button to add a new task. Tap the edit icon to edit it. The same
dialog is used for both. Copying a task opens the same dialog pre-filled
with the existing values and creates a new task on confirmation.

The following fields are always visible:

- **Type** — the monitoring method: Ping, Connect, Download or SNMP
- **Interval (min)** — how often the task runs, in minutes
- **Only on WiFi** — if enabled, the task does not run on mobile networks
- **Notifications** — if enabled, a notification is sent on failure or
  status change (see [Settings](#notifications))
- **High priority** — visible when Notifications is enabled; sends a
  high-priority notification and optionally plays an alarm sound
  (see [System](#alarm-on-high-priority-failures))

The task name shown in the list can be changed by tapping the title after adding the task. The default name is "Network task X".

### Task actions

Swipe a task right to delete it. The following actions are also
available via the task icons on the right:

- **Delete** — deletes the task and all its log entries
- **Edit** — opens the edit dialog
- **Copy** — opens the edit dialog pre-filled with the existing values;
  confirms as a new task
- **Log** — opens the log for this task (last 100 entries)

In the log view, the three-dot menu provides **Delete logs** to clear all
log entries for this task, and **Hide successful** to show only failed
executions.

---

## Access Types

### Ping

Sends ICMP echo requests to the target host.

- **Host** — hostname or IP address
- **Ping count** — number of pings per execution
- **Package size** — ICMP packet size in bytes
- **Stop on success** — stops as soon as one ping succeeds

An execution is considered successful if at least one ping succeeds. The
log entry includes a statistics summary similar to the standard ping tool.

### Connect

Opens a TCP connection to the target host and port and immediately closes it.

- **Host** — hostname or IP address
- **Port** — TCP port number
- **Connect count** — number of connection attempts per execution
- **Stop on success** — stops as soon as one connection succeeds

An execution is considered successful if at least one connection succeeds.
The log entry includes the number of attempts, failures and timing.

### Download

Downloads a file or page via HTTP or HTTPS.

- **URL** — the target URL
- **Resolve rules** — optional rules to redirect connections to a different
  host or port (see [Resolve Rules](#resolve-rules))
- **Use default headers** — if enabled (default), sends the HTTP headers
  configured under [Defaults](#defaults); if disabled, a per-task header
  list appears instead (see [HTTP Headers](#http-headers))
- **Certificate settings** — opens a dialog with TLS and certificate options:
  - **Allow legacy TLS** — permits older TLS versions and cipher suites for
    compatibility with legacy servers; use with caution. Only takes effect if
    the Android version on the device still supports the older TLS version or
    cipher suite at all; recent Android versions have removed TLS 1.0/1.1 and
    several older cipher suites entirely, in which case this setting has no
    effect
  - **Ignore certificate errors** — disables certificate validation; use with
    caution (formerly **Ignore SSL errors**)
  - **Failure on expiry** — fails the task if the server certificate is due
    to expire within the number of days set in **Expiry days**; disabled
    while **Ignore certificate errors** is on

An execution is successful if the download completes without error and, if
**Failure on expiry** is enabled, no certificate encountered during the
download is due to expire within the configured number of days. Redirects
are followed by default and logged in detail. This can be disabled in
[Settings](#download-settings).

#### Resolve Rules

Resolve rules allow redirecting a download connection to a different host or
port than the one in the URL. This is useful when a service is accessible
under a different address than its public hostname, for example when testing
with a local mirror or when DNS-based routing is not desired.

Each rule is configured with four fields:

- **Match host** — the hostname to match (from the URL or a previous redirect)
- **Match port** — the port to match
- **Connect-to host** — the host to connect to instead
- **Connect-to port** — the port to connect to instead

Any field left empty takes its value from the URL. The combination of match
host and match port must be unique across all rules for a task. Matching is
exact — wildcards are not supported. A rule matches when both the host and
port of the connection attempt exactly match the configured values, including
after redirects.

Multiple rules can be configured per task. Under [Defaults](#defaults) a
single set of default values can be defined that pre-fills the rule dialog
whenever a new rule is added.

#### HTTP Headers

Custom HTTP headers can be configured per task or globally under
[Defaults](#defaults). Each header is defined by a name and a value.

For Basic Authentication, select **Basic Auth** instead of entering the
header manually. Enter a username and password and the `Authorization`
header is built automatically. Basic Auth headers are stored encrypted
(see [Security](DEVELOPMENT.md#security)).

The **Use default headers** switch on the Download task controls whether
the globally configured default headers are sent. When disabled, only the
per-task headers are used. When enabled (default), only the default headers
are used and the per-task header list is hidden.

### SNMP

Queries a network device via SNMP at each poll interval.

- **Host** — hostname or IP address of the SNMP agent
- **Port** — the SNMP agent port, default is 161
- **Version** — SNMPv1, SNMPv2c or SNMPv3
- **Transport** — UDP (default) or TCP
- **Community** — the SNMP community string, stored encrypted; shown for
  SNMPv1 and SNMPv2c
- **Authentication** — opens the SNMPv3 authentication dialog (see below);
  shown instead of Community for SNMPv3
- **Interfaces** — opens the interface configuration (see below)

At each execution the system group is queried, providing device information
such as description, name and uptime. A device reboot is detected
automatically when the uptime resets. The execution is considered successful
if the device responds and returns a valid uptime value.

#### SNMPv3 Authentication

- **Auth username**
- **Auth passphrase** — stored encrypted
- **Auth algorithm** — None, MD5, SHA-1, SHA-224, SHA-256, SHA-384 or SHA-512
- **Privacy passphrase** — stored encrypted
- **Privacy algorithm** — None, DES, AES-128, AES-192, AES-256 or AES-256C

Privacy passphrase and Privacy algorithm are disabled while Auth algorithm
is set to None, since SNMPv3 does not support privacy without authentication.

#### Interface Monitoring

SNMP can monitor the status of individual network interfaces. Tap
**Interfaces** to open the interface configuration.

Tap **Scan** to discover the interfaces available on the device. The scan
queries the device for its interface list. By default, loopback, tunnel and
virtual interfaces are hidden. Enable **Show all interfaces** to display
the full list.

Each interface is shown with its system name and the operational status — for example `Gi1/0/1 : copper Up` or `Gi1/0/2 : copper Down`.
Use the checkbox next to each interface to enable monitoring for that
interface.

When a monitored interface goes down or can no longer be found on the
device, the execution is marked as failed and a log entry is written. The
alert persists at each subsequent execution until the interface comes back
up or monitoring is disabled.

Interface assignments are stored by interface name. If an interface
disappears after a device reboot or reconfiguration and reappears under the
same name, monitoring resumes automatically. If the name changes, the old
entry remains and will continue to generate alerts until it is removed
manually via a new scan.

---

## Logging

### In-app log

Each task execution is logged to the internal database with its result,
duration and a message. The log is limited to the last 100 entries per
task. Log entries are deleted when the task is deleted. The log can be
viewed by tapping the log icon on a task.

### Log to file

Log entries can additionally be written to a file. This is disabled by
default and can be enabled under [Settings](#other-settings). Each task
writes its own set of log files, rotated at 1 MB and archived every 20
files. The oldest archive is deleted when 10 archives accumulate. Log
files are not deleted when a task is deleted.

---

## Defaults

The Defaults screen, accessible from the main menu, provides default values
that pre-fill the task dialog when a new task is created. All task fields
are available here, covering all access types.

In addition, Defaults provides:

- **HTTP headers** — default headers sent with every download task that has
  **Use default headers** enabled
- **Resolve rule defaults** — default values that pre-fill the resolve rule
  dialog when a new rule is added to any download task
- **SNMP settings** — default values for version, port, transport, and auth
  and privacy algorithm that pre-fill new SNMP tasks; per-task fields such as
  Auth username and the passphrases are not covered by defaults

---

## Settings

### Notifications

- **Notification when network is not active** — by default, executions that
  fail due to no network connectivity are logged as failures but do not
  trigger notifications or alarms. Enable this to treat them identically to
  other failures.
- **Notification on failure or change** — send a notification on every
  failure (default), or only when the status changes (from success to
  failure or vice versa)
- **Notifications after failure** — only relevant when notifying on failure;
  sends the notification only after this many consecutive failures (default
  is 1, i.e. every failure)

### Suspension Intervals

When enabled (default), background task execution is suspended during
configured time intervals. This can be used to reduce battery consumption
during known idle periods, or to suppress alerts during planned maintenance
windows when devices are intentionally shut down.

Tap the interval block to configure one or more daily intervals by start
and end time, for example 06:00–08:00. Each interval must be at least 30
minutes. The feature can be disabled entirely with the **Suspension
intervals enabled** switch, which leaves any configured intervals intact.

### Download Settings

- **Download follows redirects** — follow HTTP redirects (default); if
  disabled, a redirect response is treated as a failure
- **Download to an external storage folder** — by default, downloaded files
  are stored in internal app storage and deleted after a successful
  download; enable this to choose a folder for downloaded files
- **Keep downloaded files** — visible when external storage is enabled;
  keeps downloaded files instead of deleting them; filename conflicts are
  resolved by an automatic suffix

### Other Settings

- **Enforce default ping package size** — if enabled, the globally
  configured ping package size is used for all tasks and cannot be
  overridden per task
- **Log to file** — enables writing log entries to files in addition to the
    database; a folder can be selected after enabling

---

## System

### Reset, Export and Import

- **Reset configuration** — deletes all tasks, logs and settings after
  confirmation
- **Export configuration** — exports all tasks, settings and logs as a JSON
  file; optionally encrypt the export with a password to include
  credentials; without encryption, credentials are excluded from the export
- **Import configuration** — imports a previously exported JSON file; if
  the file is encrypted, the password is requested during import

Folder and file selection uses SAF or the built-in file dialog depending on
the **Allow arbitrary file location** setting.

### External Storage Type

Visible only when one or more SD cards are available and SAF is disabled.
Allows selecting **Primary** storage or an SD card as the root for all
file paths.

### Battery Optimization

Visible on Android below version 15. Shows whether battery optimization is
active or inactive for the app, and links to the Android settings to change
it. It is recommended to disable battery optimization for reliable task
execution, especially at short intervals.

On Android 15 and later, the equivalent setting is background activity,
which is enabled by default and should be left enabled.

### Notifications

Shows whether the app is allowed to send notifications. If disallowed, tap
to open the Android notification settings for the app.

### Theme

Choose between **System** (follows Android dark/light mode), **Light** and
**Dark**.

### Allow Arbitrary File Location

Enables the Android Storage Access Framework (SAF) for folder and file
selection. SAF is the recommended approach on Android 10 and later and is
required for reliable access to external storage on Android 14+. It is
recommended to enable this setting.

Note that SAF permissions may occasionally be withdrawn by the system if
the app is unused for an extended period. The app will send a notification
when this happens, prompting you to renew the permission.

### Alarm on High Priority Failures

When enabled, a 60-second alarm sound plays when a task marked as **High
priority** fails. The alarm can be dismissed from the notification or
directly in the app. The system default alarm tone is used.

### Debug Settings

Only available in the debug build variant. Provides options for external
file logging and database dumps. See
[Debug Logging](DEVELOPMENT.md#debug-logging) for details.
