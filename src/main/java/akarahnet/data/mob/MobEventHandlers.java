package akarahnet.data.mob;

import akarahnet.Core;
import akarahnet.data.items.Stats;
import akarahnet.data.items.StatsHolder;
import com.destroystokyo.paper.event.entity.EndermanEscapeEvent;
import dev.akarah.actions.Environment;
import dev.akarah.actions.values.Values;
import dev.akarah.pluginpacks.data.PackRepository;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataType;

public class MobEventHandlers implements Listener {
    @EventHandler
    public void entityTakeDamage(EntityDamageEvent event) {
        if (event.isCancelled() || event.getDamage() == 0.0) {
            return;
        }

        if (event.getEntity().getPersistentDataContainer().has(Core.key("health"))) {
            var id = event.getEntity().getPersistentDataContainer().get(Core.key("id"), PersistentDataType.STRING);
            if (id == null) {
                return;
            }
            
            var mob = PackRepository.getInstance().getRegistry(CustomMob.NAMESPACE).orElseThrow()
                    .get(NamespacedKey.fromString(id)).orElseThrow();

            if (mob.invulnerable()) {
                event.setCancelled(true);
                if (event.getDamage() <= 10000000.0) {
                    event.setDamage(0);
                }
            }

            var env = Environment.empty()
                    .parameter(Values.DEFAULT_ENTITY_NAME, event.getEntity())
                    .parameter(NamespacedKey.fromString("damage"), event.getDamage());

            mob.event().onTakeDamage().execute(env);

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
            if (event.getEntity() instanceof LivingEntity le) {
                le.getScheduler().run(Core.getInstance(), task -> {
                    le.setNoDamageTicks(0);
                    le.setArrowsInBody(0);
                    le.setFireTicks(0);
                    le.setVisualFire(false);
                }, () -> {
                });
            }

        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerAttackEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player p && event.getEntity() instanceof LivingEntity entity) {
            entity.setNoDamageTicks(0);

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

    @EventHandler
    public void endermanTeleport(EndermanEscapeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void clickEntity(PlayerInteractEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        var id = event.getRightClicked().getPersistentDataContainer().get(Core.key("id"), PersistentDataType.STRING);
        if (id == null) {
            return;
        }

        if (event.getHand() == EquipmentSlot.HAND) {
            var mob = PackRepository.getInstance().getRegistry(CustomMob.NAMESPACE).orElseThrow()
                    .get(NamespacedKey.fromString(id)).orElseThrow();

            var env = Environment.empty()
                    .parameter(NamespacedKey.fromString("entity/clicked"), event.getRightClicked())
                    .parameter(Values.DEFAULT_ENTITY_NAME, event.getPlayer());

            mob.event().onInteract().execute(env);
        }
    }


}
