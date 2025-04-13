package akarahnet.data.actions.ai;

import akarahnet.Core;
import akarahnet.data.mob.MobUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.akarah.actions.Environment;
import dev.akarah.actions.steps.Action;
import dev.akarah.actions.steps.ActionType;
import dev.akarah.actions.values.Value;
import dev.akarah.actions.values.Values;
import dev.akarah.actions.values.casting.EntityValue;
import org.bukkit.NamespacedKey;
import org.bukkit.util.Vector;

public record TryJump(EntityValue targetEntity, double distance) implements Action {
    public static MapCodec<TryJump> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Value.ENTITY.optionalFieldOf("entity", Values.DEFAULT_ENTITY).forGetter(TryJump::targetEntity),
            Codec.DOUBLE.fieldOf("distance").forGetter(TryJump::distance)
    ).apply(instance, TryJump::new));

    public static ActionType TYPE = new ActionType(NamespacedKey.fromString("entity/ai/jump"));

    @Override
    public void execute(Environment environment) {
        var entity = environment.resolve(this.targetEntity());
        entity.getScheduler().run(Core.getInstance(), task -> {
            var tempLoc = entity.getLocation().clone();
            tempLoc.setYaw((float) MobUtils.getYaw(entity));
            tempLoc.setPitch(0);
            tempLoc.add(tempLoc.getDirection().normalize());
            if (tempLoc.getBlock().isSolid()) {
                tempLoc.add(0, 1, 0);
                if (!tempLoc.getBlock().isSolid()) {
                    MobUtils.addCustomVelocity(entity, new Vector(0, 0.5, 0));
                }
            }
        }, () -> {

        });

    }

    @Override
    public ActionType getType() {
        return TYPE;
    }
}
