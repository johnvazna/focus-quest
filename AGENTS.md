# dfm-agent — AGENT.md

## Purpose

This file defines the architectural, engineering, and code-generation rules that every AI coding agent must follow when working on the **dfm-agent** repository.

These rules are model-agnostic and apply regardless of:

- AI provider
- IDE integration
- coding assistant
- execution environment
- model version

The agent MUST follow these rules consistently across all code generation, refactoring, architecture decisions, testing, and Gradle changes.

When a requested implementation conflicts with this document, the agent MUST preserve the architectural constraints defined here unless explicitly instructed otherwise by the repository maintainers.

---

# 1. Rule Levels

The following keywords define requirement strength:

- **MUST**: Required. The rule cannot be ignored.
- **MUST NOT**: Forbidden.
- **SHOULD**: Expected default unless there is a justified technical reason.
- **SHOULD NOT**: Strongly discouraged unless there is a justified technical reason.
- **MAY**: Optional.

Architectural boundaries defined with MUST or MUST NOT take precedence over implementation convenience.

---

# 2. Project Principles

The **dfm-agent** project is designed for long-term scalability and maintainability.

The architecture MUST follow:

- Clean Architecture
- SOLID principles
- Feature-first modularization
- Unidirectional Data Flow
- Dependency Inversion
- Explicit module boundaries
- High cohesion
- Low coupling
- Testability by design
- Clear ownership of responsibilities
- Scalable Gradle structure
- Dynamic Feature Modules only when delivery requirements justify them

The agent MUST NOT introduce architectural shortcuts merely to reduce implementation effort.

The agent MUST prefer simple, explicit, maintainable solutions over speculative abstractions.

---

# 3. Before Writing Code

Before creating or modifying code, the agent MUST:

1. Inspect the current module structure.
2. Identify the feature that owns the requested functionality.
3. Identify the correct architectural layer.
4. Inspect similar implementations already present in the repository.
5. Check existing abstractions before creating new ones.
6. Verify dependency direction.
7. Check whether the functionality belongs to an existing module.
8. Avoid creating shared abstractions prematurely.
9. Check relevant tests.
10. Preserve existing project conventions unless they violate this document.

The agent MUST prefer existing architectural patterns in the repository over introducing new patterns.

The agent MUST NOT create a new abstraction when an existing contract already solves the same responsibility.

---

# 4. High-Level Architecture

The project follows Clean Architecture.

The conceptual dependency direction is:

```text
Presentation → Domain ← Data
```

Domain represents the most stable architectural layer.

Outer layers MAY depend on inner abstractions.

Inner layers MUST NOT depend on outer implementations.

The architecture MUST make business rules independently testable.

---

# 5. Domain Layer

The Domain layer owns business behavior.

It MAY contain:

- Business models
- Business rules
- Use cases
- Repository contracts
- Domain errors
- Validation rules
- Business policies

The Domain layer MUST:

- Remain framework-independent whenever possible
- Prefer pure Kotlin
- Remain independently testable
- Expose business-oriented contracts

The Domain layer MUST NOT depend on:

- Android SDK
- Jetpack Compose
- Retrofit
- OkHttp
- Room
- Data implementations
- Presentation
- ViewModel
- Android Context
- Fragment
- Activity

Infrastructure details MUST NOT leak into Domain.

---

# 6. Data Layer

The Data layer owns infrastructure and data-access implementation details.

It MAY contain:

- Repository implementations
- Remote data sources
- Local data sources
- DTOs
- Database entities
- API interfaces
- Cache implementations
- Persistence implementations
- Data mappers
- Serialization models

The Data layer MAY depend on Domain contracts.

The Data layer MUST NOT expose infrastructure-specific models outside its boundary.

The following types MUST NOT cross the Data boundary:

- DTOs
- Database entities
- Retrofit `Response<T>`
- Retrofit-specific exceptions
- Room-specific types
- Cursor
- Persistence implementation types

Infrastructure models MUST be mapped into Domain models before crossing the layer boundary.

---

# 7. Presentation Layer

The Presentation layer owns UI behavior.

It MAY contain:

- Jetpack Compose UI
- ViewModels
- UI State
- UI Events
- UI Effects
- Presentation models
- Presentation mappers
- Route composables
- Screen composables

