package akarahnet;

import akarahnet.data.items.UpdateInventory;
import akarahnet.data.mob.MobLoop;
import akarahnet.data.mob.event.MobEventHandlers;
import akarahnet.data.mob.spawning.SpawnRuleInstance;
import akarahnet.player.PlayerLoop;
import akarahnet.player.event.DamageHandler;
import akarahnet.player.event.MapEvents;
import akarahnet.player.event.UseAbility;
import dev.akarah.pluginpacks.data.PackRepository;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class Core extends JavaPlugin implements Listener {
    public static Core INSTANCE;

    public static Core getInstance() {
        return INSTANCE;
    }

    public static NamespacedKey key(String path) {
        return new NamespacedKey("akarahnet", path);
    }

    @Override
    public void onEnable() {
        INSTANCE = this;

        PackRepository.getInstance().reloadRegistries();

        this.getServer().getPluginManager().registerEvents(this, this);
        this.getServer().getPluginManager().registerEvents(new UseAbility(), this);
        this.getServer().getPluginManager().registerEvents(new DamageHandler(), this);
        this.getServer().getPluginManager().registerEvents(new MapEvents(), this);
        this.getServer().getPluginManager().registerEvents(new MobEventHandlers(), this);


        Bukkit.getGlobalRegionScheduler().runAtFixedRate(Core.getInstance(), task -> {
            for (var player : Bukkit.getServer().getOnlinePlayers()) {
                player.getScheduler().run(Core.getInstance(), subtask -> UpdateInventory.update(player.getInventory()), () -> {
                });
            }
        }, 1, 20);

        Bukkit.getGlobalRegionScheduler().runAtFixedRate(Core.getInstance(), task -> {
            PlayerLoop.time.incrementAndGet();
            MobLoop.time.incrementAndGet();

            SpawnRuleInstance.doSpawning();
        }, 1, 1);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void join(PlayerJoinEvent event) {
        var player = event.getPlayer();
        player.getScheduler().run(Core.getInstance(), subtask -> PlayerLoop.tick(player), () -> {
        });
    }

    @EventHandler
    public void spawnEntity(EntitySpawnEvent event) {
        var entity = event.getEntity();
        entity.getScheduler().runAtFixedRate(Core.getInstance(), subtask -> MobLoop.tick(entity), () -> {
        }, 1, 20);
    }
}
