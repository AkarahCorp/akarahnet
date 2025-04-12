package akarahnet.commands;

import dev.akarah.pluginpacks.data.PackRepository;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;

import java.util.Objects;

public class CReloadPacksCommand {
    public static void register(Commands commands) {
        commands.register(Commands.literal("creloadpacks")
                .executes(ctx -> {
                    for (var entity : Objects.requireNonNull(Bukkit.getWorld("world")).getEntities()) {
                        try {
                            entity.remove();
                        } catch (UnsupportedOperationException ignored) {

                        }
                    }
                    PackRepository.getInstance().reloadRegistries();
                    return 0;
                }).build());
    }
}
