package akarahnet.data.mob.model;

import com.mojang.serialization.MapCodec;
import dev.akarah.pluginpacks.Codecs;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;

public record CustomModel(
        NamespacedKey itemModel
) implements MobModel {
    public static MobModelType TYPE = new MobModelType(NamespacedKey.fromString("custom"));
    public static MapCodec<CustomModel> CODEC = Codecs.NAMESPACED_KEY.fieldOf("item_model").xmap(CustomModel::new, CustomModel::itemModel);

    @Override
    public MobModelType getType() {
        return TYPE;
    }

    @Override
    public Entity spawnChild(Location loc) {
        var itemModel = loc.getWorld().spawnEntity(loc, EntityType.ITEM_DISPLAY);

        if (itemModel instanceof ItemDisplay itemDisplay) {
            itemDisplay.setBillboard(Display.Billboard.FIXED);
            itemDisplay.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.FIXED);

            var item = ItemStack.of(Material.POISONOUS_POTATO);
            item.setData(DataComponentTypes.ITEM_MODEL, this.itemModel());
            itemDisplay.setItemStack(item);

        }

        return itemModel;
    }
}