The Presentation layer MAY depend on Domain.

The Presentation layer MUST NOT:

- Access Retrofit directly
- Access Room directly
- Instantiate repository implementations directly
- Contain infrastructure code
- Contain persistence logic
- Contain networking logic
- Contain business rules that belong to Domain

UI models SHOULD remain presentation-specific.

---

# 8. Feature-First Modularization

The **dfm-agent** project follows feature-first modularization.

Code belongs to a feature by default.

A feature MUST represent a business capability or user-facing product capability.

Examples:

```text
authentication
home
profile
payments
transactions
settings
reports
```

Modules MUST NOT be created only around generic technical categories when the responsibility belongs to a specific feature.

Prefer:

```text
feature/payments
```

over:

```text
feature/forms
feature/viewmodels
feature/buttons
```

---

# 9. Core Modules

Core modules exist only for stable cross-cutting capabilities.

Typical core modules MAY include:

```text
core/
    common/
    designsystem/
    ui/
    navigation/
    network/
    database/
    analytics/
    testing/
```

Critical rule:

> CORE IS NOT A SHARED DUMPING GROUND.

Code MUST remain inside its owning feature unless multiple independent features genuinely require the same stable abstraction.

Before moving code into `core`, the agent MUST verify:

1. At least two independent features require it.
2. The abstraction is stable.
3. Ownership is clear.
4. The abstraction does not contain feature-specific behavior.
5. Moving it does not create unnecessary global coupling.

The agent MUST NOT move code into `core` merely because it may be reused in the future.

---

# 10. Recommended Project Structure

The expected high-level structure is:

```text
dfm-agent/
│
├── app/
├── build-logic/
│
├── core/
│   ├── common/
│   ├── designsystem/
│   ├── ui/
│   ├── navigation/
│   ├── network/
│   ├── database/
│   ├── analytics/
│   └── testing/
│
├── feature/
│   ├── authentication/
│   │   ├── api/
│   │   └── impl/
│   │
│   ├── home/
│   │   ├── api/
│   │   └── impl/
│   │
│   └── profile/
│       ├── api/
│       └── impl/
│
└── dynamic-feature/
    └── <feature-name>/
```

The project structure MAY evolve as the application grows.

New modules MUST have a clear architectural or delivery reason.

---

# 11. Feature API Modules

A feature API module exposes the minimum contract required by other modules.

It MAY contain:

- Navigation contracts
- Feature entry points
- Public feature capabilities
- Stable public interfaces
- Public domain contracts when cross-feature access is required

It MUST NOT expose:

- Internal ViewModels
- Internal repositories
- DTOs
- Database entities
- Internal Compose screens
- Internal implementation classes
- Internal data sources

The public API surface MUST remain minimal.

---

# 12. Feature Implementation Modules

Implementation details remain private to the feature.

Recommended structure:

```text
feature/<feature-name>/impl/

    presentation/
    domain/
    data/
    di/
```

For sufficiently complex features, subpackages MAY be introduced:

```text
presentation/
    screen/
    state/
    model/
    mapper/

domain/
    model/
    repository/
    usecase/

data/
    repository/
    remote/
    local/
    mapper/

di/
```

The agent SHOULD avoid unnecessary package fragmentation for small features.

Package structure MUST improve ownership and discoverability, not create ceremony.

---

# 13. Cross-Feature Dependencies

Features MUST NOT depend directly on implementation modules of other features.

Forbidden:

```text
FeatureAImpl → FeatureBImpl
```

Allowed:

```text
FeatureAImpl → FeatureBApi
```

Cross-feature communication MUST happen through explicit contracts.

If two features need the same capability, the agent MUST evaluate:

1. Which feature owns the capability?
2. Should the capability be exposed through that feature's API?
3. Is the capability genuinely cross-cutting?
4. Only then should extraction to `core` be considered.

Bidirectional feature dependencies are forbidden.

Circular Gradle dependencies are forbidden.

---

# 14. Dynamic Feature Modules

Dynamic Feature Modules are a delivery mechanism.

They are NOT an architectural layering mechanism.

The agent MUST NOT convert a feature into a Dynamic Feature Module solely because the feature is architecturally independent.

Dynamic Feature Modules SHOULD only be used when there is a product or delivery reason such as:

