package net.ignissak.discoverareas.utils.chatinput;

public enum ChatInputType {

    EDIT_SOUND("EDIT_SOUND"),
    EDIT_COMMAND("EDIT_COMMAND"),
    EDIT_DESCRIPTION("EDIT_DESCRIPTION"),
    EDIT_EXP("EDIT_EXP"),
    ADD_COMMAND("ADD_COMMAND");

    private String name;

    ChatInputType(String name) {

    }

    public String getName() {
        return name;
    }
}
