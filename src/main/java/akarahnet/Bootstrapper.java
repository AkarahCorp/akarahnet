package akarahnet;

import akarahnet.commands.CItemCommand;
import akarahnet.commands.ReloadCommand;
import akarahnet.items.CustomItem;
import dev.akarah.pluginpacks.data.PackRepository;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;

public class Bootstrapper implements PluginBootstrap {
    @Override
    public void bootstrap(BootstrapContext bootstrapContext) {
        PackRepository.getInstance().reloadRegistries();

        PackRepository.getInstance().addRegistry(CustomItem.NAMESPACE, PackRepository.RegistryInstance.create(CustomItem.CODEC, CustomItem.class));

        bootstrapContext.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {

            ReloadCommand.register(event.registrar());
            CItemCommand.register(event.registrar());
        });
    }
}
