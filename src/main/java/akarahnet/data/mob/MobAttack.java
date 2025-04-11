package akarahnet.data.mob;

import akarahnet.Core;
import akarahnet.data.items.Stats;
import akarahnet.data.items.StatsHolder;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.persistence.PersistentDataType;

public class MobAttack implements Listener {
    @EventHandler
    public void entityTakeDamage(EntityDamageEvent event) {
        if (event.isCancelled() || event.getDamage() == 0.0) {
            return;
        }

        if (event.getEntity().getPersistentDataContainer().has(Core.key("health"))) {
            var hp = event.getEntity().getPersistentDataContainer().get(Core.key("health"), PersistentDataType.DOUBLE);
            if (hp == null) {
                hp = 0.0;
            }

            var fhp = hp - event.getDamage();
            if (fhp <= 0) {
                event.getEntity().remove();
            } else {
                event.getEntity().getPersistentDataContainer().set(Core.key("health"), PersistentDataType.DOUBLE, fhp);
            }
            event.setDamage(0);

        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerAttackEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player p && event.getEntity() instanceof LivingEntity entity) {
            if (StatsHolder.getInstance().getAttackCooldown(p.getUniqueId()) > 0) {
                event.setCancelled(true);
                event.setDamage(0);
                return;
            }

            StatsHolder.getInstance().setAttackCooldown(
                    p.getUniqueId(),
                    10 - ((int) StatsHolder.getInstance().getStatsFor(p.getUniqueId()).get(Stats.ATTACK_SPEED) / 10)
            );
            event.setDamage(
                    StatsHolder.getInstance().getStatsFor(p.getUniqueId()).get(Stats.ATTACK_DAMAGE)
            );

        }
    }
}
