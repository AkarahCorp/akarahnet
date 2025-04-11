package akarahnet.actions;

import com.mojang.serialization.MapCodec;
import dev.akarah.actions.steps.Action;
import dev.akarah.actions.steps.ActionType;
import dev.akarah.pluginpacks.multientry.MultiTypeRegistry;

public interface AknActions {
    static void registerAll() {
        register(DebugLog.TYPE, DebugLog.CODEC);
    }

    @SuppressWarnings("unchecked")
    static void register(ActionType name, MapCodec<? extends Action> codec) {
        MultiTypeRegistry.getInstance().lookup(Action.INSTANCE_NAMESPACE)
                .orElseThrow()
                .register(name, (MapCodec<Action>) codec);
    }
}
