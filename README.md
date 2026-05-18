# Scoopz Share Extension

> Share TikTok links directly into the Scoopz queue from any Android app

A minimal Android `Activity` that hooks into the system share sheet. When you share a TikTok link from the TikTok app (or any browser), it appends the URL to `/sdcard/Download/links_inbox.txt` — the same inbox that `collector.py` watches.

---

## How It Works

1. You tap **Share** on any TikTok video
2. The Android share sheet shows **Scoopz** as a target
3. `ShareActivity` receives the URL, validates it contains `tiktok.com`, and appends it to `links_inbox.txt`
4. `collector.py` picks it up on its next poll and downloads the video

---

## Permissions

This activity requires **All Files Access** (`MANAGE_EXTERNAL_STORAGE`), which allows writing to `/sdcard/Download/`.

On first launch, if the permission is not granted, the app automatically opens the system settings page for you to enable it. Once enabled, sharing works silently with a toast confirmation.

> **Why this permission?** Standard `WRITE_EXTERNAL_STORAGE` is restricted on Android 11+. All Files Access is required to write to shared storage paths like `/sdcard/Download`.

---

## Setup

### 1. Prerequisites

- Android Studio (any recent version)
- Android device or emulator running **Android 11+ (API 30+)**
- Scoopz (`collector.py`) already set up in Termux

### 2. Add to your Android project

Place `ShareActivity.kt` in:

```
app/src/main/java/com/scoopz/share/ShareActivity.kt
```

### 3. Register in AndroidManifest.xml

```xml
<uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE" />

<activity
    android:name=".ShareActivity"
    android:label="Scoopz"
    android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.SEND" />
        <category android:name="android.intent.category.DEFAULT" />
        <data android:mimeType="text/plain" />
    </intent-filter>
</activity>
```

### 4. Build and install

```bash
./gradlew installDebug
```

Or build the APK via **Build > Build APK** in Android Studio and sideload it to your device.

---

## Usage

1. Open TikTok and find a video you want to download
2. Tap **Share > More > Scoopz**
3. You'll see a ** Queued** toast if the link was saved, or an error message if something went wrong
4. `collector.py` (running in Termux) picks up the link and downloads the video automatically

---

## Behavior Reference

| Scenario | Result |
|----------|--------|
| All Files Access is OFF | Opens system settings, shows instructions toast, exits |
| Valid TikTok URL shared | Appends URL to `links_inbox.txt`, shows Queued |
| Non-TikTok URL shared | Silently exits (no write, no toast) |
| File write error | Shows Error toast with the exception message |

---

## File Written

```
/sdcard/Download/links_inbox.txt
```

Each URL is appended on its own line. The file is created automatically if it doesn't exist. This is the same file `collector.py` polls — no extra configuration needed.

---

## Troubleshooting

**Scoopz doesn't appear in the share sheet**
- Confirm the `<intent-filter>` in `AndroidManifest.xml` includes `text/plain` and `android.intent.action.SEND`
- Reinstall the APK and retry

**Settings page opens every time**
- Go to *Settings > Apps > Scoopz > Permissions > Files and Media* and enable **Allow management of all files**

**Queued shows but video never downloads**
- Make sure `collector.py` is running in Termux
- Confirm the URL was written: `cat /sdcard/Download/links_inbox.txt`

**Error toast on share**
- Check that `/sdcard/Download/` exists: `ls /sdcard/Download/`
- Verify All Files Access is enabled for Scoopz in system settings

---

## Compatibility

| Property | Value |
|----------|-------|
| Minimum SDK | API 30 (Android 11) |
| Target SDK | API 34 (Android 14) recommended |
| Language | Kotlin |
| Permission required | `MANAGE_EXTERNAL_STORAGE` |

---

## License

MIT
