# Rick and Morty Browser

A modern Android application that showcases the Rick and Morty universe by displaying characters, their details, and episodes. Built with clean architecture principles, reactive programming, and offline-first caching.

## Setup Instructions

### Prerequisites

- Android Studio Jellyfish (2024.1.1) or later
- JDK 11 or higher
- Minimum SDK: Android 7.0 (API 24)
- Target SDK: Android 15 (API 36)

### Clone and Build

1. Clone the repository:

   ```bash
   git clone <repository-url>
   cd RickAndMortyBrowser
   ```

2. Open in Android Studio:
   - File → Open → Select the project root directory
   - Android Studio will automatically sync Gradle files

3. Configure local properties (optional):
   - Create/update `local.properties` in the project root
   - Add: `sdk.dir=/path/to/your/android/sdk`

4. Build and run:
   - Build: `./gradlew build` or Build → Build Bundle(s) / APK(s)
   - Run: Select a target device and press Shift+F10 or Run → Run 'app'

### Running Tests

**Unit Tests:**

```bash
./gradlew test
```

**UI/Instrumented Tests:**

```bash
./gradlew connectedAndroidTest
```

**Generate Coverage Report:**

```bash
./gradlew test connectedAndroidTest --no-daemon
```

## Architecture Decisions

### MVVM + Repository Pattern

**Why This Approach:**

- **Separation of Concerns**: ViewModels manage UI state and orchestration, Repositories handle data fetching and caching. This makes each component testable in isolation.
- **Maintainability**: Clear boundaries between data, presentation, and business logic reduce coupling and make refactoring safer.
- **Testability**: Repository layer can be mocked; ViewModel logic doesn't depend on Android framework code directly.
- **Scalability**: New data sources (API, cache, local database) can be added to the Repository without touching ViewModels.

The repository pattern specifically provides a clean abstraction over Retrofit (network), Room (local database), and NetworkMonitor (connectivity), making it trivial to swap implementations for testing or add new features like different cache strategies.

### UiState Sealed Class Pattern

**Why Not State Holders or Multiple LiveData/StateFlow Objects:**

```kotlin
sealed class CharacterListUiState {
    data object Loading : CharacterListUiState()
    data class Success(val characters: List<Character>) : CharacterListUiState()
    data object Empty : CharacterListUiState()
    data class Error(val message: String) : CharacterListUiState()
    // ... more states
}
```

**Benefits:**

- **Type Safety**: When you pattern match on the sealed class, Kotlin's compiler ensures you handle all cases. If you add a new state, you get compile errors in all consumers.
- **Single Source of Truth**: One StateFlow instead of multiple Boolean/String flows reduces state coherence bugs (e.g., showing both Success and Error simultaneously becomes impossible).
- **Explicit States**: Every valid UI state is explicitly named, making code self-documenting.
- **Composable Testing**: Compose tests can directly check `uiState is CharacterListUiState.Success` without managing multiple state variables.

### Offline Support: Cache-First Strategy

**How It Works:**

1. **On Every Request**:
   - Emit `Result.Loading`
   - Check Room database for cached data
   - If found, emit `Result.Success(cachedData)` immediately to reduce perceived latency
   - Attempt network request (if online)

2. **On Network Success**:
   - Update Room database with fresh data
   - Emit `Result.Success(freshData)`

3. **On Network Failure**:
   - If data was already cached, the user sees the cached data from step 1
   - If no cache exists, emit `Result.Error`

4. **Offline Scenario**:
   - Cache is available: User sees stale data with "offline" snackbar notification
   - Cache unavailable: UI shows `OfflineNoCache` state

**Why Cache-First:**

- **UX**: Users see content immediately while the app fetches fresher data in the background
- **Resilience**: Network hiccups don't break the app if data exists locally
- **Bandwidth**: Reduces redundant API calls; if the same character is viewed twice, the second view uses cache

### Search Debouncing Implementation

**Implementation Details:**

```kotlin
searchQuery.debounce(AppConstants.SEARCH_DEBOUNCE_MS) // 500ms
    .combine(selectedStatus, selectedSpecies) { query, status, species ->
        Triple(query, status, species)
    }
    .collectLatest { (query, status, species) ->
        // Execute search
    }
```

**Why Debouncing:**

- **API Efficiency**: Prevents firing a search request on every keystroke (typing "Rick" would normally fire 4 requests: "R", "Ri", "Ric", "Rick")
- **Reduced Server Load**: Fewer unnecessary API calls mean lower server costs and faster responses for actual final searches
- **Better UX**: Avoids rapid UI state flashing between loading states
- **User Intent**: Assumes users pause briefly before searching, so 500ms debounce captures intent without feeling laggy

