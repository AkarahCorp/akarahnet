package akarahnet.player.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class MiscEvents implements Listener {
    @EventHandler
    public void stopCrafting(CraftItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void interactWithBlock(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);
        }
    }
}
