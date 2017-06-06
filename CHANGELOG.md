Change-Log
===============

### [Release 1.0.3](https://github.com/universum-studios/android_universi/releases/tag/support-1.0.3) ###
> 06.06.2017

- Small patches and code quality improvements.

### [Release 1.0.2](https://github.com/universum-studios/android_universi/releases/tag/support-1.0.2) ###
> 14.05.2017

- Updated [Fragments](https://github.com/universum-studios/android_fragments) library dependency to
  the **1.2.0** version.
- Updated [Transitions](https://github.com/universum-studios/android_transitions) library dependency
  to the **1.1.0** version.

### [Release 1.0.1](https://github.com/universum-studios/android_universi/releases/tag/support-1.0.1) ###
> 12.04.2017

- Fixed [Issue #14](https://github.com/universum-studios/android_universi/issues/14).

### [Release 1.0.0](https://github.com/universum-studios/android_universi/releases/tag/support-1.0.0) ###
> 02.04.2017

- First production release.
- Updated `fragments` + `dialogs` dependencies to the latest versions.
- Updated `transitions` dependency to the latest version and incorporated updates for `BaseNavigationalTransition`
  introduced in **[1.0.2](https://github.com/universum-studios/android_transitions/releases/tag/1.0.2)**
  release. Whenever a desired `BaseNavigationalTransition` is attached to `UniversiActivity` or
  `UniversiActivityCompat` via `setNavigationalTransition(...)` method a configuration for incoming
  transitions is performed for that activity via `BaseNavigationalTransition.configureIncomingTransitions(Activity)`.
- `UniversiConfig` class has been removed as it was not used across the library.