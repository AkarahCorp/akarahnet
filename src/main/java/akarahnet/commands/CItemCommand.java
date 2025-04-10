package akarahnet.commands;

import akarahnet.data.items.CustomItem;
import dev.akarah.pluginpacks.data.PackRepository;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

public class CItemCommand {
    public static void register(Commands commands) {
        commands.register(Commands.literal("citem")
                .then(
                        Commands.argument("key",
                                        RegistryArgumentType.forRegistry(
                                                PackRepository.getInstance().getRegistry(CustomItem.NAMESPACE).orElseThrow()))
                                .executes(ctx -> {
                                    if (ctx.getSource().getExecutor() instanceof Player p) {
                                        var item = ctx.getArgument("key", CustomItem.class);
                                        p.give(item.toItemStack());
                                    }
                                    return 0;
                                })
                ).build());
    }
}
