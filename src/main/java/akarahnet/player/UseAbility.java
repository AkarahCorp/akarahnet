package akarahnet.player;

import akarahnet.Core;
import akarahnet.data.items.StatsHolder;
import akarahnet.data.items.StatsObject;
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

        for (int i = 0; i < 32; i++) {
            var newPos = pos.add(pos.getDirection().normalize().multiply(0.25));
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
                if (entity.getPersistentDataContainer().has(Core.key("health")) && entity instanceof LivingEntity le
                        && !dmg.contains(entity.getUniqueId())) {
                    le.damage(
                            StatsHolder.getInstance().getStatsFor(event.getPlayer().getUniqueId())
                                    .get(StatsObject.ATTACK_DAMAGE) * 0.3
                    );
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
