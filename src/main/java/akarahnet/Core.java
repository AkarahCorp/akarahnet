package akarahnet;

import akarahnet.items.CustomItem;
import dev.akarah.pluginpacks.data.PackRepository;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public final class Core extends JavaPlugin {
    public static Core INSTANCE;

    public static Core getInstance() {
        return INSTANCE;
    }

    public static NamespacedKey key(String path) {
        return new NamespacedKey(INSTANCE, path);
    }

    @Override
    public void onEnable() {
        INSTANCE = this;

        var r = PackRepository.RegistryInstance.create(CustomItem.CODEC, CustomItem.class);
        PackRepository.getInstance().addRegistry(CustomItem.NAMESPACE, r);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
