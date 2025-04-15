package akarahnet.player.event;

import akarahnet.data.items.stats.StatsHolder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class UseAbility implements Listener {
    @EventHandler
    public void onSwapHands(PlayerSwapHandItemsEvent event) {
        event.setCancelled(true);

        if (StatsHolder.getInstance().getMana(event.getPlayer().getUniqueId()) < 15.0) {
            return;
        }

        StatsHolder.getInstance().setMana(
                event.getPlayer().getUniqueId(),
                StatsHolder.getInstance().getMana(event.getPlayer().getUniqueId()) - 15.0);


    }
}
