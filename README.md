`----------------Overview----------------------------------------------`

MovieApp is a modular Android application developed with Kotlin and Jetpack Compose.
It is built following Clean Architecture + MVI pattern to ensure scalability, testability, and maintainability.

`-------------Architecture-----------------------------------------------------`

The project follows a modular Clean Architecture + MVI pattern with the following modules:

**app** – Application entry point, dependency injection setup, and navigation host.

**core** – Core utilities, error handling, and functional helpers.

**core-ui** – Reusable UI components, MVI contracts, and theme definitions.

**data** – Data layer: Retrofit API services, DTOs, repositories, mappers, and paging sources.

**domain** – Business logic layer: Entities, UseCases, and Repository interfaces.

**presentation** – UI state management with ViewModels and MVI contracts.

`---------------Data Flow--------------------------------------------------------------------`

User interacts with Jetpack Compose UI.

UI sends an Event → handled by a ViewModel.

ViewModel calls a UseCase (Domain layer).

UseCase communicates with a Repository (Data layer).

Repository fetches data from API (Retrofit) or cache and maps it into domain models.

Result is returned as success/failure → ViewModel updates the State.

The Composable observes state and re-renders automatically.

`----------------Key Technologies Used----------------------------------------`

**Modular Clean Architecture + MVI**

**Jetpack Compose for UI**

**Hilt (Dagger Hilt) for dependency injection**

**Retrofit for API communication**

**Paging 3 for pagination**

**Kotlin Coroutines & Flow for async programming**

**JUnit 5 for unit testing**

`-------------------Future Improvements (if more time is available)--------------------`

=>Add UI Test Cases (Compose testing + Espresso)

=>Configure Network Security (certificate pinning, HTTPS)

=>Integrate Firebase Analytics for tracking

=>Improve UI for better user experience