- On-demand installation
- Conditional delivery
- Large binary size
- Rarely used functionality
- Optional workflows
- Feature delivery optimization

A normal feature module SHOULD be preferred when on-demand delivery is unnecessary.

---

# 15. Dynamic Feature Contracts

When communication between the base application and a Dynamic Feature Module is required, the contract MUST live in a non-dynamic module.

Preferred structure:

```text
feature/payments/api
        ↑
dynamic-feature/payments
```

The base application MUST NOT depend on implementation details contained inside a Dynamic Feature Module.

A Dynamic Feature Module MAY depend on stable contracts exposed by the base project.

Dynamic feature availability MUST NOT be assumed.

---

# 16. Dynamic Feature Installation State

The application MUST handle Dynamic Feature availability explicitly.

Recommended installation states:

```kotlin
sealed interface DynamicFeatureState {
    data object NotInstalled : DynamicFeatureState
    data class Installing(val progress: Int?) : DynamicFeatureState
    data object Installed : DynamicFeatureState
    data class Failed(val cause: Throwable?) : DynamicFeatureState
}
```

The exact implementation MAY vary.

The architecture MUST account for:

- Feature not installed
- Installation in progress
- Installation completed
- Installation failed
- Navigation attempted before installation
- Retry behavior

UI and navigation MUST gracefully handle unavailable features.

---

# 17. Dependency Rules

Dependencies MUST point toward stable abstractions.

Allowed examples:

```text
app → feature
feature → core
feature:impl → feature:api
data → domain
presentation → domain
```

Forbidden examples:

```text
domain → data
domain → presentation
domain → Android framework
core → feature
feature:A:impl → feature:B:impl
```

Circular dependencies MUST NOT exist.

---

# 18. Gradle Dependency Visibility

Gradle dependencies SHOULD use:

```kotlin
implementation(...)
```

by default.

The `api(...)` configuration MUST only be used when the dependency is intentionally part of the module's public API.

Transitive dependencies SHOULD be minimized.

A module MUST NOT expose implementation dependencies without a clear architectural reason.

---

# 19. Build Logic

Shared Gradle configuration SHOULD live in `build-logic`.

Convention plugins SHOULD be preferred over duplicated Gradle configuration.

Examples:

```text
android.application
android.library
android.feature
android.dynamic-feature
android.compose
android.hilt
android.room
android.testing
```

Dependency versions SHOULD be centralized in:

```text
gradle/libs.versions.toml
```

Individual modules MUST NOT hardcode dependency versions unless there is a documented technical reason.

---

# 20. SOLID Principles

SOLID principles are mandatory architectural constraints.

## 20.1 Single Responsibility Principle

Each class, component, and module SHOULD have one clear reason to change.

The agent MUST NOT create classes that mix unrelated responsibilities such as:

- UI rendering
- business logic
- persistence
- networking
- analytics

## 20.2 Open/Closed Principle

Prefer extension through composition and stable contracts.

The agent SHOULD NOT introduce inheritance solely for code reuse.

## 20.3 Liskov Substitution Principle

Implementations MUST preserve the behavioral expectations of their contracts.

Implementations MUST NOT require incompatible assumptions that violate the contract they implement.

## 20.4 Interface Segregation Principle

Prefer small, focused contracts.

Avoid large interfaces containing unrelated capabilities.

## 20.5 Dependency Inversion Principle

High-level business logic MUST depend on abstractions.

Infrastructure MUST implement those abstractions.

Domain MUST NOT depend on infrastructure.

---

# 21. Repository Rules

Repository interfaces SHOULD live in Domain when they represent business-facing data contracts.

Repository implementations MUST live in Data.

Example:

```text
domain/repository/UserRepository.kt
data/repository/DefaultUserRepository.kt
```

Repositories coordinate data sources.

Repositories MUST NOT expose:

- Retrofit DTOs
- Room entities
- Retrofit responses
- Database-specific models
- Infrastructure implementation details

Repositories SHOULD return Domain models.

---

# 22. Use Cases

Use cases represent meaningful business operations.

Good examples:

```text
AuthenticateUser
ObserveAccountBalance
SubmitPayment
GetUserProfile
ObserveTransactions
```

A use case SHOULD be introduced when it:

