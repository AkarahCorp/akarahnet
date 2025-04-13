package akarahnet.data.actions.ai;

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

public record Gravity(EntityValue targetEntity, double distance) implements Action {
    public static MapCodec<Gravity> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Value.ENTITY.optionalFieldOf("entity", Values.DEFAULT_ENTITY).forGetter(Gravity::targetEntity),
            Codec.DOUBLE.fieldOf("distance").forGetter(Gravity::distance)
    ).apply(instance, Gravity::new));

    public static ActionType TYPE = new ActionType(NamespacedKey.fromString("entity/physics/gravity"));

    @Override
    public void execute(Environment environment) {
        var entity = environment.resolve(this.targetEntity());
        MobUtils.addCustomVelocity(entity, new Vector(0, -distance, 0));
    }

    @Override
    public ActionType getType() {
        return TYPE;
    }
}
