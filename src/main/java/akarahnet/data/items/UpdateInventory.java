package akarahnet.data.items;

import akarahnet.Core;
import dev.akarah.pluginpacks.data.PackRepository;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class UpdateInventory {
    public static void update(Inventory inventory) {
        var idx = 0;
        for (var item : inventory.getContents()) {
            if (item == null) {
                idx += 1;
                continue;
            }
            inventory.setItem(idx, UpdateInventory.tryUpdate(item));
            idx += 1;
        }
    }

    public static ItemStack tryUpdate(ItemStack original) {
        if (original.getPersistentDataContainer().has(Core.key("id"), PersistentDataType.STRING)) {
            var amount = original.getAmount();
            var id = original.getPersistentDataContainer().get(Core.key("id"), PersistentDataType.STRING);
            var key = NamespacedKey.fromString(id);
            var item = PackRepository.getInstance().getRegistry(CustomItem.NAMESPACE).orElseThrow().get(key);
            if (item.isPresent()) {
                var originalPdc = original.getPersistentDataContainer();
                var newItem = item.get().toItemStack();
                newItem.setAmount(amount);
                newItem.editPersistentDataContainer(pdc -> {
                    originalPdc.copyTo(pdc, true);
                });
                return newItem;
            }
            return original;
        }
        return original;
    }
}
