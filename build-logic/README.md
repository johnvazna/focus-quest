# FocusQuest build conventions

The included build exposes only conventions required by current or immediately planned modules.

## Plugins

- `focusquest.android.application`: Android application with Compose, Java 17, SDK defaults, and strict lint.
- `focusquest.android.library`: Android library with Java 17, SDK defaults, and strict lint.
- `focusquest.android.library.compose`: Android library convention plus Compose support.

Application-specific values such as namespace, application ID, target SDK, and version remain explicit in the consuming module.

Dependencies remain explicit in each module so its architectural boundary is visible from its build file.
