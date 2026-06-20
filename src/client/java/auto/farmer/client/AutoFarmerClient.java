package auto.farmer.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Objects;


public class AutoFarmerClient implements ClientModInitializer {
        public static final String MOD_ID = "auto-farmer";
        public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
        LocalPlayer player;

        String playerPos;

        String pos1;

        String pos2;

//        File file = new File("run/config/autofarm.txt");


    public AutoFarmerClient() throws FileNotFoundException {
        System.out.println("file not found");
    }

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

//                            out.println(pos1);
//
//                            out.close();
                        } else {
                            context.getSource().sendSuccess(() -> Component.literal("Position 2: " + pos), false);
                            pos2 = pos;
                            playerPos = null;

//                            out.println(pos2);
//
//                            out.close();
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
                        // TODO figure out how to make player move
                        player.getInventory().setSelectedSlot(0);
                        StartCommand.move(pos1, pos2);

                    } else {
                        context.getSource().sendSuccess(() -> Component.literal("Starting your sunsi bot"), false);
                        StartCommand.move(pos1, pos2);
                    }
                    return 1;
                }));
            });
	}
}