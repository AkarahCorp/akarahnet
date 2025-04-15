package akarahnet.player.event;

import akarahnet.Core;
import akarahnet.data.items.CustomItem;
import dev.akarah.actions.Environment;
import dev.akarah.actions.values.Values;
import dev.akarah.pluginpacks.data.PackRepository;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataType;

public class MiscEvents implements Listener {
    @EventHandler
    public void stopCrafting(CraftItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void runInteractions(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && !event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            event.setCancelled(true);
        }

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getHand() == EquipmentSlot.HAND) {
            var heldItem = event.getPlayer().getInventory().getItem(EquipmentSlot.HAND);
            if (heldItem.getPersistentDataContainer().has(Core.key("id"))) {
                var id = heldItem.getPersistentDataContainer().getOrDefault(Core.key("id"), PersistentDataType.STRING, "null");
                var item = PackRepository.getInstance().getRegistry(CustomItem.NAMESPACE)
                        .flatMap(x -> x.get(NamespacedKey.fromString(id)))
                        .orElseThrow();
                item.event().ifPresent(itemEventHandlers -> {
                    var env = Environment.empty()
                            .parameter(Values.DEFAULT_ENTITY_NAME, event.getPlayer());
                    itemEventHandlers.onRightClick().execute(env);
                });
            }
        }
    }
}
