package akarahnet.commands;

import akarahnet.data.mob.CustomMob;
import dev.akarah.pluginpacks.data.PackRepository;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class CMobCommand {
    public static void register(Commands commands) {
        commands.register(Commands.literal("cmob")
                .then(
                        Commands.argument("key",
                                        RegistryArgumentType.forRegistry(
                                                PackRepository.getInstance().getRegistry(CustomMob.NAMESPACE).orElseThrow()))
                                .executes(ctx -> {
                                    if (ctx.getSource().getExecutor() instanceof Player p) {
                                        var mob = ctx.getArgument("key", CustomMob.class);
                                        var loc = p.getLocation();
                                        mob.spawn(loc);
                                        p.sendMessage(Component.text("Spawned mob!"));
                                    }
                                    return 0;
                                }))
                .build());
    }
}
