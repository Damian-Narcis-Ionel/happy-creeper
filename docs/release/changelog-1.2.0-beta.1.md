# Happy Creeper 1.2.0-beta.1

Third public release and a feature expansion beta build.

## Added

- Dedicated companion UI for tamed creepers
- 3D creeper preview in the companion UI
- Helmet and chestplate management directly in the UI
- Fuel slot in the companion UI
- Active status effect icons in the companion UI
- UI title uses the creeper's custom name when named
- Persistent fuel storage on tamed creepers
- Automatic self-healing from stored fuel when below 50% health
- Stored `Gunpowder` fuel heals `8 HP`
- Stored `Sweet Gunpowder Biscuit` fuel heals `12 HP`
- Sweet Gunpowder Biscuits can now be stored as fuel
- Happy Creeper Mask support is now reflected in the main docs and release text

## Changed

- Empty-hand right-click now opens the companion UI
- `Shift` + empty-hand right-click now toggles follow/stay in the world
- Armor management is now centered in the companion UI instead of ad hoc world interactions
- Health display in the UI now uses a heart icon instead of a text label
- Companion UI now reflects the current follow/stay state directly
- Fuel slot layout was moved to the left side of the panel to avoid button overlap

## Fixed

- Fuel slot interaction no longer causes the UI health display to fall back to stale `20 HP` max health values
- Reusing the same dye on a creeper that already has that color no longer consumes the dye
- Reusing a Rainbow Biscuit on an already-rainbow creeper no longer wastes the item
- Companion UI title now falls back cleanly to `Happy Creeper` when no custom name exists

## Notes

- This is a beta build intended for broader playtesting, not a final release
- Existing 1.21.1 worlds should continue to work
- The companion UI is now the primary place for armor and fuel management