- Encapsulates business logic
- Coordinates multiple repositories
- Represents an application capability
- Provides reusable Domain behavior
- Improves business-rule testability

The agent SHOULD NOT create a use case that merely proxies a repository method without adding meaningful Domain value.

Avoid unnecessary chains such as:

```text
ViewModel
    ↓
TrivialUseCase
    ↓
Repository
    ↓
DataSource
    ↓
Api
```

Clean Architecture MUST NOT become unnecessary boilerplate.

---

# 23. Model Boundaries

Models SHOULD remain isolated by layer when their responsibilities differ.

Typical models:

```text
Remote:       UserDto
Local:        UserEntity
Domain:       User
Presentation: UserUiModel
```

Mappings SHOULD be explicit.

Example:

```kotlin
UserDto.toDomain()
UserEntity.toDomain()
User.toUiModel()
```

DTOs and database entities MUST NOT leak into Domain or Presentation.

---

# 24. State Management

Presentation follows Unidirectional Data Flow.

The recommended model is:

```text
UiState
UiEvent
UiEffect
```

`UiState` represents persistent screen state.

`UiEvent` represents user or system actions.

`UiEffect` represents one-time side effects.

Example:

```kotlin
data class PaymentUiState(
    val isLoading: Boolean = false,
    val payment: PaymentUiModel? = null,
    val error: UiError? = null,
)

sealed interface PaymentUiEvent {
    data object Retry : PaymentUiEvent

    data class Submit(
        val amount: BigDecimal,
    ) : PaymentUiEvent
}

sealed interface PaymentUiEffect {
    data object NavigateBack : PaymentUiEffect

    data class ShowMessage(
        val message: String,
    ) : PaymentUiEffect
}
```

---

# 25. ViewModel Rules

ViewModels MUST expose immutable state.

Prefer:

```kotlin
val state: StateFlow<UiState>
```

Avoid exposing:

```kotlin
val state: MutableStateFlow<UiState>
```

Mutable state MUST remain private.

ViewModels SHOULD:

- Coordinate presentation behavior
- Invoke Domain capabilities
- Transform Domain output into UI state
- Handle UI events
- Emit one-time effects

ViewModels MUST NOT:

- Access Retrofit directly
- Access Room directly
- Contain persistence implementation details
- Own business rules that belong in Domain

---

# 26. Compose Rules

Composable functions SHOULD be stateless whenever practical.

Preferred structure:

```text
FeatureRoute(...)
FeatureScreen(...)
```

A Route composable MAY:

- Obtain the ViewModel
- Collect state
- Observe effects
- Integrate navigation

A Screen composable SHOULD:

- Receive immutable state
- Emit events
- Remain unaware of ViewModel implementation
- Avoid business logic
- Remain previewable and testable where possible

Prefer:

```kotlin
ProfileScreen(
    state = state,
    onEvent = viewModel::onEvent,
)
```

Avoid:

```kotlin
ProfileScreen(
    viewModel = viewModel,
)
```

ViewModels SHOULD NOT be propagated deeply through the composable hierarchy.

---

# 27. Navigation

Features SHOULD own their internal navigation.

Other modules SHOULD navigate through stable contracts.

Concrete implementation screens SHOULD NOT be exposed across feature boundaries.

Navigation arguments SHOULD contain stable identifiers.

Prefer:

```text
userId: String
transactionId: String
```

Avoid passing complex Domain objects through navigation when an identifier can be used instead.

Dynamic feature navigation MUST account for installation state before attempting to open the destination.

---

# 28. Coroutines

Structured concurrency MUST be used.

ViewModels SHOULD use:

```kotlin
viewModelScope
```

Repositories SHOULD expose:

- `suspend` functions
- `Flow<T>`

depending on the required behavior.

The agent MUST NOT introduce:

```kotlin
GlobalScope
```

The agent MUST NOT create unmanaged long-lived coroutine scopes without a clear lifecycle owner.

Dispatchers SHOULD be injectable when execution context matters for testability.

---

# 29. Flow

`Flow` SHOULD be used for observable streams of data.

`StateFlow` SHOULD be used for observable state.

The agent SHOULD avoid unnecessary conversion chains between reactive primitives.

Flows MUST respect lifecycle ownership.

UI collection SHOULD use lifecycle-aware APIs.

