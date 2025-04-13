package akarahnet.data.actions.ai;

import akarahnet.Core;
import akarahnet.data.mob.MobUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.akarah.actions.Environment;
import dev.akarah.actions.steps.Action;
import dev.akarah.actions.steps.ActionType;
import dev.akarah.actions.values.Value;
import dev.akarah.actions.values.Values;
import dev.akarah.actions.values.casting.EntityValue;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public record FaceNearest(EntityValue targetEntity, double radius) implements Action {
    public static MapCodec<FaceNearest> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Value.ENTITY.optionalFieldOf("entity", Values.DEFAULT_ENTITY).forGetter(FaceNearest::targetEntity),
            Codec.DOUBLE.fieldOf("radius").forGetter(FaceNearest::radius)
    ).apply(instance, FaceNearest::new));

    public static ActionType TYPE = new ActionType(NamespacedKey.fromString("entity/face_nearest"));

    @Override
    public void execute(Environment environment) {
        var entity = environment.resolve(this.targetEntity());
        entity.getScheduler().run(Core.getInstance(), task -> {
            var nearbyEntities = entity.getNearbyEntities(this.radius, this.radius, this.radius);
            Entity nearestEntity = null;
            var nearestDist = 100000.0;

            for (var nearbyEntity : nearbyEntities) {
                var dist = nearbyEntity.getLocation().distance(entity.getLocation());
                if (dist <= nearestDist && nearbyEntity.getType().equals(EntityType.PLAYER)) {
                    nearestEntity = nearbyEntity;
                    nearestDist = dist;
                }
            }

            if (nearestEntity != null) {
                var vector = nearestEntity.getLocation().subtract(entity.getLocation()).toVector();
                var yaw = new Location(Bukkit.getWorld("world"), 0.0, 0.0, 0.0).setDirection(vector).getYaw();
                MobUtils.setYaw(entity, yaw);
            }
        }, () -> {
        });

    }

    @Override
    public ActionType getType() {
        return TYPE;
    }
}
