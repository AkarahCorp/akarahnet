package akarahnet.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public interface LocalCodecs {
    Codec<Location> LOCATION = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.fieldOf("x").forGetter(Location::x),
            Codec.DOUBLE.fieldOf("y").forGetter(Location::y),
            Codec.DOUBLE.fieldOf("z").forGetter(Location::z),
            Codec.FLOAT.optionalFieldOf("pitch", 0.0f).forGetter(Location::getPitch),
            Codec.FLOAT.optionalFieldOf("yaw", 0.0f).forGetter(Location::getYaw)
    ).apply(instance, (x, y, z, pitch, yaw) -> new Location(Bukkit.getWorld("world"), x, y, z, yaw, pitch)));

    Codec<Vector> VECTOR = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.fieldOf("x").forGetter(Vector::getX),
            Codec.DOUBLE.fieldOf("y").forGetter(Vector::getY),
            Codec.DOUBLE.fieldOf("z").forGetter(Vector::getZ)
    ).apply(instance, Vector::new));

    Codec<Color> COLOR = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("red").forGetter(Color::getRed),
            Codec.INT.fieldOf("green").forGetter(Color::getGreen),
            Codec.INT.fieldOf("blue").forGetter(Color::getBlue)
    ).apply(instance, Color::fromRGB));
}
