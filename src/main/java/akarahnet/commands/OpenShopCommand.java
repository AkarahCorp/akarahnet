package akarahnet.commands;

import akarahnet.inventory.Shop;
import dev.akarah.pluginpacks.commands.RegistryArgumentType;
import dev.akarah.pluginpacks.data.PackRepository;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

public class OpenShopCommand {
    public static void register(Commands commands) {
        commands.register(Commands.literal("shop")
                .then(
                        Commands.argument("key",
                                        RegistryArgumentType.forRegistry(
                                                PackRepository.getInstance().getRegistry(Shop.NAMESPACE).orElseThrow()))
                                .executes(ctx -> {
                                    if (ctx.getSource().getExecutor() instanceof Player p) {
                                        var shop = ctx.getArgument("key", Shop.class);
                                        p.openInventory(shop.getInventory());
                                    }
                                    return 0;
                                })
                ).build());
    }
}
