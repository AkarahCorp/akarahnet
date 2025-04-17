package akarahnet.data.mob;

import akarahnet.Core;
import akarahnet.util.LocalPDTs;
import dev.akarah.pluginpacks.data.PackRepository;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class MobUtils {
    public static void applyKnockback(Entity entity, Vector velocity) {
        if (entity instanceof Player p) {
            p.getScheduler().run(Core.getInstance(), task -> {
                if (p.getNoDamageTicks() <= 0 || p.getNoDamageTicks() == 20) {
                    p.setVelocity(
                            p.getVelocity().clone().add(velocity)
                    );
                }
            }, () -> {
            });
        } else {
            MobUtils.setVelocity(entity, MobUtils.getVelocity(entity).add(velocity));
        }
    }

    public static double getHealth(Entity entity) {
        return entity.getPersistentDataContainer().getOrDefault(Core.key("health"), PersistentDataType.DOUBLE, 0.0);
    }

    public static void setHealth(Entity entity, double health) {
        entity.getPersistentDataContainer().set(Core.key("health"), PersistentDataType.DOUBLE, health);
    }

    public static double getYaw(Entity entity) {
        if (entity instanceof LivingEntity p) {
            return p.getLocation().getYaw();
        }
        return entity.getPersistentDataContainer().getOrDefault(Core.key("yaw"), PersistentDataType.DOUBLE, 0.0);
    }

    public static void setYaw(Entity entity, double yaw) {
        entity.getPersistentDataContainer().set(Core.key("yaw"), PersistentDataType.DOUBLE, yaw);
    }

    public static Vector getVelocity(Entity entity) {
        if (entity instanceof LivingEntity le) {
            return le.getVelocity();
        }
        return LocalPDTs.toVector(entity.getPersistentDataContainer().getOrDefault(Core.key("velocity"), PersistentDataType.LIST.doubles(), List.of(0.0, 0.0, 0.0)));
    }

    public static void setVelocity(Entity entity, Vector vector) {
        if (entity instanceof LivingEntity le) {
            le.setVelocity(vector);
        }
        entity.getPersistentDataContainer().set(Core.key("velocity"), PersistentDataType.LIST.doubles(), LocalPDTs.fromVector(vector));
    }

    public static void addVelocity(Entity entity, Vector vector) {
        setVelocity(entity, getVelocity(entity).add(vector));
    }

    public static @NotNull NamespacedKey getId(Entity entity) {
        return Objects.requireNonNull(NamespacedKey.fromString(entity.getPersistentDataContainer().getOrDefault(Core.key("id"), PersistentDataType.STRING, "none")));
    }

    public static CustomMob getMobType(Entity entity) {
        return PackRepository.getInstance().getRegistry(CustomMob.NAMESPACE)
                .orElseThrow()
                .get(getId(entity))
                .orElseThrow();
    }
}
