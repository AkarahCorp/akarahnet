package akarahnet.items;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import akarahnet.Core;
import dev.akarah.pluginpacks.data.PackRepository;

public class StatsHolder {
    static StatsHolder INSTANCE = new StatsHolder();

    public static StatsHolder getInstance() {
        return INSTANCE;
    }

    HashMap<UUID, StatsObject> playerStats = new HashMap<>();

    public StatsObject getStatsFor(UUID uuid) {
        return playerStats.get(uuid);
    }

    public void updatePlayerStats(Player p) {
        var baseStats = StatsObject.of()
                .add(StatsObject.MAX_HEALTH, 100)
                .add(StatsObject.MAX_MANA, 0);

        var items = new ItemStack[] {
                p.getInventory().getItem(EquipmentSlot.HAND),
                p.getInventory().getItem(EquipmentSlot.OFF_HAND),
                p.getInventory().getItem(EquipmentSlot.HEAD),
                p.getInventory().getItem(EquipmentSlot.CHEST),
                p.getInventory().getItem(EquipmentSlot.LEGS),
                p.getInventory().getItem(EquipmentSlot.FEET),
        };

        for (var item : items) {
            if (item != null) {
                var pdc = item.getPersistentDataContainer();
                if (pdc.has(Core.key("id"))) {
                    var id = pdc.get(Core.key("id"), PersistentDataType.STRING);
                    var ns = NamespacedKey.fromString(id);
                    var opt = PackRepository.getInstance().getRegistry(CustomItem.NAMESPACE)
                            .flatMap(it -> it.get(ns))
                            .flatMap(it -> it.stats());
                    if (opt.isPresent()) {
                        var newStats = opt.get();
                        baseStats = baseStats.add(newStats);
                    }
                }
            }
        }

        this.playerStats.put(p.getUniqueId(), baseStats);
    }
}
