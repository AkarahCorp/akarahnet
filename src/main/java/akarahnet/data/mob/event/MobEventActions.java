package akarahnet.data.mob.event;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.akarah.actions.steps.Action;
import dev.akarah.actions.steps.generic.Noop;

public record MobEventActions(
        Action onTakeDamage,
        Action onInteract,
        Action onTick
) {
    public static Codec<MobEventActions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Action.CODEC.optionalFieldOf("on_take_damage", new Noop()).forGetter(MobEventActions::onTakeDamage),
            Action.CODEC.optionalFieldOf("on_interact", new Noop()).forGetter(MobEventActions::onInteract),
            Action.CODEC.optionalFieldOf("on_tick", new Noop()).forGetter(MobEventActions::onTick)
    ).apply(instance, MobEventActions::new));
}
