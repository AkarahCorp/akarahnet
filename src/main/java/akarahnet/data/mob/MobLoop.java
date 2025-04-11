package akarahnet.data.mob;

import akarahnet.Core;
import akarahnet.data.items.Stats;
import dev.akarah.actions.Environment;
import dev.akarah.actions.values.Values;
import dev.akarah.pluginpacks.data.PackRepository;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;

import java.util.concurrent.atomic.AtomicInteger;

public class MobLoop {
    public static AtomicInteger time = new AtomicInteger(0);

    public static void tick(Entity e) {
        var id = e.getPersistentDataContainer().get(Core.key("id"), PersistentDataType.STRING);
        if (id == null) {
            return;
        }

        var lifetime = e.getPersistentDataContainer().getOrDefault(Core.key("lifetime"), PersistentDataType.INTEGER, 0);
        e.getPersistentDataContainer().set(
                Core.key("lifetime"),
                PersistentDataType.INTEGER,
                lifetime
        );

        var mob = PackRepository.getInstance()
                .getRegistry(CustomMob.NAMESPACE)
                .orElseThrow()
                .get(NamespacedKey.fromString(id))
                .orElseThrow();

        var env = Environment.empty()
                .parameter(Values.DEFAULT_ENTITY_NAME, e)
                .parameter(NamespacedKey.fromString("lifetime"), lifetime);

        mob.event().onTick().execute(env);

        if (e instanceof LivingEntity le) {
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
                                    Component.text(mob.name())
                                            .color(TextColor.color(255, 0, 0))
                            )
                            .append(
                                    Component.text(" ")
                            )
                            .append(
                                    Component.text((int) (double) hp + "/" + (int) mob.stats().get(Stats.MAX_HEALTH) + "HP")
                                            .color(TextColor.color(200, 0, 0))
                            )
            );
        }
    }


}
