package akarahnet.data.mob;

import akarahnet.Core;
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

        var mob = PackRepository.getInstance()
                .getRegistry(CustomMob.NAMESPACE)
                .orElseThrow()
                .get(NamespacedKey.fromString(id))
                .orElseThrow();

        if (e instanceof LivingEntity le) {
            le.setNoDamageTicks(-1);
            le.setArrowsInBody(0);
            le.setFireTicks(0);
            le.setVisualFire(false);

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
                                    Component.text((int) (double) hp + "/" + (int) mob.health() + "HP")
                                            .color(TextColor.color(200, 0, 0))
                            )
            );
        }
    }


}
