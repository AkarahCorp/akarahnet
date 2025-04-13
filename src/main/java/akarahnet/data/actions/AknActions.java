package akarahnet.data.actions;

import akarahnet.data.actions.ai.*;
import akarahnet.data.actions.util.DebugLog;
import com.mojang.serialization.MapCodec;
import dev.akarah.actions.steps.Action;
import dev.akarah.actions.steps.ActionType;
import dev.akarah.pluginpacks.multientry.MultiTypeRegistry;

public interface AknActions {
    static void registerAll() {
        register(DebugLog.TYPE, DebugLog.CODEC);
        register(FaceNearest.TYPE, FaceNearest.CODEC);
        register(TryWalkForward.TYPE, TryWalkForward.CODEC);
        register(TryJump.TYPE, TryJump.CODEC);
        register(Gravity.TYPE, Gravity.CODEC);
        register(TryApplyVelocity.TYPE, TryApplyVelocity.CODEC);
    }

    @SuppressWarnings("unchecked")
    static void register(ActionType name, MapCodec<? extends Action> codec) {
        MultiTypeRegistry.getInstance().lookup(Action.INSTANCE_NAMESPACE)
                .orElseThrow()
                .register(name, (MapCodec<Action>) codec);
    }
}
