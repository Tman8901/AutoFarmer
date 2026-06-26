package auto.farmer.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.awt.event.KeyEvent;

import static auto.farmer.client.StartCommand.parseCoords;
import static java.lang.Math.*;

public class Start {
    static Minecraft minecraft;
    static LocalPlayer player;

    static Robot robot;

    {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    public static void moveToStart(String pos1Str) {
        Vec3 pos1 = parseCoords(pos1Str);

        Vec3 currentPos = player.position();
        if(pos1 == null) { return; }

        System.out.println("You made it this far!");
        if(pos1 != currentPos) {
            while(parseCoords(pos1Str).x != currentPos.x) {
                lookAt(player, new Vec3(pos1.x + 0.5, pos1.y + 0.5, pos1.z + 0.5));
                robot.keyPress(KeyEvent.VK_W);
            }
        }
    }

    public static void lookAt(LocalPlayer player, Vec3 target) {
        double ax = player.getX();
        double ay = player.getY() + player.getEyeHeight();
        double az = player.getZ();

        double dx = target.x - ax;
        double dy = target.y - ay;
        double dz = target.z - az;

        double horiz = sqrt(dx * dx + dz * dz);

        float yaw = (float)toDegrees(atan2(-dx, dz));
        float pitch = (float)-toDegrees(atan2(dy, horiz));

        player.setYRot(yaw);
        player.setXRot(pitch);
    }

}
