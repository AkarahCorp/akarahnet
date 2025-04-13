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

public record EntityLifetime(EntityValue targetEntity) implements Value<Integer> {
    public static MapCodec<EntityLifetime> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Value.ENTITY.optionalFieldOf("entity", Values.DEFAULT_ENTITY).forGetter(EntityLifetime::targetEntity)
    ).apply(instance, EntityLifetime::new));

    public static ValueType TYPE = new ValueType(NamespacedKey.fromString("entity/lifetime"));

    @Override
    public Integer get(Environment environment) {
        return environment.resolve(this.targetEntity)
                .getPersistentDataContainer()
                .getOrDefault(Core.key("lifetime"), PersistentDataType.INTEGER, 0);
    }

    @Override
    public ValueType getType() {
        return TYPE;
    }
}
