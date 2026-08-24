# Dependency update policy

Dependency updates are deliberate engineering changes. Automated tools MUST NOT open or merge routine version-update pull requests for this repository.

## Required evaluation

Every update must be prepared in a dedicated branch and include:

1. The reason for the update.
2. Release notes and breaking changes reviewed.
3. Compatibility verified across AGP, Gradle, Kotlin, Compose, JDK, and Android SDK where relevant.
4. Security, licensing, binary-size, and build-time impact considered.
5. The smallest compatible version set selected.
6. A rollback path identified.

## Validation

The update pull request must pass:

- Build-logic validation.
- Unit tests.
- Android lint.
- Debug application compilation.
- Instrumented-test APK compilation.
- Relevant migration, integration, or performance tests when affected.

Multiple unrelated dependency updates SHOULD NOT be combined. Coupled toolchain updates MAY be grouped when they must be evaluated as one compatible set.

Security alerts remain inputs to the evaluation process but MUST NOT bypass review and validation.
