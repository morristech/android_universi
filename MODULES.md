Modules
===============

Library is also distributed via **separate modules** which may be downloaded as standalone parts of
the library in order to decrease dependencies count in Android projects, so only dependencies really
needed in an Android project are included. **However** some modules may depend on another modules
from this library or on modules from other libraries.

## Download ##

### Gradle ###

For **successful resolving** of artifacts for separate modules via **Gradle** add the following snippet
into **build.gradle** script of your desired Android project and use `implementation '...'` declaration
as usually.

    repositories {
        maven {
            url  "http://dl.bintray.com/universum-studios/android"
        }
    }

## Available modules ##
> Following modules are available in the [latest](https://github.com/universum-studios/android_universi/releases "Releases page") stable release.

- **[Core](https://github.com/universum-studios/android_universi/tree/master/library-core)**
- **[Activity](https://github.com/universum-studios/android_universi/tree/master/library-activity)**
- **[Fragment](https://github.com/universum-studios/android_universi/tree/master/library-fragment)**
