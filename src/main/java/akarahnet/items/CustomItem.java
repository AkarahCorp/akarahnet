package akarahnet.items;

import akarahnet.Core;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.akarah.pluginpacks.data.PluginNamespace;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public record CustomItem(
        String name,
        Optional<String> description,
        Material material,
        int stars,
        Optional<StatsObject> stats
) {
    public static PluginNamespace<CustomItem> NAMESPACE = PluginNamespace.create("citem");

    public static Codec<CustomItem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            PrimitiveCodec.STRING.fieldOf("name").forGetter(CustomItem::name),
            PrimitiveCodec.STRING.optionalFieldOf("description").forGetter(CustomItem::description),
            PrimitiveCodec.STRING.xmap(x -> Material.getMaterial(x.toUpperCase()), Material::name).fieldOf("material").forGetter(CustomItem::material),
            PrimitiveCodec.INT.fieldOf("stars").forGetter(CustomItem::stars),
            StatsObject.CODEC.optionalFieldOf("stats").forGetter(CustomItem::stats)
    ).apply(instance, CustomItem::new));
    static Component[] STARS_LIST = {
            Component.text("✪").color(TextColor.color(255, 255, 0))
                    .append(Component.text("✪✪✪✪").color(TextColor.color(133, 133, 133))),

            Component.text("✪✪").color(TextColor.color(255, 255, 0))
                    .append(Component.text("✪✪✪").color(TextColor.color(133, 133, 133))),

            Component.text("✪✪✪").color(TextColor.color(255, 255, 0))
                    .append(Component.text("✪✪").color(TextColor.color(133, 133, 133))),

            Component.text("✪✪✪✪").color(TextColor.color(255, 255, 0))
                    .append(Component.text("✪").color(TextColor.color(133, 133, 133))),

            Component.text("✪✪✪✪✪").color(TextColor.color(255, 255, 0))
                    .append(Component.text("").color(TextColor.color(133, 133, 133))),

    };

    public ItemStack toItemStack() {
        var item = ItemStack.of(this.material());
        item.setData(DataComponentTypes.ITEM_NAME, Component.text(this.name + " ").append(STARS_LIST[this.stars - 1]));

        var lore = ItemLore.lore();
        this.stats.ifPresent(stats -> stats.addStatsToLore(lore, true));
        item.setData(DataComponentTypes.LORE, ItemLore.lore());
        item.editMeta(meta -> {
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addAttributeModifier(Attribute.ATTACK_SPEED, new AttributeModifier(Core.key("normal_combat"), 1024.0, AttributeModifier.Operation.ADD_NUMBER));
        });
        item.setData(DataComponentTypes.LORE, lore);

        return item;
    }
}
