package akarahnet.data.mob.model;

import com.mojang.serialization.Codec;
import dev.akarah.pluginpacks.Codecs;
import org.bukkit.NamespacedKey;

public record MobModelType(NamespacedKey key) {
    public static Codec<MobModelType> CODEC = Codecs.NAMESPACED_KEY.xmap(MobModelType::new, MobModelType::key);
}
