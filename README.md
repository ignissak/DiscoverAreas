# DiscoverAreas
DiscoverAreas is a plugin that allows Minecraft server admins to set up simple discoverable places like in MMORPG games.
These areas can be discovered by players and can get a custom amount of experience etc.

## Migrating from v1 to v2
### Areas
Areas will try to automatically migrate from config.yml to areas.yml. Every area will be logged in console. If this process succeeds, areas key will be deleted from config.yml completely. Every migrated area will get assigned an unique ID.

## How to install?
Head over to [Spigot page](https://www.spigotmc.org/resources/discoverareas-1-14.72410/) and download the latest version of plugin.  
Plugin supports Spigot servers with a **version later than 1.14**, older versions are not and will not be supported.

## Compiling plugin
Building this plugin is easy, just run terminal command `./gradlew shadowJar`. Compiled .jar will be located in `build/libs`.