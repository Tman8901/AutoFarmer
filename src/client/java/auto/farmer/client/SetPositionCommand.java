package auto.farmer.client;

import java.util.HashSet;
import java.util.Set;

public class SetPositionCommand {
    private static final Set<String> executedCommands = new HashSet<>();

    public static void executedCommand(String command) {
        if(!executedCommands.contains(command)) {
            executedCommands.add(command);
        } else {

        }
    }
}
