package akarahnet.data.items.stats;

import akarahnet.Core;
import akarahnet.data.items.CustomItem;
import dev.akarah.pluginpacks.data.PackRepository;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class StatsHolder {
    static StatsHolder INSTANCE = new StatsHolder();
    ConcurrentHashMap<UUID, StatsObject> playerStats = new ConcurrentHashMap<>();
    ConcurrentHashMap<UUID, Double> currentHealth = new ConcurrentHashMap<>();
    ConcurrentHashMap<UUID, Double> currentMana = new ConcurrentHashMap<>();
    ConcurrentHashMap<UUID, Integer> attackCooldown = new ConcurrentHashMap<>();

    public static StatsHolder getInstance() {
        return INSTANCE;
    }

    public StatsObject getStatsFor(UUID uuid) {
        return playerStats.get(uuid);
    }

    public double getHealth(UUID player) {
        return currentHealth.getOrDefault(player, 100.0);
    }

    public void setHealth(UUID player, double hp) {
        currentHealth.put(player, hp);
    }

    public void addHealth(UUID player, double hp) {
        currentHealth.put(player, currentHealth.getOrDefault(player, 100000.0) + hp);
    }

    public double getMana(UUID player) {
        return currentMana.getOrDefault(player, 100.0);
    }

    public void setMana(UUID player, double mana) {
        currentMana.put(player, mana);
    }

    public void addMana(UUID player, double mana) {
        currentMana.put(player, currentMana.getOrDefault(player, 100000.0) + mana);
    }

    public int getAttackCooldown(UUID player) {
        return attackCooldown.getOrDefault(player, 0);
    }

    public void setAttackCooldown(UUID player, int cd) {
        attackCooldown.put(player, cd);
    }

    public void tickAttackCooldown(UUID player) {
        attackCooldown.put(player, attackCooldown.getOrDefault(player, 0) - 1);
    }

    public void updatePlayerStats(Player p) {
        if (!currentHealth.containsKey(p.getUniqueId())) {
            currentHealth.put(p.getUniqueId(), 100000.0);
        }
        if (!currentMana.containsKey(p.getUniqueId())) {
            currentMana.put(p.getUniqueId(), 100000.0);
        }
        var baseStats = StatsObject.of()
                .add(Stats.MAX_HEALTH, 100)
                .add(Stats.MAX_MANA, 100)
                .add(Stats.MANA_REGEN, 100)
                .add(Stats.WALK_SPEED, 100)
                .add(Stats.ATTACK_RANGE, 3)
                .add(Stats.SCALE, 100);

        var items = new ItemStack[]{
                p.getInventory().getItem(EquipmentSlot.HAND),
                p.getInventory().getItem(EquipmentSlot.OFF_HAND),
                p.getInventory().getItem(EquipmentSlot.HEAD),
                p.getInventory().getItem(EquipmentSlot.CHEST),
                p.getInventory().getItem(EquipmentSlot.LEGS),
                p.getInventory().getItem(EquipmentSlot.FEET),
        };

        for (var item : items) {
            var pdc = item.getPersistentDataContainer();
            if (pdc.has(Core.key("id"))) {
                var id = pdc.get(Core.key("id"), PersistentDataType.STRING);
                assert id != null;
                var ns = NamespacedKey.fromString(id);
                var opt = PackRepository.getInstance().getRegistry(CustomItem.NAMESPACE)
                        .flatMap(it -> it.get(ns))
                        .flatMap(CustomItem::stats);
                if (opt.isPresent()) {
                    var newStats = opt.get();
                    baseStats = baseStats.add(newStats);
                }
            }
        }

        this.playerStats.put(p.getUniqueId(), baseStats);
    }
}
