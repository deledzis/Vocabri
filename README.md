# Vocabri

[![DeepSource](https://app.deepsource.com/gh/deledzis/Vocabri.svg/?label=code+coverage&show_trend=true&token=Q7RU9Z2I1GsSG30Ivnl0ZdF0)](https://app.deepsource.com/gh/deledzis/Vocabri/)
[![DeepSource](https://app.deepsource.com/gh/deledzis/Vocabri.svg/?label=active+issues&show_trend=true&token=Q7RU9Z2I1GsSG30Ivnl0ZdF0)](https://app.deepsource.com/gh/deledzis/Vocabri/)
[![DeepSource](https://app.deepsource.com/gh/deledzis/Vocabri.svg/?label=resolved+issues&show_trend=true&token=Q7RU9Z2I1GsSG30Ivnl0ZdF0)](https://app.deepsource.com/gh/deledzis/Vocabri/)

Vocabri is a modern and intuitive Android app designed to help you create, manage, and learn from
your personalized vocabulary lists.
Other platforms are planned in the future too!

## Features

### Core Functionality

- **Add Words:** Quickly add words to your dictionary with translations, part of speech, and
  examples.
- **Edit Words:** Modify any details of your added words.
- **Delete Words:** Remove entries with a simple action.
- **Categorization:** Organize words by parts of speech for better learning.

### Advanced Features

- **Translation Management:** Add multiple translations for each word and manage them efficiently.
- **Examples:** Include usage examples for context.
- **Offline First:** Works seamlessly offline, with future support for cloud sync.
- **Keyboard Optimization:** Smart keyboard actions for improved user experience.

### Future Plans

- **Training Mode:** Practice words with quizzes, flashcards, and spaced repetition.
- **Search & Filter:** Quickly find words or categories.
- **Cloud Sync:** Backup and sync your data across devices.

## Technologies Used

### Architecture

- **Kotlin Multiplatform Ready:** Designed to support future multiplatform development.
- **Modern Android Architecture:**
    - **MVVM + MVI:** Ensures a clear separation of concerns and predictable UI behavior.
    - **SQLDelight:** Handles database operations with type-safe SQL.
    - **Koin:** Dependency injection for testable and modular architecture.

### Testing

- **Unit Tests:** Comprehensive testing for ViewModels and Use Cases.
- **Integration Tests:** Verifying end-to-end behavior of screens.
- **Mocking Framework:** MockK for repository and service mocking.

### UI

- **Jetpack Compose:** A declarative approach to building user interfaces.
- **Material Design 3:** Modern design principles for a sleek and consistent experience.
