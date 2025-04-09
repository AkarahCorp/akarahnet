package akarahnet;

import java.util.List;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;

public class Codecs {
    public static Codec<Material> MATERIAL = PrimitiveCodec.STRING.xmap(x -> Material.matchMaterial(x.toUpperCase()),
            Material::name);
    public static Codec<NamespacedKey> NAMESPACED_KEY = PrimitiveCodec.STRING.xmap(NamespacedKey::fromString,
            NamespacedKey::asString);
    public static Codec<Color> COLOR = PrimitiveCodec.INT.listOf(3, 3).xmap(
            x -> Color.fromRGB(x.getFirst(), x.get(1), x.get(2)),
            x -> List.of(x.getRed(), x.getGreen(), x.getBlue()));
}
