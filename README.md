# Vocabri

[![DeepSource](https://app.deepsource.com/gh/deledzis/Vocabri.svg/?label=code+coverage&show_trend=true&token=Q7RU9Z2I1GsSG30Ivnl0ZdF0)](https://app.deepsource.com/gh/deledzis/Vocabri/)
[![DeepSource](https://app.deepsource.com/gh/deledzis/Vocabri.svg/?label=active+issues&show_trend=true&token=Q7RU9Z2I1GsSG30Ivnl0ZdF0)](https://app.deepsource.com/gh/deledzis/Vocabri/)
[![DeepSource](https://app.deepsource.com/gh/deledzis/Vocabri.svg/?label=resolved+issues&show_trend=true&token=Q7RU9Z2I1GsSG30Ivnl0ZdF0)](https://app.deepsource.com/gh/deledzis/Vocabri/)

[![Auto Merge](https://github.com/deledzis/Vocabri/actions/workflows/conditional-merge.yml/badge.svg)](https://github.com/deledzis/Vocabri/actions/workflows/conditional-merge.yml)

Vocabri is a modern, offline‑first Android app for building your personal vocabulary. Add words with
translations and examples, browse by parts of speech, and prepare for future training flows. A Kotlin
Multiplatform shared module is already in place for future iOS support.

## Features

- Add words with:
    - Multiple translations
    - Usage examples
    - Part‑of‑speech specific metadata (noun gender/plural, verb forms/management, adjective/adverb degrees)
- Browse grouped dictionary summaries (including "All words")
- Per‑category detailed lists with deletion
- Offline‑first local database (SQLDelight)
- Debug helper: long‑press the + button on the main screen to generate and save a random word

Planned/roadmap:

- Training mode (quizzes/flashcards, spaced repetition)
- Discover and search/filter experiences
- Edit existing words
- Optional cloud sync
- iOS app using the existing shared module

## Tech stack

- Language: Kotlin (JDK 21)
- UI: Jetpack Compose, Material 3, Compose Navigation
- DI: Koin
- Concurrency: Kotlin Coroutines
- Persistence: SQLDelight 2.x (type‑safe SQL, reactive flows)
- Logging: Kermit
- Analytics & Stability: Firebase Analytics and Crashlytics
- Multiplatform: Kotlin Multiplatform shared module (Android + iOS targets)
- Quality: Spotless (ktlint + Compose rules), Detekt, Kover code coverage

## Project structure

- app: Android app, Compose UI, navigation, ViewModels, Koin appModule
- domain: Domain models, repository contracts, use cases (add/observe/delete/generate random), Koin domainModule
- data: SQLDelight database, data sources, repository implementations, ID generator, Koin dataModule
- core:
    - core/logger: Kermit logger abstractions
    - core/utils: common utilities (ID generator abstraction, Android resources bridge, etc.)
- shared: Kotlin Multiplatform module prepared for iOS frameworks
- build-logic: Custom Gradle convention plugins (Android app/library, Spotless, Detekt, Kover)

## Requirements

- Android Studio (latest stable recommended)
- JDK 21
- Android SDK 35 (minSdk 24)

## Getting started

1) Clone the repo and open it in Android Studio. Gradle sync should configure everything.
2) Run the app:
    - Android Studio: select the app configuration and Run
    - CLI: `./gradlew :app:installDebug` (or `./gradlew :app:assembleDebug` to build only)
3) Try it out:
    - Use the bottom navigation to go to Dictionary, Training, Discover, Settings
    - Tap the + to add a word, or long‑press the + to auto‑add a random word (debug)

Notes:

- A google-services.json is included for convenience. To use your own Firebase project, replace
  app/google-services.json.
- The local database file is created automatically (SQLDelight + AndroidSqliteDriver).

## Build, test, and quality

Common tasks (run from project root):

- Format code: `./gradlew spotlessApply`
- Static analysis: `./gradlew detekt`
- Unit tests (JVM): `./gradlew test`
- UI tests (instrumented): start an emulator/device, then `./gradlew :app:connectedAndroidTest`
- Code coverage (Kover): `./gradlew koverHtmlReport` (aggregated at the root), `./gradlew koverXmlReport`

Testing libraries and approach:

- JUnit 4, Kotlin Coroutines Test, MockK
- Robolectric for Android unit tests where needed
- Compose UI testing for instrumented tests

## Database

- SQLDelight schema and queries live in data/src/main/sqldelight
- Database name: vocabri.db
- Migrations and schema are generated/configured by the Gradle SQLDelight plugin

## Contributing

Contributions are welcome!

- Fork the repo and create a feature branch from main
- Keep changes focused and incremental
- Before opening a PR, please run locally:
    - `./gradlew spotlessApply detekt test koverXmlReport`
- Open a PR and ensure CI checks pass

If you plan a larger change, consider opening an issue first to discuss the approach.

## License

This project is licensed under the MIT License. See the LICENSE file for details.