---

# 30. Error Handling

Infrastructure-specific errors MUST NOT leak across architectural boundaries.

Infrastructure errors SHOULD be converted into application or Domain errors.

Example:

```text
IOException
    ↓
NetworkError

HTTP 401
    ↓
Unauthorized

Database exception
    ↓
StorageError
```

Presentation SHOULD NOT need to understand:

- `IOException`
- `HttpException`
- `SQLException`
- Retrofit internals
- Room internals

Error contracts SHOULD represent meaningful application behavior.

---

# 31. Dependency Injection

Constructor injection SHOULD be preferred.

Service locator patterns SHOULD NOT be introduced.

Interfaces SHOULD represent meaningful architectural boundaries.

The agent MUST NOT create interfaces solely because Clean Architecture is being used.

An interface SHOULD exist when it provides clear value such as:

- Boundary isolation
- Multiple implementations
- Test substitution
- Infrastructure inversion
- Stable cross-module contract

---

# 32. Testing Strategy

Every architectural layer SHOULD be independently testable.

## Domain

Prefer:

- Pure unit tests
- No Android dependency
- Business-rule tests
- Use-case tests

## ViewModel

Prefer:

- Coroutine tests
- Flow tests
- Fake repositories
- State transition tests
- Effect tests

## Data

Prefer:

- Repository tests
- Mapper tests
- Data-source tests
- Persistence integration tests where relevant

## UI

Prefer:

- Compose UI tests for critical behavior
- Navigation behavior tests where valuable

Fakes SHOULD be preferred over mocks when behavior and state are meaningful.

Mocks MAY be used when interaction verification provides clear value.

---

# 33. Naming

Names MUST express intent.

Good examples:

```text
ObserveAccountBalance
SubmitPayment
DefaultTransactionRepository
TransactionRemoteDataSource
PaymentUiState
DynamicFeatureInstaller
```

Avoid generic names such as:

```text
Manager
Helper
Utils
Processor
Handler
```

unless the responsibility genuinely matches the name.

Class names SHOULD communicate ownership and behavior.

---

# 34. Scalability Rules

The architecture MUST optimize for independent feature evolution.

Prefer:

- Local feature ownership over global shared abstractions
- Explicit contracts over implicit coupling
- Composition over inheritance
- Small cohesive modules over generic global modules
- Minimal public APIs
- Stable dependency direction
- `implementation` over `api`
- Feature-specific logic inside the owning feature

Avoid:

- Speculative abstractions
- Premature generalization
- Shared modules without ownership
- Bidirectional dependencies
- Feature implementation leakage
- Architecture driven only by anticipated future requirements

A reusable abstraction SHOULD normally emerge after multiple concrete use cases demonstrate the same stable concept.

---

# 35. New Feature Decision Rules

Before creating a new feature, determine:

```text
Feature:
<name>

Business capability:
<description>

Delivery:
Base installation | Dynamic / on-demand

Public API:
<contracts exposed to other modules>

Domain:
<business capabilities>

Data:
<data sources>

Presentation:
<screens>

Navigation:
<entry points>

Dependencies:
<required modules>
```

The agent MUST determine ownership before generating the feature structure.

A new core abstraction MUST NOT be introduced unless it represents a genuinely shared stable capability.

---

# 36. New Module Decision Rules

The agent MUST NOT create a new Gradle module without a clear reason.

Valid reasons MAY include:

- Independent feature ownership
- Dependency isolation
- Build performance
- Dynamic delivery
- Stable public contract isolation
- Reusable cross-cutting infrastructure
- Test infrastructure separation

Invalid reasons include:

- Architectural aesthetics
- One class per module
- Mirroring every Clean Architecture layer as a Gradle module without need
- Hypothetical future reuse

The project SHOULD avoid unnecessary Gradle module explosion.

---

# 37. Refactoring Rules

When refactoring, the agent MUST preserve externally observable behavior unless the task explicitly changes behavior.

Before extracting shared code, the agent MUST verify that the abstraction is genuinely shared.

Before moving code between modules, the agent MUST verify:

- Ownership
- Dependency direction
- Public API impact
- Build impact
- Testing impact
- Dynamic feature delivery constraints

Refactoring MUST NOT weaken existing architectural boundaries.

