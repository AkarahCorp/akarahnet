package akarahnet.commands;

import akarahnet.items.CustomItem;
import dev.akarah.pluginpacks.data.PackRepository;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

public class CItemCommand {
    public static void register(Commands commands) {
        commands.register(Commands.literal("citem").then(
                Commands.argument("key", ArgumentTypes.namespacedKey()).executes(ctx -> {
                    if (ctx.getSource().getExecutor() instanceof Player p) {
                        var id = ctx.getArgument("key", NamespacedKey.class);
                        var lookup = PackRepository.getInstance().getRegistry(CustomItem.NAMESPACE).orElseThrow()
                                .get(id);

                        lookup.ifPresentOrElse(
                                item -> p.give(item.toItemStack()),
                                () -> p.sendMessage(Component.text(id + " is not a valid custom item!").color(TextColor.color(255, 0, 0)))
                        );
                    }
                    return 0;
                })
        ).build());
    }
}
