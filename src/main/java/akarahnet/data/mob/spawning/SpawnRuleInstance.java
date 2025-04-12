package akarahnet.data.mob.spawning;

import akarahnet.data.mob.CustomMob;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.akarah.pluginpacks.Codecs;
import dev.akarah.pluginpacks.data.PackRepository;
import dev.akarah.pluginpacks.data.PluginNamespace;
import org.bukkit.NamespacedKey;

import java.util.NoSuchElementException;

public record SpawnRuleInstance(
        NamespacedKey mob,
        SpawnRule rule
) {
    public static PluginNamespace<SpawnRuleInstance> NAMESPACE = PluginNamespace.create("spawn_rule");

    public static Codec<SpawnRuleInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codecs.NAMESPACED_KEY.fieldOf("mob").forGetter(SpawnRuleInstance::mob),
            SpawnRule.CODEC.fieldOf("rule").forGetter(SpawnRuleInstance::rule)
    ).apply(instance, SpawnRuleInstance::new));

    public static void doSpawning() {
        var mobs = PackRepository.getInstance().getRegistry(CustomMob.NAMESPACE)
                .orElseThrow();
        var repo = PackRepository.getInstance().getRegistry(SpawnRuleInstance.NAMESPACE)
                .orElseThrow();
        for (var entry : repo.entries()) {
            try {
                entry.getValue().rule().trySpawn(
                        mobs.get(entry.getValue().mob())
                                .orElseThrow()
                );
            } catch (NoSuchElementException ignored) {
                
            }
        }
    }
}
