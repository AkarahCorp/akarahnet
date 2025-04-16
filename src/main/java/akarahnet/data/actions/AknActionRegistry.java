package akarahnet.data.actions;

import akarahnet.data.actions.ai.*;
import akarahnet.data.actions.dmg.DamageNearby;
import akarahnet.data.actions.meta.*;
import akarahnet.data.actions.sfx.PlayGlobalSound;
import akarahnet.data.actions.spell.TeleportForwards;
import akarahnet.data.actions.util.DebugLog;
import akarahnet.data.actions.util.RepeatNTimes;
import akarahnet.data.actions.vfx.PlayGlobalParticle;
import akarahnet.data.actions.vfx.SpawnCItem;
import com.mojang.serialization.MapCodec;
import dev.akarah.actions.steps.Action;
import dev.akarah.actions.steps.ActionType;
import dev.akarah.actions.values.Value;
import dev.akarah.actions.values.ValueType;
import dev.akarah.pluginpacks.multientry.MultiTypeRegistry;

public interface AknActionRegistry {


    static void registerAll() {
        action(DebugLog.TYPE, DebugLog.CODEC);
        action(FindTarget.TYPE, FindTarget.CODEC);
        action(DamageNearby.TYPE, DamageNearby.CODEC);
        action(TryWalkForward.TYPE, TryWalkForward.CODEC);
        action(TryJump.TYPE, TryJump.CODEC);
        action(Gravity.TYPE, Gravity.CODEC);
        action(TryApplyVelocity.TYPE, TryApplyVelocity.CODEC);
        action(PlayGlobalSound.TYPE, PlayGlobalSound.CODEC);
        action(AliveTimer.TYPE, AliveTimer.CODEC);
        action(RepeatNTimes.TYPE, RepeatNTimes.CODEC);
        action(PlayGlobalParticle.TYPE, PlayGlobalParticle.CODEC);
        action(TeleportForwards.TYPE, TeleportForwards.CODEC);
        action(SpawnCItem.TYPE, SpawnCItem.CODEC);

        value(EntityLifetime.TYPE, EntityLifetime.CODEC);
        value(EntityHealth.TYPE, EntityHealth.CODEC);
        value(EntityLocation.TYPE, EntityLocation.CODEC);
        value(StableLocationNear.TYPE, StableLocationNear.CODEC);
        value(EntityStat.TYPE, EntityStat.CODEC);
    }

    @SuppressWarnings("unchecked")
    static void action(ActionType name, MapCodec<? extends Action> codec) {
        MultiTypeRegistry.getInstance().lookup(Action.INSTANCE_NAMESPACE)
                .orElseThrow()
                .register(name, (MapCodec<Action>) codec);
    }

    @SuppressWarnings("unchecked")
    static void value(ValueType name, MapCodec<? extends Value<?>> codec) {
        MultiTypeRegistry.getInstance().lookup(Value.INSTANCE_NAMESPACE)
                .orElseThrow()
                .register(name, (MapCodec<Value<?>>) codec);
    }
}
