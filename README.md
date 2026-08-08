# TTRPG Spellbook app

Free, open-source spell tracking app for android and desktop. Track prepared spells and spellslots for different characters, make and use custom spells, search spells to prepare or learn, and instantly read annotated spell descriptions.

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

## Installation
Installers and distributions are provided for various systems. Download the appropriate release for your system [here](https://github.com/ARoese/spellbook/releases/latest). Submit an issue if an installer doesn't work on your platform.

| Platform              | Installer |
|-----------------------|-----------|
| Windows               | .msi      |
| Android               | .apk      |
| Linux (Ubuntu >22.04) | .deb      |
| Linux (other)         | .tar.gz   |
| Other                 | .jar      |

I do not publish to the Google Play Store. In order to install on android, you need to install the APK manually. Download the APK on your phone, then tap the "download complete" notification to install it. Reference [this tutorial](https://www.lifewire.com/install-apk-on-android-4177185) for further assistance.

## Manual Build
1. Clone this repository
2. install java 21, and java 11
  - The java 11 dependency is for KsonMuli. Ideally, this will be upgraded or removed. However, the current release of that library doesn't work for us.
3. run the gradle wrapper
   - `.\gradlew packageDistributionForCurrentOS`
4. Locate the executable/build result. 
   - Reference `.github/workflows/release.yml` for common paths for various systems

## Other
I recommend this project be opened, edited, and run via android studio

## Version Bump Procedure
edit versionString and versionCode in src/build.gradle.kts