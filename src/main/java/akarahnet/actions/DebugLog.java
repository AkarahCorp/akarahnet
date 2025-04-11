package akarahnet.actions;

import akarahnet.Core;
import com.mojang.serialization.MapCodec;
import dev.akarah.actions.Environment;
import dev.akarah.actions.steps.Action;
import dev.akarah.actions.steps.ActionType;
import dev.akarah.actions.values.Value;
import dev.akarah.actions.values.casting.StringValue;
import org.bukkit.NamespacedKey;

public record DebugLog(StringValue content) implements Action {
    public static MapCodec<DebugLog> CODEC = Value.STRING.xmap(DebugLog::new, DebugLog::content).fieldOf("content");
    public static ActionType TYPE = new ActionType(NamespacedKey.fromString("log/debug"));

    @Override
    public void execute(Environment environment) {
        Core.getInstance().getSLF4JLogger().info(environment.resolve(this.content));
    }

    @Override
    public ActionType getType() {
        return null;
    }
}
