package akarahnet.data.actions.util;

import akarahnet.Core;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.akarah.actions.Environment;
import dev.akarah.actions.steps.Action;
import dev.akarah.actions.steps.ActionType;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;

public record RepeatNTimes(Action action, int times) implements Action {
    public static MapCodec<RepeatNTimes> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Action.CODEC.fieldOf("do").forGetter(RepeatNTimes::action),
            Codec.INT.fieldOf("times").forGetter(RepeatNTimes::times)
    ).apply(instance, RepeatNTimes::new));

    public static ActionType TYPE = new ActionType(NamespacedKey.fromString("repeat_n_times"));

    @Override
    public void execute(Environment environment) {
        for (int i = 1; i < times; i++) {
            Bukkit.getGlobalRegionScheduler().runDelayed(Core.getInstance(), task -> {
                this.action.execute(environment);
            }, i);
        }
    }

    @Override
    public ActionType getType() {
        return TYPE;
    }
}
