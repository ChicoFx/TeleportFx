# TeleportFX - Fabric Mod

TeleportFX is a Minecraft Fabric mod that enhances the teleportation experience by adding configurable visual and sound effects to teleport commands. It also provides convenient commands for server administrators and players.

## Features

*   **Player-to-Player Teleport (`/tpr <player>`):** Teleport yourself to another online player.
*   **Coordinate Teleport (`/tpcoord <x> <y> <z>` or `/tpcoord <target> <x> <y> <z>`):** Teleport yourself or another player to specific coordinates. Compatible with Xaero's Minimap waypoint format.
*   **Summon Player (`/tphere <player>`):** Teleport another player to your current location.
*   **List Players (`/tplist`):** List all currently online players with their coordinates.
*   **Visual & Sound Effects:** Rich particle and sound effects for teleportation events (origin and destination).
*   **Extensive Configuration:**
    *   A `config/tpmod.json` file is generated on first run.
    *   Toggle individual effects (portal spiral, origin/destination particles, light beams, sounds).
    *   Customize particle counts, sound volumes/pitches, effect timings.
    *   Set permission levels for each command.
    *   Toggle feedback messages.
*   **In-Game Configuration Reload (`/tpmodconfig reload`):** Reload the configuration file without restarting the server (requires appropriate permission).

## Commands

*   `/tpr <targetPlayerName>`
    *   Teleports you to the specified player.
    *   Permission: Configurable (default: 0 - any player).
*   `/tpcoord <x> <y> <z>`
    *   Teleports you to the specified coordinates.
    *   Permission: Configurable (default: 0 - any player).
*   `/tpcoord <targetPlayer> <x> <y> <z>`
    *   Teleports the specified target player (or yourself using `@s`) to the coordinates.
    *   Permission: Configurable (default: 0 - any player).
*   `/tphere <targetPlayerName>`
    *   Teleports the specified player to your current location.
    *   Permission: Configurable (default: 2 - OP level 2).
*   `/tplist`
    *   Lists all online players and their current coordinates.
    *   Permission: Configurable (default: 0 - any player).
*   `/tpmodconfig reload`
    *   Reloads the `tpmod.json` configuration file from disk.
    *   Permission: Configurable (default: 2 - OP level 2).

## Configuration

The mod generates a configuration file named `tpmod.json` (or `<your_mod_id>.json` if you change it) in the `config` directory of your Minecraft/server instance.

You can customize:
*   **General:** Master switch for all effects.
*   **Particles:** Enable/disable and adjust parameters for portal spirals, origin/destination effects, ground circles.
*   **Sound:** Enable/disable and adjust volume/pitch for main teleport, portal, and whoosh sounds.
*   **LightBeam:** Enable/disable and adjust parameters for the main light beam and simpler effects for `/tpcoord`.
*   **Timing:** Delays for effects.
*   **Permissions:** Minecraft permission levels (0-4) required for each command.
*   **CoordinateValidation:** Min/max Y-level for `/tpcoord`.
*   **Messages:** Toggle feedback messages for command executors and target players.

Use `/tpmodconfig reload` (requires permission) to apply changes without a server restart.

## Installation

1.  Ensure you have [Fabric Loader](https://fabricmc.net/use/) installed.
2.  Download the latest `.jar` file of this mod from the [Releases page](https://github.com/your-username/teleport-fx-fabric-mod/releases) (You'll need to create this page once you have a release).
3.  Place the downloaded `.jar` file into your `mods` folder.
4.  (Optional but Recommended) Install [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api) if not already present, as many Fabric mods depend on it. This mod uses Fabric API for command registration.

## Building from Source

1.  Clone the repository: `git clone https://github.com/your-username/teleport-fx-fabric-mod.git`
2.  Navigate to the project directory: `cd teleport-fx-fabric-mod`
3.  Build the mod: `./gradlew build`
    *   On Windows, use: `gradlew build`
4.  The compiled `.jar` file will be located in `build/libs/`.

## Contributing

Contributions are welcome! Please feel free to submit a pull request or create an issue.

## License

This mod is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Configuration

TeleportFX is highly configurable via the `tpmod.json` file, which is generated in your server/client `config` folder on the first run.

**For a detailed explanation of all configuration options and their effects, please see the [[tpmod.json Configuration Details Wiki Page](link-to-your-wiki-page)](https://github.com/ChicoFx/TeleportFx/wiki/Configuration-File-(tpmod.json)).**

Remember, you can reload the configuration in-game without a server restart by using the `/tpmodconfig reload` command (requires appropriate permission).
