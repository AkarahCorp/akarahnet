package akarahnet.mob;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.persistence.PersistentDataType;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import akarahnet.Codecs;
import akarahnet.Core;
import dev.akarah.pluginpacks.data.PluginNamespace;

public record CustomMob(
        NamespacedKey id,
        String name,
        NamespacedKey entityType) {

    public static PluginNamespace<CustomMob> NAMESPACE = PluginNamespace.create("cmob");

    public static Codec<CustomMob> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codecs.NAMESPACED_KEY.fieldOf("id").forGetter(CustomMob::id),
            PrimitiveCodec.STRING.fieldOf("name").forGetter(CustomMob::name),
            Codecs.NAMESPACED_KEY.fieldOf("entity_type").forGetter(CustomMob::entityType))
            .apply(instance, CustomMob::new));

    public Entity spawn(Location loc) {
        var entityTypeValue = EntityType.fromName(this.entityType.value().toUpperCase());
        if (entityTypeValue == null) {
            return null;
        }
        var entity = loc.getWorld().spawnEntity(loc, entityTypeValue);
        entity.getPersistentDataContainer().set(Core.key("id"), PersistentDataType.STRING, this.id.toString());
        return entity;
    }
}