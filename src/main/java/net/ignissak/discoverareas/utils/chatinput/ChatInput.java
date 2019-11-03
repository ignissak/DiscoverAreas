package net.ignissak.discoverareas.utils.chatinput;

import net.ignissak.discoverareas.utils.ChatInfo;
import net.ignissak.discoverareas.utils.PlayerRunnable;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class ChatInput {
    private Player p;
    private ChatInputCompleteMethod completeMethod;
    private PlayerRunnable exitMethod;

    private static HashMap<Player, ChatInput> inputPlayers = new HashMap<>();

    public ChatInput(Player p){
        this.p = p;
        inputPlayers.put(p, this);
        this.completeMethod = (pl, m) -> {};
        this.exitMethod = pl -> {
            ChatInfo.info(pl, "Operation cancelled.");
        };
    }

    public void finish(){
        inputPlayers.remove(p);
    }

    public void setChatInputCompleteMethod(ChatInputCompleteMethod method){
        this.completeMethod = method;
    }

    public void setChatInputExitMethod(PlayerRunnable method){
        this.exitMethod = method;
    }

    public ChatInputCompleteMethod getCompleteMethod() {
        return completeMethod;
    }

    public PlayerRunnable getExitMethod() {
        return exitMethod;
    }

    public static boolean isInInputMode(Player p){
        return inputPlayers.containsKey(p);
    }

    public static ChatInput getInput(Player p){
        return inputPlayers.get(p);
    }

    public static boolean isStopMessage(String message){
        return message.equalsIgnoreCase("cancel");
    }
}
