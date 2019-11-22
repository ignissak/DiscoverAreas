package net.ignissak.discoverareas.objects;

public enum ServerVersion {

    V1_9("1.9"),
    V1_10("1.10"),
    V1_11("1.11"),
    V1_12("1.12"),
    V1_13("1.13"),
    V1_14("1.14"),
    UNSUPPORTED("UNSUPPORTED");

    private String str;

    ServerVersion(String str) {
        this.str = str;
    }
}
