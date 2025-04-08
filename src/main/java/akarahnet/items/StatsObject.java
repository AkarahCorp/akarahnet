package akarahnet.items;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;

import java.util.HashMap;
import java.util.Map;

public class StatsObject {
    public static Codec<StatsObject> CODEC = Codec.unboundedMap(
            PrimitiveCodec.STRING.xmap(NamespacedKey::fromString, NamespacedKey::asString),
            PrimitiveCodec.DOUBLE
    ).xmap(StatsObject::new, x -> x.stats);

    public static NamespacedKey MAX_HEALTH = NamespacedKey.minecraft("max_health");
    public static NamespacedKey MAX_MANA = NamespacedKey.minecraft("max_mana");
    public static NamespacedKey ATTACK_DAMAGE = NamespacedKey.minecraft("attack_damage");
    public static NamespacedKey ATTACK_SPEED = NamespacedKey.minecraft("attack_speed");

    HashMap<NamespacedKey, Double> stats;

    StatsObject(Map<NamespacedKey, Double> stats) {
        this.stats = new HashMap<>(stats);
    }

    public static StatsObject of() {
        return new StatsObject(new HashMap<>());
    }

    public void addPositiveStat(ItemLore.Builder lore, String symbol, String name, NamespacedKey key, boolean hideZeroes) {
        if (this.stats.containsKey(key)) {
            var value = this.stats.get(key);
            if (value > 0.0) {
                lore.addLine(
                        Component.text(symbol + " " + name + ": ").color(TextColor.color(175, 175, 175))
                                .append(Component.text("+" + value).color(TextColor.color(0, 255, 0)))
                                .decorations(Map.of(TextDecoration.ITALIC, TextDecoration.State.FALSE))
                );
            } else if (value < 0.0) {
                lore.addLine(
                        Component.text(symbol + " " + name + ": ").color(TextColor.color(175, 175, 175))
                                .append(Component.text(String.valueOf(value)).color(TextColor.color(255, 0, 0)))
                                .decorations(Map.of(TextDecoration.ITALIC, TextDecoration.State.FALSE))
                );
            }
        } else if (!hideZeroes) {
            lore.addLine(
                    Component.text(symbol + " " + name + ": ").color(TextColor.color(175, 175, 175))
                            .append(Component.text("+0.0").color(TextColor.color(0, 255, 0)))
                            .decorations(Map.of(TextDecoration.ITALIC, TextDecoration.State.FALSE))
            );
        }
    }

    public void addStatsToLore(ItemLore.Builder lore, boolean hideZeroes) {
        this.addPositiveStat(lore, "❤", "Max Health", StatsObject.MAX_HEALTH, hideZeroes);
        this.addPositiveStat(lore, "⸎", "Max Mana", StatsObject.MAX_MANA, hideZeroes);
        this.addPositiveStat(lore, "🗡", "Attack Damage", StatsObject.ATTACK_DAMAGE, hideZeroes);
        this.addPositiveStat(lore, "α", "Attack Speed", StatsObject.ATTACK_SPEED, hideZeroes);
    }
}
