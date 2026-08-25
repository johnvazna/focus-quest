# FocusQuest backlog

This document records known work that has been identified but deliberately not taken yet. It exists so that a deferred decision stays visible instead of becoming silent technical debt.

Each entry states the problem, the evidence, the affected rules in `AGENTS.md`, and the condition that closes it.

Entries are ordered by priority. Priority reflects user impact and architectural risk, not implementation effort.

- **P0**: breaks user-facing behavior. Must be taken before the next release.
- **P1**: architectural boundary weakness. Should be taken before the owning area grows further.
- **P2**: quality or consistency gap with a known owner.
- **P3**: hygiene. May be taken opportunistically.

Taking an entry means opening a dedicated branch and pull request into `develop`, following the quality gates in `docs/architecture.md`.

The product design lives in the Claude Design project **Project Focus Mobile App** (`d96b172f-eb76-4060-93e4-9e638e836f4b`), file `Focus App.dc.html`. Entries below refer to its option identifiers: `3a` is the dashboard before a project exists, `1a` the dashboard with progress, and `1b`–`1e` the Project, Focus, Timeline and Settings screens.

---

## P0-1 — An active focus session is never cleared

**Status:** open
**Owner:** unassigned
**Area:** `feature/focus-session`

### Problem

The active-session row is written but never removed. After the first session reaches `COMPLETED` or `CANCELLED`, the row in `active_focus_session` survives, so every later start attempt is rejected as already active. The user can start exactly one focus session per installation.

### Evidence

- `FocusSessionDao.clearActiveSession` and `FocusSessionDao.getActiveSession` have no production caller. They are referenced only from `FocusSessionDaoTest` and `FocusSessionIntegrationTest`.
- `FocusSessionRepository` exposes only `startIfNoneActive`, so `FocusSessionViewModel` has no contract to report completion or cancellation to Data.
- `FocusSessionEntity.toDomain` is exercised only by `FocusSessionMapperTest`. Nothing reads persisted state at runtime.

### Direction

Extend the Domain repository contract with the terminal transitions the session already models, and call it from the ViewModel when the session completes or is cancelled. The contract must stay business-oriented; no Room type may cross the Data boundary.

Interface segregation applies: prefer focused contract methods over a single generic save operation.

### Definition of done

- Completing a session allows a new session to start.
- Cancelling a session allows a new session to start.
- Unit tests cover both transitions against a fake repository.
- An instrumented test covers the full start, complete, start-again path against the real database.
- `AGENTS.md` §6, §21, §32 remain satisfied.

---

## P0-2 — An interrupted session is lost on process death

**Status:** open
**Owner:** unassigned
**Area:** `feature/focus-session`
**Blocked by:** P0-1

### Problem

The active session lives only in `FocusSessionViewModel`. Persisted state is never read back, so a session in progress is silently lost after process death, and the stale row from P0-1 then blocks recovery.

### Evidence

- `FocusSessionViewModel` holds the session in a private field and initializes `FocusSessionUiState` from defaults.
- `AppContainer` builds the repository but nothing observes or restores prior state.
- `FocusSession.restore` exists in Domain and has no production caller.

### Direction

Decide the source-of-truth policy for an in-progress session before implementing. The persisted session is the natural source of truth; presentation should reflect it rather than own it.

Session recovery must also decide what happens to elapsed wall-clock time while the process was dead. A session that resumes with an unchanged remaining duration is a product decision and must be stated explicitly, not implied by the implementation.

### Definition of done

- The active session survives process death and configuration change.
- The recovery policy for elapsed time is documented in this repository.
- Saved-state behavior has test coverage.
- `AGENTS.md` §43, §47 remain satisfied.

---

## P1-1 — Room types cross the feature boundary into `app`

**Status:** open
**Owner:** unassigned
**Area:** `app`, `feature/focus-session`

### Problem

`app` constructs the database and the repository implementation directly. This forces `FocusQuestDatabase`, `FocusSessionEntity` and `RoomFocusSessionRepository` to be public, so persistence details are part of the feature's public surface.

### Evidence

- `AppContainer` calls `Room.databaseBuilder(context, FocusQuestDatabase::class.java, ...)` and instantiates `RoomFocusSessionRepository`.
- `app/build.gradle.kts` declares `implementation(libs.androidx.room.runtime)` purely to satisfy that call.
- `AGENTS.md` §52 requires declarations to be `internal` by default. §6 forbids the Data layer from exposing infrastructure-specific models outside its boundary.

