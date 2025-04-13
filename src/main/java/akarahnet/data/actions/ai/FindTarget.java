package akarahnet.data.actions.ai;

import akarahnet.Core;
import akarahnet.data.mob.MobUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.akarah.actions.Environment;
import dev.akarah.actions.steps.Action;
import dev.akarah.actions.steps.ActionType;
import dev.akarah.actions.steps.generic.Noop;
import dev.akarah.actions.values.Value;
import dev.akarah.actions.values.Values;
import dev.akarah.actions.values.casting.EntityValue;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public record FindTarget(EntityValue targetEntity, boolean onlyPlayers, double radius, Action runIfFound,
                         Action runIfNotFound) implements Action {
    public static MapCodec<FindTarget> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Value.ENTITY.optionalFieldOf("entity", Values.DEFAULT_ENTITY).forGetter(FindTarget::targetEntity),
            Codec.BOOL.optionalFieldOf("only_players", true).forGetter(FindTarget::onlyPlayers),
            Codec.DOUBLE.fieldOf("radius").forGetter(FindTarget::radius),
            Action.CODEC.optionalFieldOf("if_found", new Noop()).forGetter(FindTarget::runIfFound),
            Action.CODEC.optionalFieldOf("or_else", new Noop()).forGetter(FindTarget::runIfNotFound)
    ).apply(instance, FindTarget::new));

    public static ActionType TYPE = new ActionType(NamespacedKey.fromString("entity/ai/find_target"));

    @Override
    public void execute(Environment environment) {
        var entity = environment.resolve(this.targetEntity());
        entity.getScheduler().run(Core.getInstance(), task -> {
            var nearbyEntities = entity.getNearbyEntities(this.radius, this.radius, this.radius);
            Entity nearestEntity = null;
            var nearestDist = 100000.0;

            for (var nearbyEntity : nearbyEntities) {
                var dist = nearbyEntity.getLocation().distance(entity.getLocation());
                if (dist <= nearestDist) {
                    if (onlyPlayers && nearbyEntity.getType().equals(EntityType.PLAYER)) {
                        nearestEntity = nearbyEntity;
                        nearestDist = dist;
                    } else if (!onlyPlayers) {
                        nearestEntity = nearbyEntity;
                        nearestDist = dist;
                    }
                }
            }

            if (nearestEntity != null) {
                var vector = nearestEntity.getLocation().subtract(entity.getLocation()).toVector();
                var yaw = new Location(Bukkit.getWorld("world"), 0.0, 0.0, 0.0).setDirection(vector).getYaw();
                MobUtils.setYaw(entity, yaw);
                runIfFound.execute(environment);
            } else {
                runIfNotFound.execute(environment);
            }
        }, () -> {
        });

    }

    @Override
    public ActionType getType() {
        return TYPE;
    }
}
