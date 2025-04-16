package akarahnet.data.actions.vfx;

import akarahnet.Core;
import akarahnet.data.actions.casting.LocationValue;
import akarahnet.data.items.CustomItem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.akarah.actions.Environment;
import dev.akarah.actions.steps.Action;
import dev.akarah.actions.steps.ActionType;
import dev.akarah.pluginpacks.Codecs;
import dev.akarah.pluginpacks.data.PackRepository;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;

public record SpawnCItem(LocationValue location, NamespacedKey item, double chance) implements Action {
    public static MapCodec<SpawnCItem> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LocationValue.CODEC.fieldOf("location").forGetter(SpawnCItem::location),
            Codecs.NAMESPACED_KEY.fieldOf("item").forGetter(SpawnCItem::item),
            Codec.DOUBLE.optionalFieldOf("chance", 100.0).forGetter(SpawnCItem::chance)
    ).apply(instance, SpawnCItem::new));

    public static ActionType TYPE = new ActionType(NamespacedKey.fromString("world/spawn_citem"));

    @Override
    public void execute(Environment environment) {
        var loc = environment.resolve(this.location);
        Bukkit.getRegionScheduler().execute(Core.getInstance(), loc, () -> {
            PackRepository.getInstance().getRegistry(CustomItem.NAMESPACE)
                    .flatMap(x -> x.get(this.item))
                    .ifPresent(item -> {
                        var rng = Math.random() * 100;
                        if (rng <= this.chance) {
                            loc.getWorld().dropItem(
                                    loc,
                                    item.toItemStack()
                            );
                        }
                    });
        });
    }

    @Override
    public ActionType getType() {
        return TYPE;
    }
}
