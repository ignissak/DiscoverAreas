package net.ignissak.discoverareas.events.worldguard;

public enum MovementWay {

    MOVE("MOVE", 0),
    TELEPORT("TELEPORT", 1),
    SPAWN("SPAWN", 2),
    DISCONNECT("DISCONNECT", 3),
    WORLD_CHANGE("WORLD_CHANGE", 4),
    RIDE("RIDE", 5);

    private MovementWay(final String s, final int n) {
    }

}
