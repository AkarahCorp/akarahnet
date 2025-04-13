package akarahnet.player.event;

import akarahnet.data.items.stats.StatsHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.time.Duration;

public class DamageHandler implements Listener {
    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player p) {
            var hp = StatsHolder.getInstance().getHealth(p.getUniqueId());
            switch (event.getCause()) {
                case FALL -> hp -= event.getDamage() * 3;
                case VOID -> hp -= event.getDamage() * 20;
                default -> hp -= event.getDamage();
            }
            StatsHolder.getInstance().setHealth(p.getUniqueId(), hp);

            if (hp <= 0) {
                p.showTitle(
                        Title.title(
                                Component.empty(),
                                Component.text("YOU DIED")
                                        .color(TextColor.color(255, 0, 0))
                                        .decoration(TextDecoration.BOLD, true),
                                Title.Times.times(
                                        Duration.ofMillis(100),
                                        Duration.ofMillis(2000),
                                        Duration.ofMillis(100))));
                p.teleportAsync(new Location(Bukkit.getWorld("world"), 6.5, 75, 24.5));
                StatsHolder.getInstance().setHealth(p.getUniqueId(), 10000);
            }
        }
    }

    @EventHandler
    public void onDie(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            event.setCancelled(true);
        }
    }
}
