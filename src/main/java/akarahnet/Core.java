package akarahnet;

import akarahnet.items.UpdateInventory;
import akarahnet.mob.MobAttack;
import akarahnet.mob.MobLoop;
import akarahnet.player.DamageHandler;
import akarahnet.player.MapEvents;
import akarahnet.player.PlayerLoop;
import akarahnet.player.UseAbility;
import dev.akarah.pluginpacks.data.PackRepository;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

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

        this.getServer().getPluginManager().registerEvents(new UseAbility(), this);
        this.getServer().getPluginManager().registerEvents(new DamageHandler(), this);
        this.getServer().getPluginManager().registerEvents(new MapEvents(), this);
        this.getServer().getPluginManager().registerEvents(new MobAttack(), this);

        Bukkit.getGlobalRegionScheduler().runAtFixedRate(Core.getInstance(), task -> {
            for (var player : Bukkit.getServer().getOnlinePlayers()) {
                player.getScheduler().run(Core.getInstance(), subtask -> {
                    UpdateInventory.update(player.getInventory());
                }, () -> {
                });
            }
        }, 1, 20);

        Bukkit.getGlobalRegionScheduler().runAtFixedRate(Core.getInstance(), task -> {
            PlayerLoop.time.incrementAndGet();
            for (var player : Bukkit.getServer().getOnlinePlayers()) {
                player.getScheduler().run(Core.getInstance(), subtask -> {
                    PlayerLoop.tick(player);
                }, () -> {
                });
            }

            MobLoop.time.incrementAndGet();
            for (var entity : Objects.requireNonNull(Bukkit.getWorld("world")).getEntities()) {
                if (!(entity instanceof Player)) {
                    MobLoop.tick(entity);
                }
            }

        }, 1, 1);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
