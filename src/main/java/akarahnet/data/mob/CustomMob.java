package akarahnet.data.mob;

import akarahnet.Codecs;
import akarahnet.Core;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.akarah.pluginpacks.data.PluginNamespace;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;

public record CustomMob(
        NamespacedKey id,
        String name,
        NamespacedKey entityType,
        double health
) {

    public static PluginNamespace<CustomMob> NAMESPACE = PluginNamespace.create("cmob");

    public static Codec<CustomMob> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codecs.NAMESPACED_KEY.fieldOf("id").forGetter(CustomMob::id),
            PrimitiveCodec.STRING.fieldOf("name").forGetter(CustomMob::name),
            Codecs.NAMESPACED_KEY.fieldOf("entity_type").forGetter(CustomMob::entityType),
            PrimitiveCodec.DOUBLE.fieldOf("health").forGetter(CustomMob::health)
    ).apply(instance, CustomMob::new));

    public Entity spawn(Location loc) {
        var entityTypeValue = EntityType.fromName(this.entityType.value().toUpperCase());
        if (entityTypeValue == null) {
            return null;
        }

        var entity = loc.getWorld().spawnEntity(loc, entityTypeValue);
        entity.getPersistentDataContainer().set(Core.key("id"), PersistentDataType.STRING, this.id.toString());
        entity.getPersistentDataContainer().set(Core.key("health"), PersistentDataType.DOUBLE, this.health);

        if (entity instanceof LivingEntity le) {
            le.setMaximumNoDamageTicks(0);
        }
        return entity;
    }
}