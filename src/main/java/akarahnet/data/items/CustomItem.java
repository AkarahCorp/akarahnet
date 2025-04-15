package akarahnet.data.items;

import akarahnet.Core;
import akarahnet.data.items.stats.StatsObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.akarah.pluginpacks.Codecs;
import dev.akarah.pluginpacks.data.PluginNamespace;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ColorableArmorMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Optional;

public record CustomItem(
        NamespacedKey id,
        Optional<StatsObject> stats,
        VisualData visualData,
        Optional<ItemEventHandlers> event
) {
    public static PluginNamespace<CustomItem> NAMESPACE = PluginNamespace.create("citem");

    public static Codec<CustomItem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codecs.NAMESPACED_KEY.fieldOf("id").forGetter(CustomItem::id),
            StatsObject.CODEC.optionalFieldOf("stats").forGetter(CustomItem::stats),
            VisualData.CODEC.fieldOf("visual").forGetter(CustomItem::visualData),
            ItemEventHandlers.CODEC.optionalFieldOf("event").forGetter(CustomItem::event)
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
        var item = ItemStack.of(this.visualData.material);
        item.setData(DataComponentTypes.ITEM_NAME, Component.text(this.visualData.name + " ").append(STARS_LIST[this.visualData.stars - 1]));

        var lore = ItemLore.lore();
        this.stats.ifPresent(stats -> stats.addStatsToLore(lore, true));
        item.editMeta(meta -> {
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addAttributeModifier(Attribute.ATTACK_SPEED, new AttributeModifier(Core.key("normal_combat"), 1024.0, AttributeModifier.Operation.ADD_NUMBER));
            if (meta instanceof ColorableArmorMeta colorableArmorMeta && this.visualData.itemColor.isPresent()) {
                colorableArmorMeta.setColor(this.visualData.itemColor.get());
                colorableArmorMeta.addItemFlags(ItemFlag.HIDE_DYE);
            }
            meta.lore(lore.build().lines());
            meta.setUnbreakable(true);
            meta.getPersistentDataContainer().set(Core.key("id"), PersistentDataType.STRING, this.id.asString());
        });

        return item;
    }

    record VisualData(
            Material material,
            String name,
            Optional<String> description,
            int stars,
            Optional<Color> itemColor
    ) {
        public static Codec<VisualData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codecs.MATERIAL.fieldOf("material").forGetter(VisualData::material),
                PrimitiveCodec.STRING.fieldOf("name").forGetter(VisualData::name),
                PrimitiveCodec.STRING.optionalFieldOf("description").forGetter(VisualData::description),
                PrimitiveCodec.INT.fieldOf("stars").forGetter(VisualData::stars),
                Codecs.COLOR.optionalFieldOf("color").forGetter(VisualData::itemColor)
        ).apply(instance, VisualData::new));
    }
}
