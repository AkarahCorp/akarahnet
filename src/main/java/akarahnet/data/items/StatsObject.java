package akarahnet.data.items;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class StatsObject {
    public static Codec<StatsObject> CODEC = Codec.unboundedMap(
            PrimitiveCodec.STRING.xmap(NamespacedKey::fromString, NamespacedKey::asString),
            PrimitiveCodec.DOUBLE).xmap(StatsObject::new, x -> x.stats);

    HashMap<NamespacedKey, Double> stats;

    StatsObject(Map<NamespacedKey, Double> stats) {
        this.stats = new HashMap<>(stats);
    }

    public static StatsObject of() {
        return new StatsObject(new HashMap<>());
    }

    public void addPositiveStat(ItemLore.Builder lore, String symbol, String name, NamespacedKey key,
                                boolean hideZeroes) {
        if (this.stats.containsKey(key)) {
            var value = this.stats.get(key);
            if (value > 0.0) {
                lore.addLine(
                        Component.text(symbol + " " + name + ": ").color(TextColor.color(175, 175, 175))
                                .append(Component.text("+" + value).color(TextColor.color(0, 255, 0)))
                                .decorations(Map.of(TextDecoration.ITALIC, TextDecoration.State.FALSE)));
            } else if (value < 0.0) {
                lore.addLine(
                        Component.text(symbol + " " + name + ": ").color(TextColor.color(175, 175, 175))
                                .append(Component.text(String.valueOf(value)).color(TextColor.color(255, 0, 0)))
                                .decorations(Map.of(TextDecoration.ITALIC, TextDecoration.State.FALSE)));
            }
        } else if (!hideZeroes) {
            lore.addLine(
                    Component.text(symbol + " " + name + ": ").color(TextColor.color(175, 175, 175))
                            .append(Component.text("+0.0").color(TextColor.color(0, 255, 0)))
                            .decorations(Map.of(TextDecoration.ITALIC, TextDecoration.State.FALSE)));
        }
    }

    public void addStatsToLore(ItemLore.Builder lore, boolean hideZeroes) {
        this.addPositiveStat(lore, "❤", "Max Health", Stats.MAX_HEALTH, hideZeroes);
        this.addPositiveStat(lore, "⸎", "Max Mana", Stats.MAX_MANA, hideZeroes);
        this.addPositiveStat(lore, "🗡", "Attack Damage", Stats.ATTACK_DAMAGE, hideZeroes);
        this.addPositiveStat(lore, "α", "Attack Speed", Stats.ATTACK_SPEED, hideZeroes);
        this.addPositiveStat(lore, "ψ", "Attack Range", Stats.ATTACK_RANGE, hideZeroes);
        this.addPositiveStat(lore, "≈", "Walk Speed", Stats.WALK_SPEED, hideZeroes);


        this.addPositiveStat(lore, "\uD83D\uDEE5", "Teleport Damage", Stats.TELEPORT_DAMAGE, hideZeroes);
        this.addPositiveStat(lore, "\uD83D\uDEE7", "Teleport Range", Stats.TELEPORT_RANGE, hideZeroes);
    }

    public StatsObject add(NamespacedKey stat, double amount) {
        this.stats.put(stat, this.get(stat) + amount);
        return this;
    }

    public StatsObject add(StatsObject other) {
        var setKeys = new HashSet<NamespacedKey>();
        setKeys.addAll(this.stats.keySet());
        setKeys.addAll(other.stats.keySet());
        var obj = StatsObject.of();
        for (var key : setKeys) {
            obj.add(key, this.get(key) + other.get(key));
        }
        return obj;
    }

    public double get(NamespacedKey key) {
        return this.stats.getOrDefault(key, 0.0);
    }

    @Override
    public String toString() {
        return this.stats.toString();
    }
}
