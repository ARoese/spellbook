# TTRPG Spellbook app

## Platforms
Android and Desktop (Windows & Linux) thanks to Compose Multiplatform

## Other
I recommend this project be opened, edited, and run via android studio

## Version Bump Procedure
edit versionString and versionCode in src/build.gradle.kts

## Build
Build distribution:
`.\gradlew packageDistributionForCurrentOS`

## Features
- Filterable list of all spells for fast and easy lookup
    - <img src="md-assets/filterable-spell-list.png" alt="filterable spell list" width="200"/>
- Per-character prepared, known, and class spell lists with spell slot tracking
    - <img src="md-assets/character-spell-list.png" alt="prepared, known, and class spell lists" width="200"/>
- View conditions and their effects directly from within spell text
    - <img src="md-assets/conditions-display.png" alt="character conditions in spell text" width="200"/>
- Tracking of sets of prepared spells for quick spell preparation
    - <img src="md-assets/spell-loadouts.png" alt="prepared spell loadouts" width="200"/>
- Duplicate and modify existing spells, or make completely new ones from scratch.
    - <img src="md-assets/spell-editing.png" alt="spell editing" width="200"/>
- Import spells from the 5e SRD api and JSON sources. More sources to come.
    - <img src="md-assets/imports.png" alt="spell import sources" width="200"/>
- Popout spell information on desktop
    - <img src="md-assets/popout.png" alt="spell information popout window" width="200"/>