**Technical Note**: Uses `collectLatest` to cancel in-flight searches if the user types again before results arrive, combining debounce with smart request cancellation.

## Assumptions Made During Development

1. **First 3 Episodes Only**: Character detail screens show a maximum of 3 episodes per character. If a character appears in 100 episodes, the app displays only the first 3 to reduce payload size and UI complexity.

2. **Species Filter Uses Predefined Values**: The species filter (`Filters.SPECIES_HUMAN`, `Filters.SPECIES_ALIEN`) are hardcoded based on common values in the Rick and Morty API. Dynamic species filtering from the API is not implemented.

3. **Status Filter is Required for Some Filters**: The status filter (Alive/Dead/Unknown) is a primary filter. Only predefined statuses are supported, not custom user-provided statuses.

4. **Pagination on First Page Only**: Pagination (load next page) is disabled when a search or filter is active. Users must clear filters to browse paginated results. This simplifies state management and aligns with typical search UI patterns.

5. **Character ID is Immutable**: Once a Character object is created, its ID never changes. This allows safe use of ID as a database primary key.

6. **Network Timeout Handling**: Network requests have a 30-second timeout. Requests exceeding this are treated as errors with a specific timeout message. Retry is user-initiated via UI buttons.

7. **Room Database Single Instance**: The app maintains a single AppDatabase instance (Hilt singleton). All data access goes through this instance to prevent SQLite concurrency issues.

## Libraries Used and Why

### Core Android Framework

- **androidx-core-ktx** (1.17.0): Kotlin extensions for Android framework (e.g., `repeatOnLifecycle`, `viewLifecycleScope`). Reduces boilerplate.
- **androidx-lifecycle-runtime-ktx** (2.10.0): Lifecycle-aware coroutines. Ensures coroutines respect Activity/Fragment lifecycle.
- **androidx-activity-compose** (1.12.3): Bridge between Activity and Compose. Enables `@Composable` directly in Activity.

### UI Framework

- **androidx-compose-\*** (2024.09.00): Jetpack Compose for declarative UI. Faster iteration, fewer bugs than XML layouts, better state management integration.
- **androidx-compose-material3**: Material 3 design system. Provides modern, accessible UI components (Material You design).
- **androidx-navigation-compose** (2.9.5): Type-safe navigation for Compose. Replaces Fragment-based navigation with Compose routes.
- **androidx-hilt-navigation-compose**: Hilt integration with Compose navigation. Enables DI for navigated destinations.

### Dependency Injection

- **dagger-hilt-android** (2.59.1): Compile-time DI framework. Reduces boilerplate vs. Dagger 2, prevents runtime errors earlier.
- **hilt-android-testing**: Hilt testing utilities for unit tests.

### Networking

- **retrofit** (2.11.0): Type-safe HTTP client. Automatic JSON serialization, interceptor support, cleaner code than URLConnection.
- **retrofit-converter-gson**: Gson serialization plugin for Retrofit. Handles JSON ↔ Kotlin object conversion.
- **okhttp** (4.12.0): HTTP engine for Retrofit. Handles connection pooling, retry logic, and HTTP-layer concerns.
- **okhttp-logging-interceptor**: Logs HTTP requests/responses for debugging network issues.

### Serialization

- **gson** (2.11.0): JSON serialization library. Fast, mature, works well with Kotlin data classes.

### Local Database

- **androidx-room-runtime** (2.7.2): Type-safe SQLite wrapper. Compile-time query verification, automatic migration support, coroutine-friendly.
- **androidx-room-ktx**: Kotlin extensions for Room (e.g., `flow` from queries).

### Image Loading

- **coil-compose** (2.7.0): Image loading library optimized for Compose. Handles caching, disk storage, and placeholder logic automatically.

### Async Programming

- **kotlinx-coroutines-android** (1.9.0): Coroutines for Android. Replaces callbacks/RxJava, cleaner concurrency model, cancellation support.

### Testing

