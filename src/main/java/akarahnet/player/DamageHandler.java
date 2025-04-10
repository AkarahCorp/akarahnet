package akarahnet.player;

import akarahnet.data.items.StatsHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
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
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                hp -= event.getDamage() * 3;
            } else {
                hp -= event.getDamage();
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
