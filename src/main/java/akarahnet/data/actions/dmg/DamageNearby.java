package akarahnet.data.actions.dmg;

import akarahnet.Core;
import akarahnet.data.actions.casting.LocationValue;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.akarah.actions.Environment;
import dev.akarah.actions.steps.Action;
import dev.akarah.actions.steps.ActionType;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public record DamageNearby(LocationValue location, boolean onlyPlayers, double damage,
                           double radius) implements Action {
    public static MapCodec<DamageNearby> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LocationValue.CODEC.fieldOf("location").forGetter(DamageNearby::location),
            Codec.BOOL.optionalFieldOf("only_players", true).forGetter(DamageNearby::onlyPlayers),
            Codec.DOUBLE.fieldOf("damage").forGetter(DamageNearby::damage),
            Codec.DOUBLE.fieldOf("radius").forGetter(DamageNearby::radius)
    ).apply(instance, DamageNearby::new));

    public static ActionType TYPE = new ActionType(NamespacedKey.fromString("damage_nearby"));

    @Override
    public void execute(Environment environment) {
        var rLocation = environment.resolve(this.location);
        Bukkit.getRegionScheduler().run(Core.getInstance(), rLocation, task -> {
            var nearbyEntities = rLocation.getNearbyEntities(this.radius, this.radius, this.radius);

            for (var nearbyEntity : nearbyEntities) {
                var loc = rLocation.clone();
                if (onlyPlayers && nearbyEntity.getType().equals(EntityType.PLAYER)) {
                    if (nearbyEntity instanceof LivingEntity le) {
                        le.damage(damage);
                        le.setVelocity(
                                le.getVelocity()
                                        .add(loc.getDirection().normalize().multiply(0.5).setY(0.1))
                        );
                    }
                } else if (!onlyPlayers) {
                    if (nearbyEntity instanceof LivingEntity le) {
                        le.damage(damage);
                        le.setVelocity(
                                le.getVelocity()
                                        .add(loc.getDirection().normalize().multiply(0.5).setY(0.1))
                        );
                    }
                }
            }
        });

    }

    @Override
    public ActionType getType() {
        return TYPE;
    }
}
