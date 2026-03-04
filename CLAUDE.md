# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run unit tests
./gradlew test

# Run Android instrumented tests
./gradlew connectedAndroidTest

# Clean build
./gradlew clean
```

## Architecture Overview

D2RTerror is an Android app that notifies users when their selected Diablo 2 Resurrected terror zones are about to become active. Terror zones change every 30 minutes (at :00 and :30).

### Data Flow

1. **TerrorZoneScraper** scrapes d2emu.com/tz, decrypts XOR-encrypted zone data (double XOR with keys `ka02jnb1` and `kb32jnb1`)
2. **TerrorZoneRepository** provides zone data to ViewModels
3. **TerrorZoneWorker** (WorkManager) runs periodically, checks if upcoming zones match user selections, and triggers notifications
4. **WorkerScheduler** calculates exact check times based on user's advance notification preference (e.g., 5 min before zone change = check at :25 and :55)

### Key Components

- **ZoneData** (`data/local/ZoneData.kt`): Static list of all 36 terror zone groups with keyword matching for scraped names
- **PreferencesManager** (`data/local/PreferencesManager.kt`): DataStore-based persistence for user settings (selected zones, notification preferences, quiet hours)
- **NotificationHelper** (`notification/NotificationHelper.kt`): Creates and shows zone alert notifications

### Dependency Injection

Uses Koin. All dependencies defined in `di/AppModule.kt`.

### Navigation

Three screens via Jetpack Navigation Compose:
- **Home**: Shows current/next terror zones with countdown timer
- **ZoneSelection**: Pick which zones to get notified about
- **Settings**: Notification timing, quiet hours, battery optimization
