package akarahnet.inventory;

import akarahnet.data.items.CustomItem;
import dev.akarah.pluginpacks.data.PackRepository;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ShopEvents implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        var clickedInv = event.getClickedInventory();
        if (clickedInv == null) {
            return;
        }
        if (clickedInv.getHolder() instanceof Shop(List<Shop.ShopEntry> entries)) {
            event.setCancelled(true);
            var clickedSlot = event.getSlot();
            var purchasedShopEntry = entries.get(clickedSlot);
            if (purchasedShopEntry == null) {
                return;
            }

            var outputItem = PackRepository.getInstance().getRegistry(CustomItem.NAMESPACE)
                    .flatMap(x -> x.get(purchasedShopEntry.item()))
                    .map(CustomItem::toItemStack)
                    .orElseThrow();
            List<ItemStack> costs = purchasedShopEntry.cost()
                    .stream()
                    .map(entry -> {
                        var ci = PackRepository.getInstance().getRegistry(CustomItem.NAMESPACE)
                                .flatMap(reg -> reg.get(entry.item()))
                                .orElseThrow();

                        var is = ci.toItemStack();
                        is.setAmount(entry.amount());
                        return is;
                    })
                    .toList();

            for (var cost : costs) {
                if (!event.getWhoClicked().getInventory().containsAtLeast(cost, cost.getAmount())) {
                    return;
                }
            }

            for (var cost : costs) {
                var amount = cost.getAmount();
                var slotIdx = 0;
                for (var playerCurrentCostSlot : event.getWhoClicked().getInventory().getContents()) {
                    if (playerCurrentCostSlot == null) {
                        slotIdx++;
                        continue;
                    }
                    if (playerCurrentCostSlot.isSimilar(cost) && amount > 0) {
                        var rem = playerCurrentCostSlot.getAmount() - Math.min(amount, 64);
                        amount -= playerCurrentCostSlot.getAmount();
                        playerCurrentCostSlot.setAmount(rem);
                        event.getWhoClicked().getInventory().setItem(slotIdx, playerCurrentCostSlot);
                    }
                    slotIdx++;
                }
            }

            event.getWhoClicked().getInventory().addItem(outputItem);
        }
    }
}
