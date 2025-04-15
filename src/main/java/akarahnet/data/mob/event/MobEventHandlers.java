package akarahnet.data.mob.event;

import akarahnet.Core;
import akarahnet.data.items.stats.Stats;
import akarahnet.data.items.stats.StatsHolder;
import akarahnet.data.mob.MobUtils;
import com.destroystokyo.paper.event.entity.EndermanEscapeEvent;
import dev.akarah.actions.Environment;
import dev.akarah.actions.values.Values;
import org.bukkit.Bukkit;
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

import java.util.UUID;

public class MobEventHandlers implements Listener {
    @EventHandler
    public void entityTakeDamage(EntityDamageEvent event) {
        if (event.isCancelled() || event.getDamage() == 0.0) {
            return;
        }

        var pdc = event.getEntity().getPersistentDataContainer();
        if (pdc.has(Core.key("health"))) {
            var mob = MobUtils.getMobType(event.getEntity());
            if (mob.configuration().invulnerable()) {
                event.setCancelled(true);
                if (event.getDamage() <= 10000000.0) {
                    event.setDamage(0);
                }
            }

            var env = Environment.empty()
                    .parameter(Values.DEFAULT_ENTITY_NAME, event.getEntity())
                    .parameter(NamespacedKey.fromString("damage"), event.getDamage());

            mob.configuration().event().onTakeDamage().execute(env);

            var hp = MobUtils.getHealth(event.getEntity());

            var fhp = hp - event.getDamage();
            if (fhp <= 0) {
                if (pdc.has(Core.key("children"))) {
                    var children = pdc.get(Core.key("children"), PersistentDataType.LIST.strings());
                    assert children != null;
                    for (var child : children) {
                        var childEntity = Bukkit.getEntity(UUID.fromString(child));
                        assert childEntity != null;
                        childEntity.getScheduler().run(Core.getInstance(), task -> {
                            childEntity.remove();
                        }, () -> {
                        });
                    }
                }
                event.getEntity().remove();
            } else {
                MobUtils.setHealth(event.getEntity(), fhp);
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
        var sh = StatsHolder.getInstance();
        if (event.getDamager() instanceof Player p) {
            var entity = event.getEntity();

            if (sh.getAttackCooldown(p.getUniqueId()) > 0) {
                event.setCancelled(true);
                event.setDamage(0);
                return;
            }

            sh.setAttackCooldown(
                    p.getUniqueId(),
                    10 - ((int) sh.getStatsFor(p.getUniqueId()).get(Stats.ATTACK_SPEED) / 10)
            );
            event.setDamage(
                    sh.getStatsFor(p.getUniqueId()).get(Stats.ATTACK_DAMAGE)
            );
            MobUtils.setVelocity(entity, p.getLocation().getDirection().normalize().setY(0.1));
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

        if (event.getHand() == EquipmentSlot.HAND) {
            var mob = MobUtils.getMobType(event.getRightClicked());

            var env = Environment.empty()
                    .parameter(NamespacedKey.fromString("entity/clicked"), event.getRightClicked())
                    .parameter(Values.DEFAULT_ENTITY_NAME, event.getPlayer());

            mob.configuration().event().onInteract().execute(env);
        }
    }


}
