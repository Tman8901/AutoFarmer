package auto.farmer.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;


public class AutoFarmerClient implements ClientModInitializer {
        public static final String MOD_ID = "auto-farmer";
        public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
        LocalPlayer player;

        String playerPos;

        String pos1;

        String pos2;

        public void onInitializeClient() {
            LOGGER.info("Auto Farmer mod initialized");
            CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
                dispatcher.register(Commands.literal("setPosition").executes(context -> {

                    player = Minecraft.getInstance().player;
                    if(player == null) {
                        context.getSource().sendFailure(Component.literal("player fail ):"));
                    }

                    Level level = player.level();

                    if(level == null) {
                        context.getSource().sendFailure(Component.literal("fail ):"));
                    }


                    // Checks what block is underneath the players feet
                    BlockState state = level.getBlockState(player.blockPosition().below());
                    BlockPos feet = player.blockPosition();
                    BlockState atFeet = level.getBlockState(feet);
                    BlockState below = level.getBlockState(feet.below());

                    if(atFeet.getBlock() == Blocks.FARMLAND || below.getBlock() == Blocks.FARMLAND) {
                        String pos = player.blockPosition().toShortString();
                        if(playerPos == null) {
                            context.getSource().sendSuccess(() -> Component.literal("Position 1: " + pos), false);
                            playerPos = pos;
                            pos1 = playerPos;
                        } else {
                            context.getSource().sendSuccess(() -> Component.literal("Position 2: " + pos), false);
                            pos2 = pos;
                            playerPos = null;
                        }
                        return 1;
                    } else {
                        context.getSource().sendFailure(Component.literal("Not on Farmland!"));
                        return 0;
                    }
                }));
            });

            CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
                dispatcher.register(Commands.literal("start").executes(context -> {
                    context.getSource().sendSuccess(() -> Component.literal(pos1 + " " + pos2), false);
                    if(!Objects.equals(playerPos, pos1)) {
//                        String[] pos1split = pos1.split(",");
                        System.out.println(pos1);
                        player.setDeltaMovement(5, 5, 5);
                    } else {
                        context.getSource().sendSuccess(() -> Component.literal("Starting your sunsi bot"), false);
                    }
                    return 1;
                }));
            });
	}
}