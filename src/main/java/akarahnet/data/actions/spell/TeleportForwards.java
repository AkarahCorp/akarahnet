package akarahnet.data.actions.spell;

import akarahnet.Core;
import akarahnet.data.items.stats.StatsHolder;
import akarahnet.data.mob.MobUtils;
import akarahnet.data.mob.event.MobEventHandlers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.akarah.actions.Environment;
import dev.akarah.actions.steps.Action;
import dev.akarah.actions.steps.ActionType;
import dev.akarah.actions.values.Value;
import dev.akarah.actions.values.Values;
import dev.akarah.actions.values.casting.EntityValue;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;
import java.util.UUID;

public record TeleportForwards(EntityValue entity, double distance, double damage) implements Action {
    public static MapCodec<TeleportForwards> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Value.ENTITY.optionalFieldOf("entity", Values.DEFAULT_ENTITY).forGetter(TeleportForwards::entity),
            Codec.DOUBLE.fieldOf("distance").forGetter(TeleportForwards::distance),
            Codec.DOUBLE.optionalFieldOf("damage", 0.0).forGetter(TeleportForwards::damage)
    ).apply(instance, TeleportForwards::new));

    public static ActionType TYPE = new ActionType(NamespacedKey.fromString("teleport_forwards"));

    @Override
    public void execute(Environment environment) {
        var entity = environment.resolve(this.entity);
        var pos = entity.getLocation().clone().add(0, 1, 0);
        var lastPos = pos.clone();
        var alreadyDamagedEntities = new ArrayList<UUID>();

        for (double d = 0; d < this.distance; d += 0.1) {
            var newPos = pos.add(pos.getDirection().normalize().multiply(0.1));
            if (newPos.getBlock().getBoundingBox().contains(newPos.x(), newPos.y(), newPos.z())
                    || newPos.getBlock().getBoundingBox().contains(newPos.x(), newPos.y() - 1, newPos.z())
                    || newPos.getBlock().getBoundingBox().contains(newPos.x(), newPos.y() + 1, newPos.z())) {
                break;
            }
            lastPos = pos.clone();

            Particle.DUST.builder()
                    .color(Color.fromRGB(133, 0, 133))
                    .location(newPos.clone().add(0, 1, 0))
                    .allPlayers()
                    .offset(0.1, 0.1, 0.1)
                    .count(3)
                    .spawn();

            if (this.damage > 0.0) {
                for (var damagedEntity : newPos.getNearbyEntities(3, 3, 3)) {
                    if (damagedEntity.getPersistentDataContainer().has(Core.key("health"))
                            && !alreadyDamagedEntities.contains(damagedEntity.getUniqueId())) {
                        MobUtils.setHealth(damagedEntity, MobUtils.getHealth(damagedEntity) - damage);
                        new MobEventHandlers().entityTakeDamage(
                                new EntityDamageEvent(damagedEntity, EntityDamageEvent.DamageCause.CUSTOM, DamageSource.builder(DamageType.GENERIC).build(), 0.01)
                        );

                        MobUtils.setVelocity(damagedEntity, entity.getLocation().getDirection().setY(0).normalize().multiply(0.5));
                        damagedEntity.playSound(
                                Sound.sound()
                                        .pitch(1)
                                        .volume(1)
                                        .seed(0)
                                        .source(Sound.Source.MASTER)
                                        .type(Key.key("minecraft:entity.enderman.hurt"))
                                        .build()
                        );

                        StatsHolder.getInstance().setAttackCooldown(entity.getUniqueId(), 0);

                        alreadyDamagedEntities.add(damagedEntity.getUniqueId());
                    }
                }
            }

        }
        entity.teleportAsync(lastPos).thenAccept(v -> {
            if (v) {
                MobUtils.addVelocity(entity, entity.getLocation().getDirection().multiply(0.2));
            }
        });
    }

    @Override
    public ActionType getType() {
        return TYPE;
    }
}