---

# 38. Code Generation Rules

When generating code, the agent MUST:

1. Identify the owning feature.
2. Identify the architectural layer.
3. Verify dependency direction.
4. Reuse existing contracts.
5. Avoid cross-feature implementation dependencies.
6. Add or update relevant tests.
7. Keep implementation private whenever possible.
8. Keep feature code out of `app`.
9. Keep feature-specific code out of `core`.
10. Prefer the simplest architecture-safe implementation.
11. Avoid speculative abstractions.
12. Respect existing naming and package conventions.
13. Preserve Dynamic Feature delivery boundaries.

---

# 39. App Module

The `app` module is the composition root.

It MAY contain:

- Application initialization
- Dependency graph composition
- Root navigation integration
- Dynamic feature registration
- Global application configuration

The `app` module SHOULD remain thin.

Business logic MUST NOT live in `app`.

Feature implementation details SHOULD NOT be centralized in `app`.

---

# 40. Architectural Decision Priority

When multiple implementations are valid, prioritize in this order:

1. Correctness
2. Architectural boundaries
3. Maintainability
4. Simplicity
5. Testability
6. Performance
7. Reusability

Minor implementation convenience MUST NOT override architectural boundaries.

---

# 41. Definition of Done

Before considering a change complete, the agent MUST verify the following.

## Architecture

- The correct feature owns the code.
- The correct architectural layer owns the responsibility.
- Dependency direction remains valid.
- No new circular dependency exists.
- No feature implementation leaks across boundaries.
- No unnecessary core abstraction was introduced.
- Dynamic Feature boundaries remain valid.

## Implementation

- Existing conventions were followed.
- Public API surface remains minimal.
- Immutable state is exposed.
- Error handling respects layer boundaries.
- Coroutine lifecycle is structured.
- No unmanaged `CoroutineScope` was introduced.
- `GlobalScope` is not used.
- Feature-specific behavior remains inside the owning feature.

## Quality

- Business logic has relevant tests.
- Existing relevant tests still pass.
- New behavior is tested where appropriate.
- Edge cases were considered.
- Accessibility was verified for affected user-facing behavior.
- State restoration and configuration changes were considered.
- Security and privacy implications were reviewed.
- Performance-sensitive changes were measured when appropriate.
- Logs, analytics, and diagnostics contain no secrets or unnecessary personal data.
- No unnecessary duplication was introduced.
- No speculative abstraction was introduced.

## Build

Run the smallest relevant validation first.

Examples:

```bash
./gradlew :feature:<feature>:test
```

Then, when appropriate:

```bash
./gradlew test
```

For architectural, Gradle, or module changes:

```bash
./gradlew build
```

The exact validation command MAY vary depending on the affected modules.

The agent MUST NOT claim completion when relevant compilation or tests are failing.

If validation cannot be executed, the agent MUST explicitly state what was not validated.

---

# 42. Conflict Resolution

When a requested implementation violates these rules, the agent MUST:

1. Identify the architectural conflict.
2. Preserve the architectural boundary.
3. Propose an architecture-safe alternative.
4. Implement the safe alternative when possible.
5. Avoid silently introducing technical debt.

When ownership is uncertain, the agent SHOULD prefer keeping logic inside the feature rather than prematurely moving it into `core`.

---

# 43. Platform Compatibility

The project MUST define and maintain an explicit compatibility policy for:

- `minSdk`
- `targetSdk`
- Android Gradle Plugin
- Kotlin
- Java toolchain
- Compose compiler and runtime

The application MUST be validated on the minimum supported API level and the current target API level when platform behavior is affected.

Platform-dependent behavior MUST be handled explicitly.

The agent MUST account for, when relevant:

- Edge-to-edge rendering
- Predictive back navigation
- Activity recreation
- Process death
- Saved state restoration
- Runtime permission changes
- Background execution limits
- Notification permission and channel behavior
- Deep-link verification

Deprecated APIs MUST NOT be introduced when a supported replacement exists.

Compatibility workarounds MUST be narrowly scoped and documented with the affected API levels and removal condition.

---

# 44. Adaptive Layouts and Input

The UI MUST NOT assume a fixed screen size, orientation, aspect ratio, or window mode.

Layouts SHOULD adapt using actual window information and supported adaptive layout APIs.

