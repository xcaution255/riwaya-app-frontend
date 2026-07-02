# RiwayaApp navigation module

Drop `src/commonMain/kotlin/com/RiwayaApp`

## 1. Gradle setup

`build.gradle.kts` (shared module), inside `commonMain.dependencies`:

```kotlin
implementation("org.jetbrains.androidx.navigation:navigation-compose:<latest>")
implementation("org.jetbrains.compose.material:material-icons-extended:<latest matching your compose version>")
implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:<latest>")
```

Check the current version numbers on the Compose Multiplatform navigation
docs — this API moved fast through 2.8.x betas/RCs and version pinning
matters. `material-icons-extended` is required because the bottom bar uses
outlined variants (`Icons.Outlined.Home`, `.Explore`, `.Person`) plus
`Icons.Outlined.Notifications` — the core `material-icons-core` artifact
only ships filled icons.

Also apply the Kotlin serialization plugin at the top of the same file:

```kotlin
plugins {
    kotlin("plugin.serialization") // or id("org.jetbrains.kotlin.plugin.serialization")
}
```

## 2. Wire it up

- Android `MainActivity`: `setContent { App() }`
- iOS `MainViewController.kt`: `ComposeUIViewController { App() }`
- Desktop: `Window(...) { App() }`

## 3. How the pieces fit together

```
App
 └─ RootNavGraph                 (owns ONE NavController for the whole app)
     ├─ AuthGraph (nested)       login / register / forgot-password
     └─ MainGraph → MainScaffold (owns its OWN NavController, for tabs)
                     ├─ top bar: title + notification bell / back arrow
                     ├─ bottom bar: Home / Explore / Profile
                     └─ inner NavHost: Home, Explore, Profile, Notifications
```

**Why two NavControllers?** The root one only ever holds two destinations
(auth vs. main) — it's not meant to model tab state. The inner one lives
inside `MainScaffold` and owns the bottom-bar tabs plus `Notifications`,
which is a pushed screen (not a tab) that hides the bottom bar and gets a
back arrow instead of the bell icon.

**Why is logout safe?** `Session` (a `StateFlow<Boolean>`) is the single
source of truth. `ProfileScreen` never navigates directly — it just calls
`onLogout()` → `Session.logout()`. `RootNavGraph` observes that flow and
reacts with:

```kotlin
navController.navigate(Route.AuthGraph) {
    popUpTo(0) { inclusive = true }
    launchSingleTop = true
}
```

`popUpTo(0) { inclusive = true }` clears the *entire* back stack — Home,
Explore, Profile, Notifications, and the inner NavController's saved tab
states all go away. A back-press from Login can never resurface the old
session, and there's no leftover state to leak between accounts.

## 4. Things to swap for production

- `Session` — replace the in-memory `MutableStateFlow` with your real
  token/repository-backed auth manager.
- `LaunchedEffect(isLoading) { delay(900); ... }` in each auth screen —
  replace with your actual network call; call the success callback on
  success, set `errorMessage` on failure.
- Sample data in `HomeScreen`, `ExploreScreen`, `ProfileScreen`,
  `NotificationsScreen` — wire to your real data sources.

--------------------------------CLEAN ARCHITECTURE----------------------------------------
root/
├── shared/                             # Your main multiplatform library
│   └── src/
│       └── commonMain/kotlin/org/project/
│           ├── core/                   # Shared util tools, string modifiers, exceptions
│           ├── di/                     # Dependency Injection framework setup (Koin)
│           │
│           ├── domain/                 # 1. DOMAIN LAYER
│           │   ├── model/              # Pure Kotlin business models (e.g., Note, User)
│           │   ├── repository/         # Abstract Interface contracts for repositories
│           │   └── usecase/            # Single-responsibility action classes (e.g., GetNotesUseCase)
│           │
│           ├── data/                   # 2. DATA LAYER
│           │   ├── remote/             # Ktor HTTP network clients, API payloads, DTOs
│           │   ├── local/              # Room / SQLDelight local offline tables
│           │   ├── mapper/             # Extensions converting DTOs ➔ Domain Models
│           │   └── repository/         # Implementation classes matching domain interfaces
│           │
│           └── presentation/           # 3. PRESENTATION LAYER
│               ├── components/         # Reusable styling items (Buttons, Input text fields)
│               ├── home/               # Feature folders grouping specific modules
│               │   ├── HomeScreen.kt   # UI Layout containing @Composable declarations
│               │   ├── HomeViewModel.kt# Component coordinating UI State transitions
│               │   └── HomeState.kt    # Data classes encapsulating what the screen shows
│               └── navigation/         # Navigation setups (Voyager / Jetpack Compose Navigation)
│
├── androidApp/                         # Tiny native launcher entry point for Android
└── iosApp/                             # Xcode launcher config linking to shared framework