### Direction

Move object composition for the feature into the feature, following the `impl/di/` package recommended by §12. `app` should request a feature entry point and remain unaware of Room.

This must not become a service locator, and it must not introduce a dependency-injection framework unless a separate decision justifies one. Constructor injection stays the default.

### Definition of done

- No Room type is referenced from `app`.
- `app` no longer declares a Room dependency.
- Feature persistence classes are `internal`.
- The composition root stays thin, per §39.

---

## P1-2 — `app` navigation depends on feature presentation internals

**Status:** open
**Owner:** unassigned
**Area:** `app`, `feature/focus-session`, `feature/dashboard`

### Problem

`FocusQuestNavHost` and `FocusQuestApp` import concrete screens, view models, and effect types from feature implementation modules. No feature exposes an `api` module, so every navigation change reaches into implementation detail.

The current cost is low because there are two destinations and one owner. The cost grows with every destination added, and the `navigation_timeline` and `navigation_settings` entries are already declared as future destinations.

### Evidence

- `FocusQuestNavHost` imports `DashboardRoute`, `FocusSessionRoute`, and `FocusSessionUiEffect` from `:impl` modules.
- `FocusQuestApp` maps `FocusSessionUiEffect` values to application-level snackbar strings, so feature effect semantics are owned by `app`.
- `settings.gradle.kts` includes only `:impl` modules for both features.

### Direction

Introduce `feature/<name>/api` only when a second consumer or a third destination makes the contract real. §36 forbids creating a module for architectural aesthetics alone, so this entry stays open until that trigger occurs.

Until then, the smaller correction is to stop translating feature effects inside `app`: a feature owns the meaning and the copy of its own messages.

### Definition of done, when taken

- `app` navigates through stable contracts rather than concrete screens.
- Feature effect strings are owned by the emitting feature.
- No bidirectional or circular module dependency is introduced.

---

## P1-3 — The dashboard's only action has no destination

**Status:** open
**Owner:** unassigned
**Area:** `app`, `feature/dashboard`

### Problem

`3a` offers exactly one action, "Create a project", and it is the whole point of the screen: without it the user cannot leave the empty state. The flow it should open does not exist, so `app` passes an empty callback and the button does nothing when pressed.

This is a dead control in a shipping surface. It is recorded at P1 rather than P0 only because no released build depends on it yet.

### Evidence

- `FocusQuestNavHost` wires `DashboardRoute(onCreateProject = {})` with a TODO referring to this entry.
- The source design's own next step for this turn is "añade el flujo de crear proyecto (3 pasos)" — the flow is not designed yet either.

### Direction

The project-creation flow must be designed before it is built; inventing screens and copy for it here would put product decisions in the implementation. Until then, the choice is between a disabled control and a control that opens nothing, and neither should be settled silently.

When the flow exists, it belongs to whichever feature owns the project itself, and the dashboard should navigate to it through a contract rather than reaching into it.

### Definition of done

- Pressing "Create a project" opens the creation flow.
- Completing the flow moves the dashboard out of its empty state.
- Cancelling the flow returns to the dashboard with nothing created.

---

## P2-1 — Visual tokens are duplicated outside the design system

**Status:** closed
**Area:** `core/designsystem`, `app`, `feature/dashboard`

The private palettes in `FocusQuestNavHost` and `DashboardScreen` are gone. `core:designsystem` now owns the product's ink and paper, exposed through the Material colour scheme, and both call sites read from `MaterialTheme.colorScheme`. Alpha variations stay at the call site, where they describe a specific use rather than a shared value.

Terracotta is defined by the source design as punctuation and is used by no implemented screen, so it is not declared yet. It arrives with the first screen that needs it.

## P2-2 — `feature/dashboard` has no unit test coverage

**Status:** closed
**Area:** `feature/dashboard`

The module declares a unit-test source set. The dot-field geometry is a pure Kotlin function covered by `ProgressFieldTest`: ring division, dot distribution, how progress paints a ring, and the entrance stagger.

The invariants previously enforced by `DashboardUiState` went away with the type itself — see P2-3.

## P2-3 — The dashboard has no data-backed state

**Status:** open
**Owner:** unassigned
**Area:** `feature/dashboard`

### Problem

Only the empty state (`3a`) is implemented. It renders no data by design, so `DashboardScreen` takes no state and the feature has no Domain or Data layer. The dashboard with real progress is `1a` in the source design and does not exist in the application.

