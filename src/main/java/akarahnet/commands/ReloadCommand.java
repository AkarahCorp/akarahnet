package akarahnet.commands;

import dev.akarah.pluginpacks.data.PackRepository;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;

import java.util.Objects;

public class ReloadCommand {
    public static void register(Commands commands) {
        commands.register(Commands.literal("creload").executes(ctx -> {
            Objects.requireNonNull(ctx.getSource().getExecutor()).sendMessage(Component.text("Reloading registries..."));
            PackRepository.getInstance().reloadRegistries();
            ctx.getSource().getExecutor().sendMessage(Component.text("Done!"));
            return 0;
        }).build());
    }
}
