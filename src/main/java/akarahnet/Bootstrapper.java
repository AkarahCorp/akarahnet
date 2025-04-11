package akarahnet;

import akarahnet.commands.CItemCommand;
import akarahnet.commands.CMobCommand;
import akarahnet.data.items.CustomItem;
import akarahnet.data.mob.CustomMob;
import dev.akarah.pluginpacks.data.PackRepository;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class Bootstrapper implements PluginBootstrap {
    @Override
    public void bootstrap(@NotNull BootstrapContext bootstrapContext) {
        bootstrapContext.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            CItemCommand.register(event.registrar());
            CMobCommand.register(event.registrar());
        });

        try {

            PackRepository.getInstance().addRegistry(CustomItem.NAMESPACE,
                    PackRepository.RegistryInstance.create(CustomItem.CODEC, CustomItem.class));
            PackRepository.getInstance().addRegistry(CustomMob.NAMESPACE,
                    PackRepository.RegistryInstance.create(CustomMob.CODEC, CustomMob.class));


        } catch (Exception e) {
            bootstrapContext.getLogger().error("An exception occurred while bootstrapping!");
            bootstrapContext.getLogger().error(e.toString());
        }
    }
}
