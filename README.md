# Happy Creeper

`Happy Creeper` is a NeoForge mod for Minecraft `1.21.1` that lets you weaken, tame, and fight alongside creepers instead of treating them only as enemies.

The mod is being built for a real `1.21.1` modpack, so the focus is on survival-friendly mechanics rather than one-off demo features.

## Target Platform

- Minecraft: `1.21.1`
- Mod Loader: `NeoForge`
- Java: `21`

## Current Gameplay Loop

1. Wear a `Creeper Head` to approach a creeper safely.
2. Feed it an `Anti-Blast Biscuit` to weaken it.
3. Feed the weakened creeper a `Sweet Gunpowder Biscuit` to tame it.
4. Command your tamed creeper to follow you or stay in place.
5. Heal it with `Gunpowder` or a `Sweet Gunpowder Biscuit`.

## Implemented Features

- Creeper-head protection prevents nearby creepers from behaving normally toward the player
- Damaging a protected creeper makes it retaliate again for a short time
- Persistent creeper state system:
  - `normal`
  - `weakened`
  - `tamed`
- `Anti-Blast Biscuit` interaction for weakening
- `Sweet Gunpowder Biscuit` interaction for taming
- Weakened creepers fail to explode normally and stay alive
- Weakened creepers emit a visible debuff-style particle effect
- Tamed creepers:
  - remember their owner
  - do not target players
  - follow their owner
  - can be told to stay
  - teleport back near the owner if they fall too far behind
  - defend the owner
  - use a manual contact blast / shove attack instead of a normal explosion
  - trigger a larger non-destructive death burst when they die
  - gain bonus max health
- Owner-only healing with:
  - `Gunpowder`
  - `Sweet Gunpowder Biscuit`
- Blue recolor groundwork for tamed creepers has been added

## Items

- `Biscuit`
- `Anti-Blast Biscuit`
- `Sweet Gunpowder Biscuit`

The base `Biscuit` is edible. The two special biscuits are interaction items and are not edible by players.

## Recipes

### Biscuit

Craft `2` biscuits with:

```text
Wheat + Sugar + Wheat
```

Placed horizontally in a crafting grid.

### Sweet Gunpowder Biscuit

Craft with:

```text
Biscuit + Gunpowder
```

### Anti-Blast Biscuit

Craft with:

```text
Biscuit + Milk Bucket
```

The milk bucket should return an empty bucket through normal vanilla crafting remainder behavior.

## Development Setup

### IntelliJ IDEA

1. Open the project root in IntelliJ IDEA
2. Ensure the project uses Java `21`
3. Let Gradle import the NeoForge project
4. Run the generated NeoForge client configuration

### Useful Commands

```powershell
.\gradlew.bat runClient
.\gradlew.bat runServer
.\gradlew.bat build
.\gradlew.bat clean
```

If dependencies get out of sync:

```powershell
.\gradlew.bat --refresh-dependencies
```

### Reload Tips

- Use `F3 + T` for textures, models, language files, and other resource changes
- Use `/reload` for recipe and datapack-style resource changes
- Restart the client for Java code changes

## Project Structure

- [src/main/java/com/damian/happycreeper](/E:/McCode/happy-creeper/src/main/java/com/damian/happycreeper) contains the mod source
- [src/main/resources](/E:/McCode/happy-creeper/src/main/resources) contains assets, recipes, and mod resources
- [build.gradle](/E:/McCode/happy-creeper/build.gradle) contains the NeoForge build configuration
- [gradle.properties](/E:/McCode/happy-creeper/gradle.properties) contains the Minecraft, NeoForge, and mod version settings

## Current Status

The core taming loop is implemented and playable:

- approach with a creeper head
- weaken with an anti-blast biscuit
- tame with a sweet gunpowder biscuit
- command, heal, and fight alongside the creeper

The main work left is polish, balance, visual upgrades, and extending customization such as recolors or future armor ideas.

## Notes

- The project uses Mojang mappings through the NeoForge toolchain
- The mod is still in active development and balance values may change frequently

## License

Current repository setting: `All Rights Reserved`