Device classification based only on resource names or checks such as `isTablet` SHOULD NOT be used when window size is the relevant constraint.

User-facing features MUST preserve usable state across:

- Window resizing
- Rotation
- Fold and unfold transitions
- Multi-window mode
- Desktop windowing

Orientation MUST NOT be locked without a documented product or technical requirement.

Interactive workflows SHOULD support, where applicable:

- Touch
- Keyboard navigation
- Hardware keyboard input
- Mouse or trackpad input
- Focus traversal

---

# 45. Accessibility

Accessibility is part of functional correctness.

Interactive UI MUST provide appropriate semantics, labels, roles, states, and actions.

The UI MUST:

- Preserve a logical focus and traversal order
- Support font and display scaling without losing essential content or actions
- Meet applicable contrast and touch-target requirements
- Avoid communicating meaning through color alone
- Provide meaningful descriptions for non-decorative visual content
- Avoid redundant descriptions for decorative content
- Respect reduced-motion preferences where animations are nonessential

Custom components MUST expose behavior equivalent to the platform component they replace.

Critical user journeys SHOULD be verified with TalkBack and keyboard or switch-style navigation.

Known critical accessibility regressions MUST block release.

---

# 46. Security and Privacy

Security and privacy MUST be considered during design, implementation, review, and release.

Secrets, access tokens, credentials, private keys, and sensitive personal data MUST NOT be committed to source control or written to logs.

Sensitive data MUST NOT be persisted in plaintext when platform-protected storage or server-side storage is appropriate.

The application MUST:

- Request only permissions required by current functionality
- Request runtime permissions in user context
- Validate exported components and intent inputs
- Use explicit and immutable `PendingIntent` behavior where applicable
- Validate deep links and untrusted navigation arguments
- Restrict WebView capabilities to the minimum required surface
- Use encrypted network transport
- Treat client-side validation as insufficient for authorization

Authentication SHOULD use current platform-supported credential APIs when applicable.

Integrity signals MAY inform risk decisions but MUST NOT replace server-side authorization or abuse controls.

Collection, storage, sharing, retention, and deletion of user data MUST have explicit ownership.

Third-party SDK data access MUST be reviewed and represented accurately in release privacy declarations.

---

# 47. Resilience and Offline Behavior

Every persisted or synchronized data type SHOULD have an explicit source-of-truth policy.

Features that operate with remote data MUST define relevant behavior for:

- No connectivity
- Slow or intermittent connectivity
- Stale cached data
- Partial responses
- Authentication expiration
- Retry exhaustion
- Concurrent updates
- Conflict resolution

Persistent deferrable work SHOULD use WorkManager or another lifecycle-safe platform mechanism appropriate to the requirement.

Retries MUST be bounded, cancellation-aware, and use an appropriate backoff policy.

Operations that may be retried SHOULD be idempotent or protected against duplicate effects.

Coroutine cancellation MUST NOT be converted into a business failure.

Recoverable and non-recoverable errors MUST remain distinguishable at the layer responsible for recovery behavior.

---

# 48. Performance and Resource Budgets

Performance MUST be measured for user-critical paths rather than inferred from implementation appearance.

The project SHOULD define measurable budgets for:

- Time to initial display
- Time to full display
- Frame timing and jank
- ANR rate
- Memory usage
- Application size
- Network usage
- Battery-sensitive background work

Critical startup and navigation paths SHOULD be covered by Baseline Profiles and Macrobenchmarks.

Benchmarks MUST use a release-like, non-debuggable build and SHOULD run on representative physical devices.

Main-thread disk, network, serialization, and computationally expensive work MUST be avoided.

Compose performance changes SHOULD be guided by compiler reports, recomposition diagnostics, benchmarks, or traces.

`@Stable` and `@Immutable` MUST NOT be used unless their behavioral contracts are actually satisfied.

Performance optimizations MUST preserve correctness, readability, and architectural boundaries.

---

# 49. Advanced Testing Strategy

Testing depth MUST be proportional to user impact, architectural risk, and regression cost.

In addition to layer-specific tests, the agent SHOULD consider:

- Contract tests between public APIs and implementations
- Database migration tests
- Serialization compatibility tests
- Saved-state and process-death tests
- Screenshot tests for stable visual contracts
- Instrumented tests for critical user journeys
- Accessibility tests
- Macrobenchmarks for critical performance paths

