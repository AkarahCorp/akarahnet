package akarahnet.data.actions.meta;

import akarahnet.data.items.stats.StatsHolder;
import akarahnet.data.mob.MobUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.akarah.actions.Environment;
import dev.akarah.actions.values.Value;
import dev.akarah.actions.values.ValueType;
import dev.akarah.actions.values.Values;
import dev.akarah.actions.values.casting.EntityValue;
import dev.akarah.pluginpacks.Codecs;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public record EntityStat(EntityValue targetEntity, NamespacedKey stat) implements Value<Double> {
    public static MapCodec<EntityStat> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Value.ENTITY.optionalFieldOf("entity", Values.DEFAULT_ENTITY).forGetter(EntityStat::targetEntity),
            Codecs.NAMESPACED_KEY.fieldOf("stat").forGetter(EntityStat::stat)
    ).apply(instance, EntityStat::new));

    public static ValueType TYPE = new ValueType(NamespacedKey.fromString("entity/stat"));

    @Override
    public Double get(Environment environment) {
        var entity = environment.resolve(this.targetEntity);
        switch (entity) {
            case Player p -> {
                var stats = StatsHolder.getInstance().getStatsFor(p.getUniqueId());
                return stats.get(this.stat);
            }
            case Entity e -> {
                var mob = MobUtils.getMobType(e);
                return mob.configuration().stats().get(this.stat);
            }
        }
    }

    @Override
    public ValueType getType() {
        return TYPE;
    }
}
