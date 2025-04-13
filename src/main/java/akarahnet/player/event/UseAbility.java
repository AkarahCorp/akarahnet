package akarahnet.player.event;

import akarahnet.Core;
import akarahnet.data.items.stats.Stats;
import akarahnet.data.items.stats.StatsHolder;
import akarahnet.data.mob.MobUtils;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import java.util.ArrayList;
import java.util.UUID;

public class UseAbility implements Listener {
    @EventHandler
    public void onSwapHands(PlayerSwapHandItemsEvent event) {
        event.setCancelled(true);

        if (StatsHolder.getInstance().getMana(event.getPlayer().getUniqueId()) < 15.0) {
            return;
        }

        StatsHolder.getInstance().setMana(
                event.getPlayer().getUniqueId(),
                StatsHolder.getInstance().getMana(event.getPlayer().getUniqueId()) - 15.0);

        var pos = event.getPlayer().getLocation().add(0, 1, 0);
        var lastPos = pos.clone();

        var dmg = new ArrayList<UUID>();

        var stats = StatsHolder.getInstance().getStatsFor(event.getPlayer().getUniqueId());

        for (double d = 0; d < stats.get(Stats.TELEPORT_RANGE); d += 0.1) {
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

            for (var entity : newPos.getNearbyEntities(3, 3, 3)) {
                if (entity.getPersistentDataContainer().has(Core.key("health"))
                        && !dmg.contains(entity.getUniqueId())) {

                    StatsHolder.getInstance().setAttackCooldown(event.getPlayer().getUniqueId(), 0);
                    var damage = stats.get(Stats.ATTACK_DAMAGE) * (stats.get(Stats.TELEPORT_DAMAGE) / 100.0);
                    MobUtils.setHealth(entity, MobUtils.getHealth(entity) - damage);
                    MobUtils.setCustomVelocity(entity, event.getPlayer().getLocation().getDirection().normalize().multiply(0.5));
                    entity.playSound(
                            Sound.sound()
                                    .pitch(1)
                                    .volume(1)
                                    .seed(0)
                                    .source(Sound.Source.MASTER)
                                    .type(Key.key("minecraft:entity.enderman.hurt"))
                                    .build()
                    );
                    StatsHolder.getInstance().setAttackCooldown(event.getPlayer().getUniqueId(), 0);

                    if (entity instanceof LivingEntity le) {
                        le.getScheduler().run(Core.getInstance(), task -> {
                            le.setNoDamageTicks(0);
                            le.setArrowsInBody(0);
                            le.setFireTicks(0);
                            le.setVisualFire(false);
                        }, () -> {
                        });
                    }

                    dmg.add(entity.getUniqueId());
                }
            }
        }
        event.getPlayer().teleportAsync(lastPos).thenAccept(v -> {
            if (v) {
                event.getPlayer().setVelocity(event.getPlayer().getLocation().getDirection().multiply(0.2));
            }
        });

    }
}
