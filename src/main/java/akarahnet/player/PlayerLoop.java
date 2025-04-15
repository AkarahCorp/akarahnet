package akarahnet.player;

import akarahnet.data.items.UpdateInventory;
import akarahnet.data.items.stats.Stats;
import akarahnet.data.items.stats.StatsHolder;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayerLoop {
    public static AtomicInteger time = new AtomicInteger(0);

    public static void tick(Player p) {
        try {
            PlayerLoop.updateStats(p);
        } catch (Exception ignored) {
        }
        if (time.get() % 3 == 0) {
            try {
                StatsHolder.getInstance().updatePlayerStats(p);
            } catch (Exception ignored) {
            }
            PlayerLoop.updateAttributes(p);
            PlayerLoop.sendActionBar(p);
        }
        if (time.get() % 20 == 0) {
            UpdateInventory.update(p.getInventory());
            PlayerLoop.sendBossBar(p);
        }
    }

    public static void updateStats(Player p) {
        var sh = StatsHolder.getInstance();
        var stats = sh.getStatsFor(p.getUniqueId());

        sh.addHealth(p.getUniqueId(), stats.get(Stats.MAX_HEALTH) / 2000);
        sh.addMana(p.getUniqueId(), stats.get(Stats.MAX_MANA) / 2000 * (stats.get(Stats.MANA_REGEN) / 100.0));

        if (sh.getHealth(p.getUniqueId()) >= stats.get(Stats.MAX_HEALTH)) {
            sh.setHealth(
                    p.getUniqueId(),
                    stats.get(Stats.MAX_HEALTH));
        }

        if (sh.getMana(p.getUniqueId()) >= stats.get(Stats.MAX_MANA)) {
            sh.setMana(
                    p.getUniqueId(),
                    stats.get(Stats.MAX_MANA));
        }

        sh.tickAttackCooldown(p.getUniqueId());

    }

    public static void updateAttributes(Player p) {
        var stats = StatsHolder.getInstance().getStatsFor(p.getUniqueId());
        p.setHealth(20);
        p.setFoodLevel(20);
        p.setSaturation(20);
        p.setTotalExperience(0);
        Objects.requireNonNull(p.getAttribute(Attribute.ENTITY_INTERACTION_RANGE)).setBaseValue(stats.get(Stats.ATTACK_RANGE));
        Objects.requireNonNull(p.getAttribute(Attribute.MOVEMENT_SPEED)).setBaseValue(stats.get(Stats.WALK_SPEED) / 1000);
    }

    public static void sendActionBar(Player p) {
        var sh = StatsHolder.getInstance();
        var stats = sh.getStatsFor(p.getUniqueId());
        var hp = sh.getHealth(p.getUniqueId());
        var mana = sh.getMana(p.getUniqueId());

        p.sendActionBar(
                Component.empty()
                        .append(
                                Component.text((int) hp + "/"
                                                + (int) stats.get(
                                                Stats.MAX_HEALTH))
                                        .color(TextColor.color(255, 133, 133)))
                        .append(
                                Component.text(" ".repeat(10)))
                        .append(
                                Component.text((int) mana + "/"
                                                + (int) stats.get(Stats.MAX_MANA))
                                        .color(TextColor.color(133, 133, 255)))
                        .font(Key.key("minecraft", "actionbar"))
                        .decoration(TextDecoration.BOLD, true)

        );
    }

    public static void sendBossBar(Player p) {
        for (var bar : p.activeBossBars()) {
            p.hideBossBar(bar);
        }

        p.showBossBar(
                BossBar.bossBar(
                        Component.empty()
                                .append(
                                        Component.text("AKARAHNET")
                                                .color(TextColor.color(
                                                        200, 0,
                                                        200))
                                                .decoration(TextDecoration.BOLD,
                                                        true))
                                .append(
                                        Component.text(" @ ")
                                                .color(TextColor.color(
                                                        133,
                                                        133,
                                                        133)))
                                .append(
                                        Component.text("mc.akarah.dev")
                                                .color(TextColor.color(
                                                        175, 0,
                                                        175)))
                                .append(
                                        Component.text(" (Indev 0.0.1) ")
                                                .color(TextColor.color(
                                                        60, 60,
                                                        60)))
                                .asComponent(),
                        0.0F,
                        BossBar.Color.PURPLE,
                        BossBar.Overlay.PROGRESS
                )
                // .flags(Set.of(BossBar.Flag.CREATE_WORLD_FOG))
        );
        if (p.getGameMode() == GameMode.CREATIVE) {
            p.setViewDistance(16);
            p.setSendViewDistance(16);
        } else {
            p.setViewDistance(5);
            p.setSendViewDistance(5);
        }
    }
}
