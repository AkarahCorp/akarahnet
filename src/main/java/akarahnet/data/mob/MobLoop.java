package akarahnet.data.mob;

import akarahnet.Core;
import akarahnet.data.items.stats.Stats;
import akarahnet.util.LocalPDTs;
import dev.akarah.actions.Environment;
import dev.akarah.actions.values.Values;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class MobLoop {
    public static AtomicInteger time = new AtomicInteger(0);

    public static void tick(Entity e) {
        var lifetime = e.getPersistentDataContainer().getOrDefault(Core.key("lifetime"), PersistentDataType.INTEGER, 0);
        e.getPersistentDataContainer().set(
                Core.key("lifetime"),
                PersistentDataType.INTEGER,
                lifetime
        );

        if (!e.getPersistentDataContainer().has(Core.key("id"))) {
            return;
        }

        var mob = MobUtils.getMobType(e);

        var env = Environment.empty()
                .parameter(Values.DEFAULT_ENTITY_NAME, e)
                .parameter(NamespacedKey.fromString("lifetime"), lifetime);

        mob.configuration().event().onTick().execute(env);

        if (e instanceof LivingEntity le) {
            updateVisuals(le, mob);
        }

        updateChildren(e);
    }

    public static void updateVisuals(LivingEntity le, CustomMob mob) {
        le.getScheduler().run(Core.getInstance(), task -> {
            le.setNoDamageTicks(0);
            le.setArrowsInBody(0);
            le.setFireTicks(0);
            le.setVisualFire(false);
        }, () -> {
        });

        var hp = le.getPersistentDataContainer().get(Core.key("health"), PersistentDataType.DOUBLE);
        if (hp == null) {
            hp = 0.0;
        }
        le.setCustomNameVisible(true);

        le.customName(
                Component.empty()
                        .append(
                                Component.text(mob.configuration().name())
                                        .color(TextColor.color(255, 0, 0))
                        )
                        .append(
                                Component.text(" ")
                        )
                        .append(
                                Component.text((int) (double) hp + "/" + mob.configuration().stats().get(Stats.MAX_HEALTH) + "HP")
                                        .color(TextColor.color(200, 0, 0))
                        )
        );
    }

    public static void updateChildren(Entity e) {
        var pdc = e.getPersistentDataContainer();
        var pos = e.getLocation();
        if (pdc.has(Core.key("children"))) {
            var children = pdc.get(Core.key("children"), PersistentDataType.LIST.strings());
            assert children != null;
            for (var child : children) {
                var childEntity = Bukkit.getEntity(UUID.fromString(child));
                assert childEntity != null;
                childEntity.getScheduler().run(Core.getInstance(), task -> {
                    var childPdc = childEntity.getPersistentDataContainer();
                    childEntity.teleportAsync(
                            pos.add(0, 0.5, 0)
                                    .add(LocalPDTs.fromList(childPdc.getOrDefault(Core.key("children/offset"), PersistentDataType.LIST.doubles(), List.of(0.0, 0.0, 0.0))))
                    );
                }, () -> {
                });
            }
        }
    }

}
