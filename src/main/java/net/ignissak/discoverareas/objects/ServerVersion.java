package net.ignissak.discoverareas.objects;

public enum ServerVersion {

    V1_14("1.14"),
    V1_15("1.15"),
    V1_16("1.16"),
    UNSUPPORTED("UNSUPPORTED");

    private String str;

    ServerVersion(String str) {
        this.str = str;
    }
}
