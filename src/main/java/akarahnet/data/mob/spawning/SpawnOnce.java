package akarahnet.data.mob.spawning;

import akarahnet.data.mob.CustomMob;
import akarahnet.util.LocalCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;

import java.util.concurrent.atomic.AtomicBoolean;

public record SpawnOnce(Location location, AtomicBoolean spawnedYet) implements SpawnRule {
    public static MapCodec<SpawnOnce> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LocalCodecs.LOCATION.fieldOf("center").forGetter(SpawnOnce::location),
            Codec.BOOL.optionalFieldOf("spawned_yet", false).xmap(AtomicBoolean::new, AtomicBoolean::get).forGetter(SpawnOnce::spawnedYet)
    ).apply(instance, SpawnOnce::new));

    public static SpawnRuleType TYPE = new SpawnRuleType(NamespacedKey.fromString("spawn_once"));

    @Override
    public void trySpawn(CustomMob mob) {
        if (!this.spawnedYet.get()) {
            this.spawnedYet.set(true);

            mob.spawn(location);
        }
    }

    @Override
    public SpawnRuleType getType() {
        return TYPE;
    }
}
