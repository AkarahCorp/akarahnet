package akarahnet.data.items;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.akarah.actions.steps.Action;
import dev.akarah.actions.steps.generic.Noop;

public record ItemEventHandlers(
        Action onRightClick,
        Action onMeleeAttack,
        Action onTakeDamage
) {
    public static Codec<ItemEventHandlers> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Action.CODEC.optionalFieldOf("on_right_click", new Noop()).forGetter(ItemEventHandlers::onRightClick),
            Action.CODEC.optionalFieldOf("on_melee_attack", new Noop()).forGetter(ItemEventHandlers::onMeleeAttack),
            Action.CODEC.optionalFieldOf("on_take_damage", new Noop()).forGetter(ItemEventHandlers::onTakeDamage)
    ).apply(instance, ItemEventHandlers::new));
}
