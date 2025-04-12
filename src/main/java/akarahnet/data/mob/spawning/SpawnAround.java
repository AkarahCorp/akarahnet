package akarahnet.data.mob.spawning;

import akarahnet.Core;
import akarahnet.data.actions.AknCodecs;
import akarahnet.data.mob.CustomMob;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.papermc.paper.raytracing.RayTraceTarget;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.util.Vector;

import java.util.Objects;

public record SpawnAround(Location center, double radius, int maxMobs) implements SpawnRule {
    public static MapCodec<SpawnAround> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            AknCodecs.LOCATION.fieldOf("center").forGetter(SpawnAround::center),
            Codec.DOUBLE.fieldOf("radius").forGetter(SpawnAround::radius),
            Codec.INT.fieldOf("max_mobs").forGetter(SpawnAround::maxMobs)
    ).apply(instance, SpawnAround::new));

    public static SpawnRuleType TYPE = new SpawnRuleType(NamespacedKey.fromString("spawn_around"));

    @Override
    public void trySpawn(CustomMob mob) {
        var entities = center.getNearbyEntities(radius, radius, radius);
        if (entities.size() >= this.maxMobs) {
            return;
        }

        var newLoc = center.clone().add(
                (Math.random() - 0.5) * radius,
                10,
                (Math.random() - 0.5) * radius
        );
        try {
            var down = Objects.requireNonNull(newLoc.getWorld().rayTrace(builder -> builder
                    .maxDistance(200.0)
                    .direction(new Vector(0, -1, 0))
                    .start(newLoc)
                    .ignorePassableBlocks(true)
                    .targets(RayTraceTarget.BLOCK)
            )).getHitPosition();
            mob.spawn(new Location(
                    Bukkit.getWorld(Core.key("game_world")),
                    down.getX(),
                    down.getY(),
                    down.getZ()
            ));
        } catch (NullPointerException ignored) {
        }
    }

    @Override
    public SpawnRuleType getType() {
        return TYPE;
    }
}
