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

public record TryWalkForward(EntityValue targetEntity, double distance) implements Action {
    public static MapCodec<TryWalkForward> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Value.ENTITY.optionalFieldOf("entity", Values.DEFAULT_ENTITY).forGetter(TryWalkForward::targetEntity),
            Codec.DOUBLE.fieldOf("distance").forGetter(TryWalkForward::distance)
    ).apply(instance, TryWalkForward::new));

    public static ActionType TYPE = new ActionType(NamespacedKey.fromString("entity/ai/walk_forward"));

    @Override
    public void execute(Environment environment) {
        var entity = environment.resolve(this.targetEntity());
        entity.getScheduler().run(Core.getInstance(), task -> {
            var tempLoc = entity.getLocation().clone();
            tempLoc.setYaw((float) MobUtils.getYaw(entity));
            tempLoc.setPitch(0);
            var tempDir = tempLoc.getDirection();
            MobUtils.addVelocity(entity, tempDir.multiply(distance));
        }, () -> {

        });

    }

    @Override
    public ActionType getType() {
        return TYPE;
    }
}
