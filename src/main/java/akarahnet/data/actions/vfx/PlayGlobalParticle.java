package akarahnet.data.actions.vfx;

import akarahnet.Core;
import akarahnet.data.actions.casting.LocationValue;
import akarahnet.util.LocalCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.akarah.actions.Environment;
import dev.akarah.actions.steps.Action;
import dev.akarah.actions.steps.ActionType;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;

public record PlayGlobalParticle(LocationValue location, Color color, int count, double spread) implements Action {
    public static MapCodec<PlayGlobalParticle> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LocationValue.CODEC.fieldOf("location").forGetter(PlayGlobalParticle::location),
            LocalCodecs.COLOR.fieldOf("color").forGetter(PlayGlobalParticle::color),
            Codec.INT.optionalFieldOf("count", 0).forGetter(PlayGlobalParticle::count),
            Codec.DOUBLE.optionalFieldOf("spread", 0.0).forGetter(PlayGlobalParticle::spread)
    ).apply(instance, PlayGlobalParticle::new));

    public static ActionType TYPE = new ActionType(NamespacedKey.fromString("vfx/play_particle"));

    @Override
    public void execute(Environment environment) {
        var loc = environment.resolve(this.location);
        Bukkit.getRegionScheduler().execute(Core.getInstance(), loc, () -> {
            Particle.DUST.builder()
                    .count(this.count)
                    .offset(this.spread, this.spread, this.spread)
                    .color(this.color)
                    .location(loc)
                    .receivers(20)
                    .spawn();
        });
    }

    @Override
    public ActionType getType() {
        return TYPE;
    }
}
