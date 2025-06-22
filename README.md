# TTRPG Spellbook app

## Features
- spell list
  - spells can be imported, copied, and edited fully
  - dice roll notations are highlighted
- character creation
  - tracking of class spells, known spells, and prepared spells
  - tracking of prepared spell count limit

## Platforms
Android and Desktop (Windows & Linux) thanks to Compose Multiplatform

## Other
I recommend this project be opened, edited, and run via android studio

## Version Bump Procedure
edit versionString and versionCode in src/build.gradle.kts

## Build
Build distribution:
`.\gradlew packageDistributionForCurrentOS`