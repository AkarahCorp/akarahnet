package akarahnet.data.mob;

import akarahnet.Core;
import akarahnet.data.items.Stats;
import akarahnet.data.items.StatsObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.akarah.actions.steps.generic.Noop;
import dev.akarah.pluginpacks.Codecs;
import dev.akarah.pluginpacks.data.PluginNamespace;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public record CustomMob(
        NamespacedKey id,
        String name,
        NamespacedKey entityType,
        StatsObject stats,

        MobEventActions event,
        boolean invulnerable
) {

    public static PluginNamespace<CustomMob> NAMESPACE = PluginNamespace.create("cmob");

    public static Codec<CustomMob> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codecs.NAMESPACED_KEY.fieldOf("id").forGetter(CustomMob::id),
            PrimitiveCodec.STRING.fieldOf("name").forGetter(CustomMob::name),
            Codecs.NAMESPACED_KEY.fieldOf("entity_type").forGetter(CustomMob::entityType),
            StatsObject.CODEC.fieldOf("stats").forGetter(CustomMob::stats),
            MobEventActions.CODEC.optionalFieldOf("event", new MobEventActions(new Noop(), new Noop(), new Noop())).forGetter(CustomMob::event),
            Codec.BOOL.optionalFieldOf("invulnerable", false).forGetter(CustomMob::invulnerable)
    ).apply(instance, CustomMob::new));

    public Entity spawn(Location loc) {
        var entityTypeValue = EntityType.fromName(this.entityType.value().toUpperCase());
        if (entityTypeValue == null) {
            return null;
        }

        var entity = loc.getWorld().spawnEntity(loc, EntityType.ENDERMAN, false);
        entity.getPersistentDataContainer().set(Core.key("id"), PersistentDataType.STRING, this.id.toString());
        entity.getPersistentDataContainer().set(Core.key("health"), PersistentDataType.DOUBLE, this.stats.get(Stats.MAX_HEALTH));

        if (entity instanceof LivingEntity le) {
            le.setMaximumNoDamageTicks(1);
            le.setRemoveWhenFarAway(false);
            le.setPersistent(false);
            
            le.registerAttribute(Attribute.SCALE);
            Objects.requireNonNull(le.getAttribute(Attribute.SCALE)).setBaseValue(this.stats.get(Stats.SCALE) / 100);

            le.registerAttribute(Attribute.ATTACK_DAMAGE);
            Objects.requireNonNull(le.getAttribute(Attribute.ATTACK_DAMAGE)).setBaseValue(this.stats.get(Stats.ATTACK_DAMAGE) * 2);
        }

        return entity;
    }
}