### Evidence

- `DashboardScreen(onCreateProject)` has no state parameter and no view model.
- `DashboardUiState` was deleted when `3a` replaced the earlier placeholder screen: every field it carried — progress, task counts, current week, days remaining, next deadline — is absent from the design's empty state.
- `progressFieldDots` already accepts a progress value and paints a ring proportionally. The empty state passes zero. Nothing else calls it.

### Direction

Introduce the dashboard state, view model, Domain capability and data source together when `1a` is implemented. `1a` derives its percentage and counts from the project's task list, which is also the `1b` Project screen's data — so ownership of that list must be settled before either screen is built, not after.

Do not reintroduce a state type ahead of that work.

### Definition of done, when taken

- The percentage, task counts and painted ring derive from real project data.
- Ownership of the task list is settled and expressed as a contract.
- The empty and populated dashboards are one screen selecting on whether a project exists.

## P2-4 — The design's typefaces are not bundled

**Status:** open
**Owner:** unassigned
**Area:** `core/designsystem`

### Problem

The source design sets display text in Spectral and everything else in Space Grotesk. Neither font is in the application, so `core:designsystem` maps both roles onto the platform families. The type scale, weights, line heights and tracking are the design's; the letterforms are not.

### Evidence

- `Type.kt` declares `FontFamily.Serif` and `FontFamily.SansSerif` and documents the substitution.
- The design project ships both faces as `woff2`, which Android cannot load — Compose needs TTF or OTF.

### Direction

Decide whether the product ships the real faces. Bundling them is a dependency decision under `AGENTS.md` §51: licensing, binary size and the choice between packaged font resources and downloadable fonts all have to be settled, and downloadable fonts add a runtime dependency and a first-render fallback.

Substituting platform families is a defensible answer as long as it is a decision rather than an accident. This entry exists so it is a decision.

### Definition of done

- The typeface question is answered either way and recorded.
- If bundled, licences are documented and the binary-size cost is measured.
- If not bundled, the substitution stays documented at the point of use.

---

## P3-1 — Design QA evidence has no defined home

**Status:** decided, partially applied
**Owner:** unassigned
**Area:** repository hygiene, `core/designsystem`, `feature/dashboard`

### Problem

An earlier design QA pass left `design-qa.md`, `dashboard-implementation.png` and `dashboard-comparison.png` untracked in the repository root, with source-of-truth links pointing at absolute paths on one developer machine. Those files have been deleted.

Design QA produces three kinds of output with three different lifetimes, and only one of them belongs in the repository.

### Decision

**Point-in-time evidence** — comparison screenshots, emulator captures, the pass or fail verdict — describes one change at one moment. It belongs in the pull request that produced it. It goes stale the moment the UI changes and nothing in the build can detect that it has.

**The visual contract** — colours, spacing, radii, typography — is durable and belongs in `core/designsystem` as code, not as prose. A token that only exists in a markdown description cannot be enforced.

**Regression protection** belongs in screenshot tests owned by the feature under test, per `AGENTS.md` §49. A screenshot test fails in CI when fidelity drifts; a screenshot in a document does not.

No `docs/design/` directory will be created and reference images will not be checked in. Both preserve the weakest property of the previous approach: unenforceable, silently stale visual truth.

### Applied so far

The `3a` implementation followed the first two parts: its colour and type values went into `core:designsystem`, and no QA artifact was written to the repository. The third part is outstanding.

### Definition of done

- Screenshot test infrastructure exists and covers `3a`, so the layout is defended by the build rather than by a screenshot in a document.
- No QA artifact is written to the repository root.
- No documentation in the repository depends on a local absolute path.

## P3-2 — Template theme values remain from project generation

**Status:** closed
**Area:** `core/designsystem`, `app`

The generated purple and pink palette is gone. `core:designsystem` declares the product's ink and paper, the type scale carries the design's sizes, weights, line heights and tracking, and `themes.xml` paints the window in the product's paper so nothing flashes before Compose draws.

`FocusQuestTheme` no longer takes `darkTheme` or `dynamicColor`. The source design defines a single light treatment, and dynamic colour would replace the product palette with the device's. A dark treatment becomes a theme parameter when the design defines one; until then the application declares light system bars explicitly rather than inheriting the system preference. This is recorded because it is a deliberate narrowing of behaviour, not an oversight.
