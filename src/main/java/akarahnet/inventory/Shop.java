package akarahnet.inventory;

import akarahnet.data.items.CustomItem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.akarah.pluginpacks.Codecs;
import dev.akarah.pluginpacks.data.PackRepository;
import dev.akarah.pluginpacks.data.PluginNamespace;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record Shop(
        List<ShopEntry> entries
) implements InventoryHolder {
    public static PluginNamespace<Shop> NAMESPACE = PluginNamespace.create("shop");

    public static Codec<Shop> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ShopEntry.CODEC.listOf().fieldOf("entries").forGetter(Shop::entries)
    ).apply(instance, Shop::new));

    @Override
    public @NotNull Inventory getInventory() {
        var inv = Bukkit.getServer().createInventory(this, 54);

        int idx = 0;
        for (var entry : this.entries()) {
            var entryItem = PackRepository.getInstance().getRegistry(CustomItem.NAMESPACE)
                    .flatMap(reg -> reg.get(entry.item()))
                    .map(CustomItem::toItemStack)
                    .orElseThrow();
            inv.setItem(idx, entryItem);
            idx += 1;
        }
        return inv;
    }


    record ShopEntry(
            NamespacedKey item,
            List<PriceEntry> cost
    ) {
        public static Codec<ShopEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codecs.NAMESPACED_KEY.fieldOf("item").forGetter(ShopEntry::item),
                PriceEntry.CODEC.listOf().fieldOf("cost").forGetter(ShopEntry::cost)
        ).apply(instance, ShopEntry::new));
    }

    record PriceEntry(
            NamespacedKey item,
            int amount
    ) {
        public static Codec<PriceEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codecs.NAMESPACED_KEY.fieldOf("item").forGetter(PriceEntry::item),
                Codec.INT.fieldOf("amount").forGetter(PriceEntry::amount)
        ).apply(instance, PriceEntry::new));
    }
}
