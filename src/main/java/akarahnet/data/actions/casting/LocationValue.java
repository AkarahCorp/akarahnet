package akarahnet.data.actions.casting;

import com.mojang.serialization.Codec;
import dev.akarah.actions.Environment;
import dev.akarah.actions.values.Value;
import dev.akarah.actions.values.ValueType;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public record LocationValue(Value<?> inner) implements Value<Location> {
    public static Codec<LocationValue> CODEC = Value.CODEC.xmap(LocationValue::new, LocationValue::inner);

    @Override
    public Location get(Environment environment) {
        return switch (environment.resolve(inner)) {
            case Location loc -> loc;
            default -> new Location(Bukkit.getWorld("world"), 0, 0, 0);
        };
    }

    @Override
    public ValueType getType() {
        return inner.getType();
    }
}
