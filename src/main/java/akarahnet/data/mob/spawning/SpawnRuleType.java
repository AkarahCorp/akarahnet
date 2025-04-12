package akarahnet.data.mob.spawning;

import com.mojang.serialization.Codec;
import dev.akarah.pluginpacks.Codecs;
import org.bukkit.NamespacedKey;

public record SpawnRuleType(NamespacedKey name) {
    public static Codec<SpawnRuleType> CODEC = Codecs.NAMESPACED_KEY.xmap(SpawnRuleType::new, SpawnRuleType::name);
}
