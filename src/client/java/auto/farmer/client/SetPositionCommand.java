package auto.farmer.client;

import net.minecraft.network.chat.Component;

import java.util.HashSet;
import java.util.Set;

public class SetPositionCommand {
    private Set<String> executedCommands = new HashSet<>();

    public void executedCommand(String command) {
        if(!executedCommands.contains(command)) {
            executedCommands.add(command);
        } else {
            // TODO figure out how to run the command again
        }
    }
}
