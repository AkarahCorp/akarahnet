package akarahnet.data.mob.model;

import com.mojang.serialization.MapCodec;
import dev.akarah.pluginpacks.data.PluginNamespace;
import dev.akarah.pluginpacks.multientry.MultiTypeRegistry;
import dev.akarah.pluginpacks.multientry.TypeRegistry;
import dev.akarah.pluginpacks.multientry.TypeRegistrySupported;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public interface MobModel extends TypeRegistrySupported<MobModelType> {
    PluginNamespace<MobModel> NAMESPACE = PluginNamespace.create("mob_model");
    TypeRegistry<MobModel, MobModelType> REGISTRY = MultiTypeRegistry.getInstance().register(NAMESPACE, TypeRegistry.create(MobModelType.CODEC));

    static void registerAll() {
        register(CustomModel.TYPE, CustomModel.CODEC);
    }

    @SuppressWarnings("unchecked")
    static <T extends MobModel> void register(MobModelType type, MapCodec<T> codec) {
        REGISTRY.register(type, codec.xmap(x -> x, x -> (T) x));
    }

    Entity spawnChild(Location loc);
}
