package akarahnet.data.actions.meta;

import akarahnet.data.mob.MobUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.akarah.actions.Environment;
import dev.akarah.actions.values.Value;
import dev.akarah.actions.values.ValueType;
import dev.akarah.actions.values.Values;
import dev.akarah.actions.values.casting.EntityValue;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;

public record EntityLocation(EntityValue targetEntity) implements Value<Location> {
    public static MapCodec<EntityLocation> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Value.ENTITY.optionalFieldOf("entity", Values.DEFAULT_ENTITY).forGetter(EntityLocation::targetEntity)
    ).apply(instance, EntityLocation::new));

    public static ValueType TYPE = new ValueType(NamespacedKey.fromString("entity/location"));

    @Override
    public Location get(Environment environment) {
        var e = environment.resolve(this.targetEntity);
        var l = e.getLocation();
        l.setYaw((float) MobUtils.getYaw(e));
        return l;
    }

    @Override
    public ValueType getType() {
        return TYPE;
    }
}