- **junit** (4.13.2): Standard unit testing framework. Lightweight, widely compatible.
- **mockk** (1.13.5): Kotlin-friendly mocking library. More intuitive syntax than Mockito for Kotlin, better coroutine support.
- **turbine** (1.0.0): Testing library for Kotlin Flow. Simplifies collecting and asserting Flow emissions in tests.
- **kotlinx-coroutines-test**: Coroutine test utilities (TestDispatchers, advanceTimeBy, etc.).
- **androidx-room-testing**: In-memory Room database for testing without SQLite file I/O.
- **androidx-compose-ui-test-junit4**: Compose testing framework. Write UI tests that read/click Compose elements.
- **androidx-junit** (1.3.0): AndroidX testing framework for instrumented tests.
- **androidx-espresso-core** (3.7.0): UI testing framework (underlying Compose tests use Espresso infrastructure).

## Known Limitations

1. **No Pagination Search Results**: When a search is active, pagination (load more) is disabled. Large search result sets don't paginate.

2. **No Filter Reset**: Filters persist after clearing the search query. Users must manually tap filters to deselect them.

3. **No Offline Indicator for Stale Cache**: The app shows a snackbar when offline but doesn't indicate how old the cached data is. Timestamps aren't stored.

4. **First Request Always Fetches**: Even if data is cached, the app always attempts a network request. This ensures freshness but may cause unnecessary API calls on slow networks.

5. **No Pull-to-Refresh**: The app doesn't support pull-to-refresh gestures. Refresh only happens via retry buttons.

6. **Single-instance Cache**: If the user has 2 devices with the same account, the cache on each device doesn't sync. Caches are fully independent.

7. **Episode URLs Assumed Valid**: Episode URLs from the API are assumed to be well-formed. If malformed URLs exist, the app may crash during Episode parsing.

8. **No Request Retry Logic**: Failed network requests are not automatically retried. Retry is manual via UI buttons.

9. **Cache Never Expires**: Cached data doesn't expire by default. A cleared app cache is required to force a full refresh.

10. **No Bandwidth Limitation**: The app doesn't adapt image loading based on network speed (e.g., loading thumbnails on slow 3G). All images use the same resolution.

## Testing

### Test Coverage

**Unit Tests (JUnit4 + MockK + Turbine):**

- `CharacterRepositoryTest.kt`: Tests caching behavior, error handling, and online/offline scenarios.
- `CharacterListViewModelTest.kt`: Tests UI state emissions, search debounce, and filter logic.
- `DetailViewModelTest.kt`: Tests episode loading, offline handling, and retry behavior.

**UI Tests (Compose Testing):**

- `CharacterListScreenTest.kt`: Tests screen rendering, navigation callbacks, and state-specific UI elements.

### Running Tests

```bash
# Unit tests
./gradlew test

# Instrumented tests (requires device or emulator)
./gradlew connectedAndroidTest

# Both
./gradlew test connectedAndroidTest
```

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/rickandmortybrowser/
│   │   │       ├── data/
│   │   │       │   ├── local/          # Room database
│   │   │       │   ├── remote/         # Retrofit API
│   │   │       │   └── repository/     # Data abstraction
│   │   │       ├── ui/
│   │   │       │   ├── characters/     # Character list screen
│   │   │       │   └── detail/         # Character detail screen
│   │   │       ├── di/                 # Hilt dependency injection
│   │   │       └── util/               # Utilities (constants, network monitor)
│   │   └── res/                        # Resources (strings, colors, themes)
│   ├── test/                           # Unit tests
│   └── androidTest/                    # Instrumented tests
└── build.gradle.kts                    # App build configuration
```

## Development Guidelines

1. **State Management**: All UI state goes through sealed class UiState objects and StateFlow. Avoid multiple correlated StateFlow/LiveData fields.

2. **Coroutine Scope**: Use `viewModelScope` for ViewModel coroutines to ensure automatic cancellation on ViewModel clear.

3. **Error Handling**: Use Result sealed class for all repository operations. Never throw exceptions from repositories; wrap in Result.Error.

4. **Testing**: Write tests for ViewModels and Repositories. UI tests verify navigation and state rendering. Mock all external dependencies (API, database, NetworkMonitor).

5. **Naming**: Follow Kotlin conventions (camelCase for variables/functions, PascalCase for classes). Use descriptive names for state objects and callback functions.

## Future Enhancements

- [ ] Add pull-to-refresh gesture
- [ ] Implement cache expiration (e.g., 24-hour TTL)
- [ ] Support dynamic species filter fetching from API
- [ ] Add pagination for search results
- [ ] Implement bandwidth-aware image loading
- [ ] Add favorites/bookmarking feature
- [ ] Integrate local search (database queries)
- [ ] Add character filtering by origin/location
- [ ] Implement automatic retry with exponential backoff

---

**Author**: Built as a modern Android architecture reference project.



