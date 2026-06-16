package auto.farmer.client;


import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class StartCommand {
    private static final double STEP_LEN = 0.45; // make it look real
    private static final long STEP_DELAY = 50; // delay between packets sent to server in ms
    private static final double NEAR_START = 0.5; // the threshold until near the start

    private static final ExecutorService WORKER = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "AutoFarmer-MoveWorker");
        t.setDaemon(true);
        return t;
    });

    public static void move(String pos1Str, String pos2Str) {
        Vec3 pos1 = parseCoords(pos1Str);
        Vec3 pos2 = parseCoords(pos2Str);
        if(pos1 == null || pos2 == null) { return; }

        Minecraft client = Minecraft.getInstance();

        client.execute(() -> {
            LocalPlayer player = client.player;
            if (player == null || client.level == null) return;

            Vec3 currentPos = player.position();
            List<Vec3> fullPath = new ArrayList<>();

            if (currentPos.distanceTo(pos1) > NEAR_START) {
                fullPath.addAll(generatePath(currentPos, pos1, STEP_LEN));
                fullPath.add(pos1); // ensure final
            }

            List<Vec3> path = generatePath(pos1, pos2, STEP_LEN);
            makeRealistic(client, player, path);
        });
    }
    // Parses the Coordinates from the string output to Vec3
    private static Vec3 parseCoords(String coordString) {
        if(coordString == null) { return null; }
        String s = coordString.replaceAll("[()\\[\\]]", "").trim();
        String[] parts = s.split(",");
        if(parts.length < 3) { return null; }
        try {
            double x = Double.parseDouble(parts[0].trim());
            double y = Double.parseDouble(parts[1].trim());
            double z = Double.parseDouble(parts[2].trim());
            return new Vec3(x,y,z);
        } catch (NumberFormatException ne) {
            ne.printStackTrace();
            return null;
        }
    }

    private static List<Vec3> generatePath(Vec3 start, Vec3 end, double stepLen) {
        List<Vec3> steps = new ArrayList<>();
        Vec3 delta = end.subtract(start);
        double distance = delta.length();

        if(distance <= 0) { return steps; }
        Vec3 direction = delta.normalize();
        int count = (int) Math.ceil(distance / stepLen);
        for(int i = 1; i < count; i++) {
            double blocksTraveled = Math.min(i * stepLen, distance);
            Vec3 a = start.add(direction.scale(blocksTraveled));
            steps.add(a);
        }
        return steps;
    }

    private static void makeRealistic(Minecraft client, LocalPlayer player, List<Vec3> path) {
        if(path.isEmpty()) { return; }
        AtomicBoolean stop = new AtomicBoolean(false);

        try {
            for(Vec3 step : path) {
                if(stop.get()) { break; }

                boolean onGround = player.onGround();

                Packet<?> packet = createPositionPacket(step.x, step.y, step.z, onGround);
                if (packet == null) { System.err.println("Could not create move packet for current mappings."); return; }
                client.getConnection().send(packet);

                try {
                    Objects.requireNonNull(client.getConnection()).send(packet);
                } catch (Throwable t) {
                    t.printStackTrace();
                    return;
                }
                client.execute(() -> {
                    LocalPlayer p = client.player;
                    if (p != null) p.teleportTo(step.x, step.y, step.z);
                });

                try {
                    Thread.sleep(STEP_DELAY);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        } finally {
            // lol
        }
    }

    private static Packet<?> createPositionPacket(double x, double y, double z, boolean onGround) {
        try {
            Class<?> cls = Class.forName("net.minecraft.network.protocol.game.ServerboundMovePlayerPacket");

            for (Class<?> c : cls.getDeclaredClasses()) {
                try {
                    return (Packet<?>) c.getConstructor(double.class, double.class, double.class, boolean.class, boolean.class)
                            .newInstance(x, y, z, onGround, onGround);
                } catch (NoSuchMethodException ignored) {}
            }

            try {
                Class<?> vecClass = Class.forName("net.minecraft.world.phys.Vec3");
                Object vec = vecClass.getConstructor(double.class, double.class, double.class).newInstance(x, y, z);
                for (Class<?> c : cls.getDeclaredClasses()) {
                    try {
                        return (Packet<?>) c.getConstructor(vecClass, boolean.class, boolean.class)
                                .newInstance(vec, onGround, onGround);
                    } catch (NoSuchMethodException ignored) {}
                }
            } catch (ClassNotFoundException ignoredVec) {}

            try {
                return (Packet<?>) cls.getConstructor(double.class, double.class, double.class, boolean.class, boolean.class)
                        .newInstance(x, y, z, onGround, onGround);
            } catch (NoSuchMethodException ignored) {}

            try {
                Class<?> alt = Class.forName("net.minecraft.network.protocol.game.ServerboundPlayerPositionPacket");
                return (Packet<?>) alt.getConstructor(double.class, double.class, double.class, boolean.class)
                        .newInstance(x, y, z, onGround);
            } catch (ClassNotFoundException | NoSuchMethodException ignoredAlt) {}

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