Tests MUST be deterministic and isolated from uncontrolled external state.

Flaky tests MUST NOT be ignored indefinitely.

A quarantined test MUST have a documented owner, reason, tracking issue, and removal condition.

Test doubles MUST model only behavior required by the test and MUST NOT reproduce implementation internals unnecessarily.

---

# 50. Observability and Analytics

Production behavior MUST be diagnosable without exposing sensitive information.

Logs SHOULD be structured, actionable, and assigned an appropriate severity.

Logs, crash reports, traces, and analytics MUST NOT contain secrets, credentials, access tokens, or unnecessary personal data.

Failures SHOULD retain sufficient cause and context for diagnosis across layer mappings.

Critical features SHOULD define operational signals for success, failure, latency, and abandonment where product requirements justify them.

Analytics events SHOULD be typed, documented, consistently named, and owned by the feature that emits them.

Analytics event names and property keys SHOULD NOT be distributed as arbitrary string literals throughout UI code.

Feature flags and remote configuration MUST define:

- A safe default
- Ownership
- Failure behavior
- Expiration or cleanup criteria

---

# 51. Dependency Governance

Every new third-party dependency MUST have a clear benefit that exceeds its maintenance, security, build-time, binary-size, and licensing cost.

Before adding a dependency, the agent MUST check whether the platform, Kotlin, Jetpack, or an existing project dependency already provides the required capability.

Dependency versions MUST remain centralized.

Dynamic versions such as `+` MUST NOT be used.

Dependencies SHOULD be monitored for vulnerabilities, abandonment, incompatible licenses, and deprecated APIs.

Dependency locking or artifact verification SHOULD be enabled when justified by the project's supply-chain requirements.

A wrapper or adapter SHOULD only be introduced when it protects a meaningful architectural boundary or provides required test substitution.

---

# 52. Public API and Module Compatibility

Declarations SHOULD be `internal` by default unless cross-module access is intentional.

Public APIs MUST be minimal, cohesive, documented, and stable enough for their consumers.

Public contracts MUST NOT expose:

- Mutable implementation state
- Dependency injection implementation types
- Infrastructure models
- Unmanaged `CoroutineScope` instances
- Mutable collections
- Implementation-specific exceptions

Asynchronous contracts MUST make ownership of cancellation, timeout, threading, and lifecycle clear.

Modules with externally consumed or long-lived public APIs SHOULD use automated API compatibility checks.

Breaking API changes MUST be explicit and coordinated with all consumers.

---

# 53. Continuous Integration and Release Quality

Continuous integration MUST provide the fastest relevant feedback first and progressively run broader validation.

Depending on change scope, CI SHOULD validate:

- Formatting
- Static analysis
- Android and Compose lint
- Unit tests
- Architecture and dependency rules
- Release compilation with shrinking enabled
- Instrumented tests for affected critical paths
- Database migrations
- Baseline Profile generation or validation
- Security and dependency checks
- Accessibility checks

Release builds MUST be validated independently from debug builds when code shrinking, resource shrinking, signing, or build variants can affect behavior.

Build warnings MUST be reviewed and MUST NOT be suppressed globally without a documented reason.

The project SHOULD preserve reproducible builds and use Gradle performance features such as configuration cache when compatible and measured.

---

# 54. Technical Debt and Rule Exceptions

Exceptions to this document MUST be explicit.

An exception MUST document:

1. The rule being violated.
2. The product or technical reason.
3. The accepted risk.
4. The responsible owner.
5. The review date or removal condition.
6. The tracking issue when remediation is required.

Temporary workarounds MUST NOT silently become permanent architecture.

TODO comments MUST describe an actionable condition and SHOULD reference a tracking issue.

The agent MUST NOT use an exception to bypass a boundary when an architecture-safe implementation is reasonably available.

---

# 55. Final Principle

The architecture of **dfm-agent** must remain understandable as the project grows.

Every new abstraction, dependency, module, and shared component must justify its existence.

Prefer explicit ownership.

Prefer stable contracts.

Prefer simple implementations.

Prefer scalable boundaries.

Do not trade long-term architectural integrity for short-term convenience.
