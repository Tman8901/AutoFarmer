package auto.farmer;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoFarmer implements ModInitializer {
	public static final String MOD_ID = "auto-farmer";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    ServerPlayer player;

	@Override
	public void onInitialize() {
		LOGGER.info("Auto Farmer mod initialized");
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("setPosition").executes(context -> {
                BlockPos below = player.blockPosition().below();
                BlockState state = player.level().getBlockState(below); // TODO THIS CAUSES COMMAND NOT TO WORK
                if(state.getBlock() == Blocks.FARMLAND) {
                        player = context.getSource().getPlayerOrException();
                        String pos = player.blockPosition().toShortString();

                        context.getSource().sendSuccess(() -> Component.literal("block" + state), true);
                        context.getSource().sendSuccess(() -> Component.literal("Position 1: " + pos), false);
                        return 1;
                } else {
                        context.getSource().sendFailure(Component.literal("Not on Farmland!"));
                        return 0;
                }
            }));
        });
	}
}