# Happy Creeper

`Happy Creeper` is a NeoForge mod for Minecraft `1.21.1` focused on turning creepers from pure danger into creatures you can weaken and eventually tame.

The first gameplay loop is:

1. Wear a creeper head so nearby creepers stop behaving like normal suicide bombers around you.
2. Feed a creeper an `Anti-Blast Biscuit` to weaken it.
3. Once weakened, feed it a `Sweet Gunpowder Biscuit` to tame it.

This repository currently contains the initial mod setup and is being built toward that taming system.

## Target Platform

- Minecraft: `1.21.1`
- Mod Loader: `NeoForge`
- Java: `21`

This project is intentionally starting on `1.21.1` because that is the version used in the modpack this mod is being developed for.

## Planned Features

- Creepers react differently when the player is wearing a creeper head
- `Anti-Blast Biscuit` item for weakening creepers
- Weakened creepers fail to explode normally, or only produce a weak non-lethal blast
- `Sweet Gunpowder Biscuit` item for taming weakened creepers
- Friendly creeper behavior after taming

## Current Design Direction

The current plan is to implement the feature in phases:

1. Replace the starter template code with real mod content
2. Register the biscuit items
3. Add persistent creeper state: normal, weakened, tamed
4. Hook player interaction and explosion behavior
5. Define post-tame behavior

This keeps the mod playable early and reduces the risk of mixing AI work with core state logic too soon.

## Development Setup

### IntelliJ IDEA

1. Open the project root in IntelliJ IDEA
2. Ensure the project is using Java `21`
3. Let Gradle import the NeoForge project
4. Use the generated NeoForge run configurations to start the client

### Useful Commands

```powershell
.\gradlew runClient
.\gradlew runServer
.\gradlew build
.\gradlew clean
```

If dependencies get out of sync:

```powershell
.\gradlew --refresh-dependencies
```

## Project Structure

- [src/main/java/com/damian/happycreeper](/E:/McCode/happy-creeper/src/main/java/com/damian/happycreeper) contains the mod source
- [src/main/resources](/E:/McCode/happy-creeper/src/main/resources) contains assets and mod resources
- [build.gradle](/E:/McCode/happy-creeper/build.gradle) contains the NeoForge build configuration
- [gradle.properties](/E:/McCode/happy-creeper/gradle.properties) contains the Minecraft, NeoForge, and mod version settings

## Status

This is an early-stage project. The repository is set up and ready for feature work, but the actual creeper taming gameplay is still being implemented.

## Notes

- The project currently uses Mojang mappings through the NeoForge toolchain
- The generated template code is still being replaced with mod-specific systems

## License

Current repository setting: `All Rights Reserved`
