# FocusQuest architecture

## Current foundation

FocusQuest starts with the smallest architecture that supports the next product increment.

- `app` is the composition root and owns application startup and root UI composition.
- `build-logic` owns shared Gradle conventions that are already used by a real module.
- Feature behavior does not belong in `app`.
- Dependencies follow `Presentation -> Domain <- Data` inside each feature.

## First product capability

The first owning feature will be `focus-session`. It will contain the behavior required to configure, run, complete, and record a focus session.

The feature will begin as a normal installed module. Dynamic delivery is not justified for this critical application flow.

## Deferred decisions

The following infrastructure will be introduced only with its first concrete consumer:

- Navigation, when the application has more than one destination.
- Dependency injection, when object composition is no longer trivial.
- Local persistence, when a focus session must be recorded.
- Networking, when a remote product requirement exists.
- Shared core modules, after at least two independent features require the same stable capability.

Generic `BaseViewModel`, `BaseRepository`, and `BaseUseCase` abstractions are not allowed. Shared behavior will use focused contracts and composition after repeated concrete needs are demonstrated.

## Module conventions

Android application and library modules use the convention plugins exposed by `build-logic`. Compose remains opt-in for library modules so Domain and Data code are not coupled to UI tooling.

New Gradle modules require an explicit owner, architectural boundary, and dependency direction before being added to the project.

## Quality gates

Every pull request into `develop` must compile the debug application and pass unit tests and Android lint. Feature changes must add the smallest relevant tests for their business and presentation behavior.
