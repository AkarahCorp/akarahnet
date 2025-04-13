package akarahnet.data.actions.meta;

import akarahnet.data.actions.casting.LocationValue;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.akarah.actions.Environment;
import dev.akarah.actions.values.Value;
import dev.akarah.actions.values.ValueType;
import io.papermc.paper.raytracing.RayTraceTarget;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public record StableLocationNear(LocationValue center, double radius) implements Value<Location> {
    public static MapCodec<StableLocationNear> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LocationValue.CODEC.fieldOf("center").forGetter(StableLocationNear::center),
            Codec.DOUBLE.fieldOf("radius").forGetter(StableLocationNear::radius)
    ).apply(instance, StableLocationNear::new));

    public static ValueType TYPE = new ValueType(NamespacedKey.fromString("stable_location"));

    @Override
    public Location get(Environment environment) {
        var center = environment.resolve(this.center);
        var tries = 0;
        while (true) {
            var tryAdjust = center.clone().add((Math.random() - 0.5) * radius, 5, (Math.random() - 0.5) * radius);
            var rayDown = center.getWorld().rayTrace(builder -> {
                builder.targets(RayTraceTarget.BLOCK)
                        .start(tryAdjust)
                        .direction(new Vector(0, -1, 0))
                        .maxDistance(50.0)
                        .blockFilter(Block::isSolid)
                        .fluidCollisionMode(FluidCollisionMode.NEVER)
                        .ignorePassableBlocks(true);
            });
            if (rayDown != null) {
                return rayDown.getHitPosition().toLocation(center.getWorld());
            }
            tries++;
            if (tries > 50) {
                return null;
            }
        }
    }

    @Override
    public ValueType getType() {
        return TYPE;
    }
}
