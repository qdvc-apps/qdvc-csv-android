# QDVC CSV

A simple, fast CSV viewer for Android. It renders a CSV file as a table grid, keeps
the header row pinned while you scroll, and registers itself as an **Open with**
target for `.csv` files in your file browser.

- **Application ID / namespace:** `qdvc.csv.android.app`
- **Min SDK:** 24 (Android 7.0) · **Target/Compile SDK:** 34
- **UI:** Jetbrains Compose + Material 3

## Features

- Table grid rendering with the **first row treated as sticky column headers**.
  The header stays pinned as you pan up and down; header and body scroll
  horizontally in sync so columns stay aligned.
- Registers as an **Open with** handler for CSV files (by MIME type and by
  `.csv` extension), plus an in-app file picker.
- **Settings page** to switch between Light, Dark, or System theme. Default is
  **System**, so the app follows your OS light/dark mode. The choice is
  persisted with DataStore.
- Robust CSV parsing (RFC 4180 essentials): quoted fields, embedded commas and
  newlines, escaped quotes (`""`), CRLF/LF line endings, UTF-8 BOM stripping,
  and delimiter auto-detection (`,` `;` tab `|`).

## Build

```bash
./gradlew assembleDebug
```

The APK is written to `app/build/outputs/apk/debug/app-debug.apk`.

To install on a connected device/emulator:

```bash
./gradlew installDebug
```

Or open the project folder in Android Studio and press Run.

## Project layout

```
app/src/main/
├── AndroidManifest.xml            # Launcher + CSV "Open with" intent filters
├── java/qdvc/csv/android/app/
│   ├── MainActivity.kt            # Intent handling, theme, viewer/settings nav
│   ├── CsvViewModel.kt            # Loads & parses CSV off the main thread
│   ├── CsvParser.kt               # RFC 4180-style parser + delimiter detection
│   ├── CsvTable.kt                # Sticky-header scrollable grid
│   ├── ViewerScreen.kt            # Top bar, empty/loading/error/loaded states
│   ├── SettingsScreen.kt          # Light / Dark / System theme selector
│   ├── ThemePreference.kt         # DataStore-backed theme persistence
│   └── Theme.kt                   # Material 3 color schemes
└── res/                           # Strings, themes, adaptive launcher icon
```
