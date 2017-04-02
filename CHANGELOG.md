Change-Log
===============

### [Release 1.0.0](https://github.com/universum-studios/android_universi/releases/tag/1.0.0) ###
> --.04.2017

- First production release.
- Updated `fragments` + `dialogs` dependencies to the latest versions.
- Updated `transitions` dependency to the latest version and incorporated updates for `BaseNavigationalTransition`
  introduced in **[1.0.2](https://github.com/universum-studios/android_transitions/releases/tag/1.0.2)**
  release. Whenever a desired `BaseNavigationalTransition` is attached to `UniversiActivity` or
  `UniversiActivityCompat` via `setNavigationalTransition(...)` method a configuration for incoming
  transitions is performed for that activity via `BaseNavigationalTransition.configureIncomingTransitions(Activity)`.
- `UniversiConfig` class has been removed as it was not used across the library.