# Recipe App

An Android app for browsing random recipes, searching by name, and viewing ingredients and cooking instructions. Recipes come from TheMealDB API, and favourites are saved locally on the device.

## Technologies

- Kotlin with Jetpack Compose and Material 3 for the UI.
- Navigation Compose, ViewModel, coroutines, and Flow for navigation and state.
- Ktor and Gson for API requests and JSON parsing.
- Room for local storage, with KSP for code generation.
- Gradle for builds.

## File structure

```text
app/src/
|-- main/
|   |-- java/com/bcit/final_project/
|   |   |-- MainActivity.kt       # App entry point
|   |   |-- data/                 # Recipe models, API, repository, and database
|   |   `-- ui/
|   |       |-- main/             # Screens, navigation, and state
|   |       `-- theme/            # Colours, typography, and theme
|   |-- res/                      # Android resources
|   `-- AndroidManifest.xml
|-- test/                         # Unit tests
`-- androidTest/                  # Instrumented tests
app/build.gradle.kts              # App dependencies and build settings
gradle/                           # Gradle wrapper and dependency versions
```
