package akarahnet;

import akarahnet.commands.CItemCommand;
import akarahnet.commands.CMobCommand;
import akarahnet.commands.CReloadPacksCommand;
import akarahnet.commands.OpenShopCommand;
import akarahnet.data.actions.AknActionRegistry;
import akarahnet.data.items.CustomItem;
import akarahnet.data.mob.CustomMob;
import akarahnet.data.mob.model.MobModel;
import akarahnet.data.mob.model.MobModelType;
import akarahnet.data.mob.spawning.SpawnRule;
import akarahnet.data.mob.spawning.SpawnRuleInstance;
import akarahnet.inventory.Shop;
import dev.akarah.pluginpacks.data.PackRepository;
import dev.akarah.pluginpacks.multientry.MultiTypeRegistry;
import dev.akarah.pluginpacks.multientry.TypeRegistry;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class Bootstrapper implements PluginBootstrap {
    @Override
    public void bootstrap(@NotNull BootstrapContext bootstrapContext) {
        bootstrapContext.getLogger().info("Starting akarahnet bootstrapper");
        AknActionRegistry.registerAll();
        SpawnRule.registerAll();

        bootstrapContext.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            CItemCommand.register(event.registrar());
            CMobCommand.register(event.registrar());
            CReloadPacksCommand.register(event.registrar());
            OpenShopCommand.register(event.registrar());
        });


        try {
            PackRepository.getInstance().addRegistry(CustomItem.NAMESPACE,
                    PackRepository.RegistryInstance.create(CustomItem.CODEC, CustomItem.class));
            PackRepository.getInstance().addRegistry(CustomMob.NAMESPACE,
                    PackRepository.RegistryInstance.create(CustomMob.CODEC, CustomMob.class));
            PackRepository.getInstance().addRegistry(SpawnRuleInstance.NAMESPACE,
                    PackRepository.RegistryInstance.create(SpawnRuleInstance.CODEC, SpawnRuleInstance.class));
            PackRepository.getInstance().addRegistry(Shop.NAMESPACE,
                    PackRepository.RegistryInstance.create(Shop.CODEC, Shop.class));

            MultiTypeRegistry.getInstance().register(MobModel.NAMESPACE, TypeRegistry.create(MobModelType.CODEC));
            MobModel.registerAll();
        } catch (Exception e) {
            bootstrapContext.getLogger().error("An exception occurred while bootstrapping!");
            bootstrapContext.getLogger().error(e.toString());
        }
    }
}
