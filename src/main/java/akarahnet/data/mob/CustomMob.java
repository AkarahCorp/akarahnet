package akarahnet.data.mob;

import akarahnet.Core;
import akarahnet.data.items.stats.Stats;
import akarahnet.data.items.stats.StatsObject;
import akarahnet.data.mob.event.MobEventActions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.akarah.actions.steps.generic.Noop;
import dev.akarah.pluginpacks.Codecs;
import dev.akarah.pluginpacks.data.PluginNamespace;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public record CustomMob(
        NamespacedKey id,
        String name,
        NamespacedKey entityType,
        StatsObject stats,
        Configuration configuration
) {
    public static PluginNamespace<CustomMob> NAMESPACE = PluginNamespace.create("cmob");

    public static Codec<CustomMob> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codecs.NAMESPACED_KEY.fieldOf("id").forGetter(CustomMob::id),
            PrimitiveCodec.STRING.fieldOf("name").forGetter(CustomMob::name),
            Codecs.NAMESPACED_KEY.fieldOf("entity_type").forGetter(CustomMob::entityType),
            StatsObject.CODEC.fieldOf("stats").forGetter(CustomMob::stats),
            Configuration.CODEC.optionalFieldOf("config", Configuration.DEFAULT).forGetter(CustomMob::configuration)
    ).apply(instance, CustomMob::new));

    public void spawn(Location loc) {
        var entityTypeValue = EntityType.fromName(this.entityType.value().toUpperCase());
        if (entityTypeValue == null) {
            return;
        }

        Bukkit.getServer().getRegionScheduler().run(Core.getInstance(), loc, task -> {
            var entity = loc.getWorld().spawnEntity(loc, entityTypeValue, false);
            MobUtils.setHealth(entity, this.stats.get(Stats.MAX_HEALTH));
            entity.getPersistentDataContainer().set(Core.key("id"), PersistentDataType.STRING, this.id.toString());

            if (entity instanceof LivingEntity le) {
                le.setMaximumNoDamageTicks(1);
                le.setRemoveWhenFarAway(false);
                le.setPersistent(false);

                Objects.requireNonNull(le.getAttribute(Attribute.SCALE)).setBaseValue(this.stats.get(Stats.SCALE) / 100);

                le.registerAttribute(Attribute.ATTACK_DAMAGE);
                Objects.requireNonNull(le.getAttribute(Attribute.ATTACK_DAMAGE)).setBaseValue(this.stats.get(Stats.ATTACK_DAMAGE) * 2);

                if (this.configuration.noAI()) {
                    le.setAI(false);
                }
                if (this.configuration.invulnerable()) {
                    le.setInvulnerable(true);
                }
            }
        });
    }

    public record Configuration(
            MobEventActions event,
            boolean invulnerable,
            boolean noAI
    ) {
        public static Codec<Configuration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                MobEventActions.CODEC.optionalFieldOf("event", new MobEventActions(new Noop(), new Noop(), new Noop())).forGetter(Configuration::event),
                Codec.BOOL.optionalFieldOf("invulnerable", false).forGetter(Configuration::invulnerable),
                Codec.BOOL.optionalFieldOf("no_ai", false).forGetter(Configuration::noAI)
        ).apply(instance, Configuration::new));

        public static Configuration DEFAULT = new Configuration(
                new MobEventActions(new Noop(), new Noop(), new Noop()),
                false,
                false
        );
    }
}