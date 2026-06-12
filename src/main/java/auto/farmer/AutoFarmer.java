package auto.farmer;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoFarmer implements ModInitializer {
	public static final String MOD_ID = "auto-farmer";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Auto Farmer mod initialized");
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("setPosition").executes(context -> {
                try {
                    ServerPlayer player = context.getSource().getPlayerOrException();

                    String pos = player.blockPosition().toShortString();
                    context.getSource().sendSuccess(() -> Component.literal("Position 1: " + pos), false);
                } catch (Exception E) {
                    context.getSource().sendFailure(Component.literal("Command Failed Please fix it bro!"));
                }
                return 1;
            }));
        });
	}
}