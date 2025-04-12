package akarahnet.actions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public interface AknCodecs {
    Codec<Location> LOCATION = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.fieldOf("x").forGetter(Location::x),
            Codec.DOUBLE.fieldOf("y").forGetter(Location::y),
            Codec.DOUBLE.fieldOf("z").forGetter(Location::z),
            Codec.FLOAT.optionalFieldOf("pitch", 0.0f).forGetter(Location::getPitch),
            Codec.FLOAT.optionalFieldOf("yaw", 0.0f).forGetter(Location::getYaw)
    ).apply(instance, (x, y, z, pitch, yaw) -> new Location(Bukkit.getWorld("world"), x, y, z, yaw, pitch)));
}
