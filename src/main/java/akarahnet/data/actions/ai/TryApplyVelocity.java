package akarahnet.data.actions.ai;

import akarahnet.Core;
import akarahnet.data.mob.MobUtils;
import com.mojang.serialization.MapCodec;
import dev.akarah.actions.Environment;
import dev.akarah.actions.steps.Action;
import dev.akarah.actions.steps.ActionType;
import dev.akarah.actions.values.Value;
import dev.akarah.actions.values.Values;
import dev.akarah.actions.values.casting.EntityValue;
import org.bukkit.NamespacedKey;
import org.bukkit.util.Vector;

public record TryApplyVelocity(EntityValue targetEntity) implements Action {
    public static MapCodec<TryApplyVelocity> CODEC = Value.ENTITY.optionalFieldOf("entity", Values.DEFAULT_ENTITY).xmap(TryApplyVelocity::new, TryApplyVelocity::targetEntity);

    public static ActionType TYPE = new ActionType(NamespacedKey.fromString("entity/physics/apply_velocity"));

    @Override
    public void execute(Environment environment) {
        var entity = environment.resolve(this.targetEntity());
        entity.getScheduler().run(Core.getInstance(), task -> {
            var loc = entity.getLocation();

            var velocity = MobUtils.getVelocity(entity);

            {
                var locTmp = loc.clone().add(velocity.getX(), 0, 0);
                var voxel = locTmp.getBlock().getBoundingBox();
                if (voxel.contains(locTmp.toVector())) {
                    MobUtils.setVelocity(entity, MobUtils.getVelocity(entity).setX(0));
                }
            }

            {
                var locTmp = loc.clone().add(0, velocity.getY(), 0);
                var voxel = locTmp.getBlock().getBoundingBox();
                if (voxel.contains(locTmp.toVector())) {
                    MobUtils.setVelocity(entity, MobUtils.getVelocity(entity).setY(0));
                }
            }

            {
                var locTmp = loc.clone().add(0, 0, velocity.getZ());
                var voxel = locTmp.getBlock().getBoundingBox();
                if (voxel.contains(locTmp.toVector())) {
                    MobUtils.setVelocity(entity, MobUtils.getVelocity(entity).setZ(0));
                }
            }

            var newLoc = loc.add(MobUtils.getVelocity(entity));
            entity.teleportAsync(newLoc);
            MobUtils.addVelocity(entity, MobUtils.getVelocity(entity).divide(new Vector(2, 2, 2)).multiply(-1));
        }, () -> {
        });

    }

    @Override
    public ActionType getType() {
        return TYPE;
    }
}
