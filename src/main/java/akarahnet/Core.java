package akarahnet;

import akarahnet.items.UpdateInventory;
import dev.akarah.pluginpacks.data.PackRepository;
import org.bukkit.Bukkit;
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

        PackRepository.getInstance().reloadRegistries();

        Bukkit.getGlobalRegionScheduler().runAtFixedRate(Core.getInstance(), task -> {
            for (var player : Bukkit.getServer().getOnlinePlayers()) {
                player.getScheduler().run(Core.getInstance(), subtask -> UpdateInventory.update(player.getInventory()), () -> {
                });
            }
        }, 1, 20);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
