package auto.farmer.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;

public class AutoFarmerClient implements ClientModInitializer {
    public static LocalPlayer p;
    public static BlockPos pos;
	@Override
	public void onInitializeClient() {
        Minecraft mc = Minecraft.getInstance();
        mc.execute(() -> {
            LocalPlayer player = mc.player;
            if(player != null) {
                player = p;
                pos = player.getOnPos();
            }
        });
	}
}