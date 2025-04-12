package akarahnet.data.mob.spawning;

import akarahnet.data.mob.CustomMob;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.akarah.pluginpacks.data.PluginNamespace;
import dev.akarah.pluginpacks.multientry.MultiTypeRegistry;
import dev.akarah.pluginpacks.multientry.TypeRegistry;
import dev.akarah.pluginpacks.multientry.TypeRegistrySupported;

public interface SpawnRule extends TypeRegistrySupported<SpawnRuleType> {
    PluginNamespace<SpawnRule> INSTANCE_NAMESPACE = PluginNamespace.create("spawn_rule/instance");
    TypeRegistry<SpawnRule, SpawnRuleType> REGISTRY = MultiTypeRegistry.getInstance().register(SpawnRule.INSTANCE_NAMESPACE,
            TypeRegistry.create(SpawnRuleType.CODEC));
    Codec<SpawnRule> CODEC = MultiTypeRegistry.getInstance().lookup(INSTANCE_NAMESPACE).orElseThrow().codec();

    static void registerAll() {
        register(SpawnOnce.TYPE, SpawnOnce.CODEC);
        register(SpawnAround.TYPE, SpawnAround.CODEC);
    }

    @SuppressWarnings("unchecked")
    static void register(SpawnRuleType type, MapCodec<? extends SpawnRule> codec) {
        REGISTRY.register(type, (MapCodec<SpawnRule>) codec);
    }

    void trySpawn(CustomMob mob);
}
