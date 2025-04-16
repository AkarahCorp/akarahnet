package akarahnet.data.mob;

import akarahnet.Core;
import akarahnet.data.items.stats.Stats;
import akarahnet.data.items.stats.StatsObject;
import akarahnet.data.mob.event.MobEventActions;
import akarahnet.util.LocalCodecs;
import akarahnet.util.LocalPDTs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.akarah.actions.steps.generic.Noop;
import dev.akarah.pluginpacks.Codecs;
import dev.akarah.pluginpacks.data.PluginNamespace;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public record CustomMob(
        NamespacedKey id,
        Configuration configuration
) {
    public static PluginNamespace<CustomMob> NAMESPACE = PluginNamespace.create("cmob");

    public static Codec<CustomMob> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codecs.NAMESPACED_KEY.fieldOf("id").forGetter(CustomMob::id),
            Configuration.CODEC.fieldOf("config").forGetter(CustomMob::configuration)
    ).apply(instance, CustomMob::new));

    public void spawn(Location loc) {
        Bukkit.getServer().getRegionScheduler().run(Core.getInstance(), loc, task -> {
            var rootEntity = loc.getWorld().spawnEntity(loc, EntityType.INTERACTION, false);
            MobUtils.setHealth(rootEntity, this.configuration().stats().get(Stats.MAX_HEALTH));
            rootEntity.getPersistentDataContainer().set(Core.key("id"), PersistentDataType.STRING, this.id.toString());

            if (rootEntity instanceof Interaction le) {
                le.setPersistent(false);
                le.setInteractionWidth(this.configuration().hitbox().width());
                le.setInteractionHeight(this.configuration().hitbox().height());
            }

            var models = new ArrayList<String>();
            for (var model : this.configuration().model()) {
                var itemModel = loc.getWorld().spawnEntity(loc.clone().add(model.offset()).add(0, 0.5, 0).setRotation(0, 0), EntityType.ITEM_DISPLAY);

                if (itemModel instanceof ItemDisplay itemDisplay) {
                    itemDisplay.setBillboard(Display.Billboard.FIXED);
                    itemDisplay.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.FIXED);

                    var item = ItemStack.of(Material.POISONOUS_POTATO);
                    item.setData(DataComponentTypes.ITEM_MODEL, model.itemModel());
                    itemDisplay.setItemStack(item);
                    models.add(itemModel.getUniqueId().toString());

                    itemModel.getPersistentDataContainer()
                            .set(Core.key("children/offset"), PersistentDataType.LIST.doubles(), LocalPDTs.fromVector(model.offset()));
                }
            }
            rootEntity.getPersistentDataContainer()
                    .set(Core.key("children"), PersistentDataType.LIST.strings(), models);
        });
    }

    public record Configuration(
            String name,
            StatsObject stats,
            HitBox hitbox,
            List<Model> model,
            MobEventActions event,
            boolean invulnerable
    ) {
        public static Codec<Configuration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                PrimitiveCodec.STRING.optionalFieldOf("name", "Unnamed").forGetter(Configuration::name),
                StatsObject.CODEC.optionalFieldOf("stats", StatsObject.of()).forGetter(Configuration::stats),
                HitBox.CODEC.fieldOf("hitbox").forGetter(Configuration::hitbox),
                Model.CODEC.listOf().fieldOf("model").forGetter(Configuration::model),
                MobEventActions.CODEC.optionalFieldOf("event", new MobEventActions(new Noop(), new Noop(), new Noop(), new Noop())).forGetter(Configuration::event),
                Codec.BOOL.optionalFieldOf("invulnerable", false).forGetter(Configuration::invulnerable)
        ).apply(instance, Configuration::new));
    }

    public record HitBox(
            float height,
            float width
    ) {
        public static Codec<HitBox> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.FLOAT.fieldOf("height").forGetter(HitBox::height),
                Codec.FLOAT.fieldOf("width").forGetter(HitBox::width)
        ).apply(instance, HitBox::new));
    }

    public record Model(
            NamespacedKey itemModel,
            Vector offset
    ) {
        public static Codec<Model> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codecs.NAMESPACED_KEY.fieldOf("item_model").forGetter(Model::itemModel),
                LocalCodecs.VECTOR.fieldOf("offset").forGetter(Model::offset)
        ).apply(instance, Model::new));
    }
}