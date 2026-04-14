# Happy Creeper 1.1.0-alpha.1

Second public alpha release.

## Added

- Tamed creepers can now wear helmets
- Tamed creepers can now wear chestplates
- Visible armor rendering for equipped helmets and chestplates
- Material-based armor scaling for creeper equipment
- Extra armor scaling for tamed creepers so equipped armor pieces are more impactful
- Direct potion feeding for normal drinkable potions

## Changed

- Direct potion feeding is limited to normal potions only
- Splash and lingering potions are now handled through normal vanilla area effects instead of direct feeding
- Armor management now supports both helmet and chestplate swapping and removal
- Biscuit crafting now uses the new cross-shaped wheat recipe with sugar in the center

## Fixed

- Lead interaction no longer conflicts with follow/stay toggling
- Leashing tamed creepers now works correctly instead of snapping immediately
- Removed old template logging and unused starter config leftovers

## Notes

- This is still an alpha build intended for public playtesting
- Existing worlds should continue to work
- Old armor normalization modifiers from previous test builds are cleaned up automatically
