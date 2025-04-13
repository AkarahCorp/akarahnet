package akarahnet.data.actions.ai;

import akarahnet.Core;
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
import org.bukkit.persistence.PersistentDataType;

public record AliveTimer(EntityValue targetEntity, int time, Action runIfTrue) implements Action {
    public static MapCodec<AliveTimer> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Value.ENTITY.optionalFieldOf("entity", Values.DEFAULT_ENTITY).forGetter(AliveTimer::targetEntity),
            Codec.INT.fieldOf("time").forGetter(AliveTimer::time),
            Action.CODEC.fieldOf("if_true").forGetter(AliveTimer::runIfTrue)
    ).apply(instance, AliveTimer::new));

    public static ActionType TYPE = new ActionType(NamespacedKey.fromString("entity/do_every_n_ticks"));

    @Override
    public void execute(Environment environment) {
        var entity = environment.resolve(this.targetEntity());
        var lt = entity.getPersistentDataContainer()
                .getOrDefault(Core.key("lifetime"), PersistentDataType.INTEGER, 1);
        if ((lt % this.time) == 0) {
            this.runIfTrue.execute(environment);
        }
    }

    @Override
    public ActionType getType() {
        return TYPE;
    }
}
