# RAMScope

**A lightweight Android RAM and memory monitoring tool.**

RAMScope is an open-source diagnostic app for Android. It shows real, unmodified memory statistics exposed by the platform — total/used/available RAM, cache, ZRAM and swap when accessible, and basic process information — in a clear, technical layout.

RAMScope is **not** a "RAM booster" or task killer. It never kills processes, never claims to "free up" or "optimize" memory, and never runs unnecessary background work. It is a read-only diagnostic tool.

## Features

- **Dashboard** — total, used and available RAM, cached memory, and used-RAM percentage shown as a circular gauge.
- **ZRAM / Swap** — ZRAM size and usage, swap total/used/available, read from platform-exposed sources. Any metric a device doesn't expose is shown as `Unavailable` instead of a guessed value.
- **Memory details** — a more technical breakdown: low-memory threshold, low-memory state, kernel `MemAvailable`, and everything from the dashboard in one place.
- **Process information** — shows what Android actually lets a third-party app see about running processes (see [Limitations](#limitations-of-android-apis) below).
- **Manual refresh** via a `Refresh` button, plus an optional configurable auto-refresh toggle. No continuous background polling.
- **Material 3** UI with full dark mode and light mode support, dynamic color on Android 12+, and a layout that adapts down to small screens.
- **No ads, no network access, no telemetry.**

## Screenshots

*(placeholders — see [`/screenshots`](./screenshots))*

| Dashboard | Details | Processes |
|---|---|---|
| `screenshots/dashboard_light.png` | `screenshots/details.png` | `screenshots/processes.png` |

## Requirements

- Android Studio (Koala or newer recommended)
- JDK 17
- Android SDK Platform 35
- A device or emulator running Android 8.0 (API 26) or later

## Building

```bash
git clone https://github.com/<your-username>/ramscope.git
cd ramscope
./gradlew assembleDebug
```

The debug APK will be produced at `app/build/outputs/apk/debug/app-debug.apk`.

> **Note:** this repository ships the Gradle wrapper scripts and configuration (`gradlew`, `gradlew.bat`, `gradle/wrapper/gradle-wrapper.properties`) but not the binary `gradle-wrapper.jar`, since binary files aren't practical to hand-author. Opening the project in Android Studio will regenerate it automatically on first sync, or you can generate it yourself with a local Gradle install:
>
> ```bash
> gradle wrapper --gradle-version 8.10.2
> ```

Alternatively, just open the project folder in Android Studio and let it sync — no extra steps needed there.

## Architecture

RAMScope keeps a small, conventional separation of concerns instead of putting logic in `MainActivity`:

```
app/src/main/java/com/stefandx/ramscope/
├── data/         → MemoryInfoProvider: the only class that talks to Android
│                   memory APIs and /proc. Everything else consumes it.
├── domain/       → GetMemorySnapshotUseCase: a thin use-case wrapper.
├── model/        → RamStats, ZramStats, MemoryDetails, ProcessInfo.
├── ui/
│   ├── theme/        → Material 3 color scheme, typography, dark/light.
│   ├── components/   → Reusable composables (MetricRow, SectionCard, RamUsageGauge).
│   ├── dashboard/    → Dashboard screen + its ViewModel.
│   ├── details/      → Memory details screen.
│   ├── processes/    → Process information screen + its ViewModel.
│   └── navigation/   → Bottom navigation + NavHost wiring.
└── utils/        → Formatting helpers and Android-version checks.
```

`MemoryInfoProvider` is the single source of truth for every metric in the app. It never uses root or restricted/unsafe methods — if a value can't be read safely on a given device or API level, it returns `null`, and the UI renders that as `Unavailable`.

## Limitations of Android APIs

RAMScope is built to be honest about what Android actually allows a normal, non-root app to see:

- **Per-app process memory**: since Android 8.0 (API 26), apps can no longer enumerate other apps' running processes or their memory usage — this was locked down for privacy reasons. `ActivityManager.getRunningAppProcesses()` now effectively only returns the calling app's own process. The Process information screen reflects this honestly instead of pretending otherwise.
- **ZRAM details**: `/sys/block/zram0/*` is often restricted by SELinux policies on production builds/OEM skins. When it can't be read, RAMScope shows `Unavailable` rather than a fabricated number.
- **`/proc/meminfo`**: generally world-readable on stock Android and used for cache/swap figures, but some heavily customized OEM builds may restrict or alter it.
- **No root, no shell-outs, no reflection tricks** are used to work around any of the above. RAMScope only ever calls public, documented Android APIs and reads the standard, unprivileged `/proc` and `/sys` nodes that many system-info tools rely on.

## Privacy

RAMScope collects no personal data and has no network access — it requests no INTERNET permission at all. Every metric is read and displayed entirely on-device.

## Contributing

Issues and pull requests are welcome. Please keep contributions aligned with the project's goals: real data, no fake "boost" features, minimal dependencies, and a clean architecture.

## License

RAMScope is licensed under the [MIT License](./LICENSE).
