# Plank Sack Counter

## Description
A RuneLite plugin that displays the contents of your Plank Sack at a glance using varbits. This is a complete rewrite using the varbit-based approach for tracking plank counts, which is far more reliable than the previous chat message parsing approach.

**Original Author:** Hydrox6
**Refactored Version:** Uses varbits instead of chat messages/inventory tracking

## Last Task Worked On
Complete refactor to use varbits for tracking plank sack contents:

1. **Removed all chat message parsing** - No longer depends on game messages
2. **Removed inventory diffing logic** - No more snapshot/delta calculations
3. **Removed animation tracking** - No Mahogany Homes workarounds needed
4. **Added varbit tracking** - Uses 7 varbits for each plank type:
   - `VarbitID.PLANK_SACK_PLAIN` - Normal planks
   - `VarbitID.PLANK_SACK_OAK` - Oak planks
   - `VarbitID.PLANK_SACK_TEAK` - Teak planks
   - `VarbitID.PLANK_SACK_MAHOGANY` - Mahogany planks
   - `VarbitID.PLANK_SACK_CAMPHOR` - Camphor planks (Sailing)
   - `VarbitID.PLANK_SACK_IRONWOOD` - Ironwood planks (Sailing)
   - `VarbitID.PLANK_SACK_ROSEWOOD` - Rosewood planks (Sailing)

## Benefits of Varbit Approach
- **Future-proof** - Immune to chat message text changes
- **More reliable** - Reads actual game state, not inferred from side effects
- **Simpler code** - Reduced from ~575 lines to ~215 lines
- **Sailing support** - Automatically includes new Sailing plank types
- **Works everywhere** - Construction, Mahogany Homes, Hallowed Sepulchre, Fishing Cranes, Sailing all automatically tracked

## How to Run (Development)
1. Configure RuneLite to save credentials: Start Menu -> "RuneLite (configure)" -> add `--insecure-write-credentials` to Client arguments -> Save
2. Launch RuneLite via Jagex Launcher once to generate credentials
3. Open project in IntelliJ IDEA
4. Run the test launcher: `PlankSackPluginLauncher`
5. Plugin loads automatically with Jagex account credentials

## Future Plans
- Submit as PR if wanted
- Consider adding per-plank-type breakdown display

## Key Files
- `src/main/java/io/hydrox/planksack/PlankSackPlugin.java` - Main plugin logic (varbit-based)
- `src/main/java/io/hydrox/planksack/PlankSackConfig.java` - Configuration
- `src/main/java/io/hydrox/planksack/PlankSackOverlay.java` - Item overlay
- `src/main/java/io/hydrox/planksack/PlankSackCounter.java` - Infobox counter
- `src/test/java/io/hydrox/planksack/PlankSackPluginLauncher.java` - Test launcher

## Key IDs
- Plank Sack Item ID: 24882
