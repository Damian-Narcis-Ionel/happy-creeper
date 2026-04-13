# Happy Creeper

`Happy Creeper` is a NeoForge mod for Minecraft `1.21.1` focused on one idea: turn creepers from a survival threat into a real companion you can weaken, tame, heal, recolor, and bring into combat.

The mod is being developed for actual survival play, so the mechanics are meant to feel usable in a normal world and modpack, not just in a test map.

## Platform

- Minecraft: `1.21.1`
- Loader: `NeoForge`
- Java: `21`

## Core Loop

1. Wear a `Creeper Head` so creepers do not behave normally around you.
2. Feed a creeper an `Anti-Blast Biscuit` to weaken it.
3. Feed the weakened creeper a `Sweet Gunpowder Biscuit` to tame it.
4. Right-click your tamed creeper with an empty main hand to switch between `follow` and `stay`.
5. Heal it with `Gunpowder` or another `Sweet Gunpowder Biscuit`.

## Current Features

- Creeper-head protection:
  - nearby creepers do not aggro and explode normally while you wear a creeper head
  - if you attack a protected creeper, it can retaliate again
- Creeper states:
  - `normal`
  - `weakened`
  - `tamed`
- Weakening:
  - requires wearing a `Creeper Head`
  - uses the `Anti-Blast Biscuit`
  - gives the creeper a continuous weakened particle effect
  - prevents the normal suicide explosion behavior
- Taming:
  - uses the `Sweet Gunpowder Biscuit`
  - stores the owner
  - clears the weakened visual state once tamed
- Companion behavior:
  - follows the owner
  - can be told to stay in place
  - teleports back if left too far behind
  - changes dimension with the owner
  - resyncs to the owner after respawn
- Combat:
  - does not target players by default
  - protects the owner from hostile mobs
  - also attacks mobs the owner is attacking
  - also keeps fighting mobs that retarget onto the creeper itself
  - uses a small contact blast / shove attack instead of a real self-destruct explosion
  - gets extra movement speed while fighting
  - dies with a larger non-destructive blast burst
- Health and healing:
  - tamed creepers have increased max health
  - owners can heal them with `Gunpowder`
  - owners can heal them more with `Sweet Gunpowder Biscuit`
- Visual customization:
  - tamed creepers default to the `happy` green texture
  - recolor options currently include `blue`, `cyan`, `gray`, `yellow`, `pink`, `purple`, `red`, `black`
  - `Lime Dye` restores the default happy green texture
  - `Rainbow Biscuit` enables rainbow mode
  - rainbow mode cycles smoothly through the available colored creeper textures
- Loot:
  - `Rainbow Biscuit` can appear in generated structure chests

## Items

- `Biscuit`
- `Anti-Blast Biscuit`
- `Sweet Gunpowder Biscuit`
- `Rainbow Biscuit`

`Biscuit` is edible.

`Anti-Blast Biscuit`, `Sweet Gunpowder Biscuit`, and `Rainbow Biscuit` are interaction items and are not edible by players.

## Recipes

### Biscuit

Craft `2` biscuits from:

```text
Wheat + Sugar + Wheat
```

Place the ingredients horizontally in a crafting grid.

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

The milk bucket returns an empty bucket after crafting.

### Rainbow Biscuit

There is currently no crafting recipe.

It is found as loot in generated chests.

## Development

### IntelliJ IDEA

1. Open the project root in IntelliJ IDEA.
2. Make sure the project uses Java `21`.
3. Let Gradle import the NeoForge project.
4. Run the generated NeoForge client configuration.

### Commands

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

- Use `F3 + T` for textures, models, language files, and other client assets.
- Use `/reload` for recipes and loot-table style datapack resources.
- Restart the game for Java code changes.

## Project Structure

- [src/main/java/com/damian/happycreeper](/E:/McCode/happy-creeper/src/main/java/com/damian/happycreeper) contains the mod source
- [src/main/resources](/E:/McCode/happy-creeper/src/main/resources) contains assets, loot, recipes, and mod resources
- [src/main/templates/META-INF/neoforge.mods.toml](/E:/McCode/happy-creeper/src/main/templates/META-INF/neoforge.mods.toml) contains mod metadata
- [build.gradle](/E:/McCode/happy-creeper/build.gradle) contains the NeoForge build configuration
- [gradle.properties](/E:/McCode/happy-creeper/gradle.properties) contains version and mod properties

## Release Status

The current build is suitable for public alpha playtesting.

The main gameplay loop is in place, but balance, visuals, and quality-of-life details are still being tuned.

## License

Current repository setting: `All Rights Reserved`
