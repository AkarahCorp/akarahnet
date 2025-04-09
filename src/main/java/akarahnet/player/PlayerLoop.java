package akarahnet.player;

import akarahnet.items.StatsHolder;
import akarahnet.items.StatsObject;
import akarahnet.items.UpdateInventory;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;

public class PlayerLoop {
    public static AtomicInteger time = new AtomicInteger(0);

    public static void tick(Player p) {
        if (time.get() % 20 == 0) {
            UpdateInventory.update(p.getInventory());
        }
        StatsHolder.getInstance().updatePlayerStats(p);

        var stats = StatsHolder.getInstance().getStatsFor(p.getUniqueId());

        StatsHolder.getInstance().setHealth(
                p.getUniqueId(),
                StatsHolder.getInstance().getHealth(p.getUniqueId())
                        + stats.get(StatsObject.MAX_HEALTH) / 2000);

        StatsHolder.getInstance().setMana(
                p.getUniqueId(),
                StatsHolder.getInstance().getMana(p.getUniqueId())
                        + stats.get(StatsObject.MAX_MANA) / 2000);

        StatsHolder.getInstance().setAttackCooldown(
                p.getUniqueId(),
                StatsHolder.getInstance().getAttackCooldown(p.getUniqueId()) - 1
        );

        if (StatsHolder.getInstance().getHealth(p.getUniqueId()) > stats.get(StatsObject.MAX_HEALTH)) {
            StatsHolder.getInstance().setHealth(
                    p.getUniqueId(),
                    stats.get(StatsObject.MAX_HEALTH));
        }

        if (StatsHolder.getInstance().getMana(p.getUniqueId()) > stats.get(StatsObject.MAX_MANA)) {
            StatsHolder.getInstance().setMana(
                    p.getUniqueId(),
                    stats.get(StatsObject.MAX_MANA));
        }
        var hp = StatsHolder.getInstance().getHealth(p.getUniqueId());
        var mana = StatsHolder.getInstance().getMana(p.getUniqueId());
        p.setHealth(20);
        p.setFoodLevel(20);
        p.setSaturation(20);
        p.setTotalExperience(0);

        p.sendActionBar(
                Component.empty()
                        .append(
                                Component.text((int) hp + "/"
                                                + (int) stats.get(
                                                StatsObject.MAX_HEALTH))
                                        .color(TextColor.color(255, 133, 133)))
                        .append(
                                Component.text(" ".repeat(10)))
                        .append(
                                Component.text((int) mana + "/"
                                                + (int) stats.get(StatsObject.MAX_MANA))
                                        .color(TextColor.color(133, 133, 255)))
                        .font(Key.key("minecraft", "actionbar"))
                        .decoration(TextDecoration.BOLD, true)

        );

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
                        BossBar.Overlay.PROGRESS));
    }
}
