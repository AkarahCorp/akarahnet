package akarahnet.data.actions.meta;

import akarahnet.Core;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.akarah.actions.Environment;
import dev.akarah.actions.values.Value;
import dev.akarah.actions.values.ValueType;
import dev.akarah.actions.values.Values;
import dev.akarah.actions.values.casting.EntityValue;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

public record EntityHealth(EntityValue targetEntity) implements Value<Double> {
    public static MapCodec<EntityHealth> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Value.ENTITY.optionalFieldOf("entity", Values.DEFAULT_ENTITY).forGetter(EntityHealth::targetEntity)
    ).apply(instance, EntityHealth::new));

    public static ValueType TYPE = new ValueType(NamespacedKey.fromString("entity/health"));

    @Override
    public Double get(Environment environment) {
        return environment.resolve(this.targetEntity)
                .getPersistentDataContainer()
                .getOrDefault(Core.key("health"), PersistentDataType.DOUBLE, 0.0);
    }

    @Override
    public ValueType getType() {
        return TYPE;
    }
}
