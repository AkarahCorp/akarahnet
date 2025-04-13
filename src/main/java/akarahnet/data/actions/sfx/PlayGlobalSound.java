package akarahnet.data.actions.sfx;

import akarahnet.Core;
import akarahnet.data.actions.casting.LocationValue;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.akarah.actions.Environment;
import dev.akarah.actions.steps.Action;
import dev.akarah.actions.steps.ActionType;
import dev.akarah.pluginpacks.Codecs;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;

public record PlayGlobalSound(LocationValue location, NamespacedKey sound) implements Action {
    public static MapCodec<PlayGlobalSound> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LocationValue.CODEC.fieldOf("location").forGetter(PlayGlobalSound::location),
            Codecs.NAMESPACED_KEY.fieldOf("sound").forGetter(PlayGlobalSound::sound)
    ).apply(instance, PlayGlobalSound::new));

    public static ActionType TYPE = new ActionType(NamespacedKey.fromString("sfx/play_sound"));

    @Override
    public void execute(Environment environment) {
        var loc = environment.resolve(this.location);
        Bukkit.getRegionScheduler().execute(Core.getInstance(), loc, () -> {
            var snd = Sound.sound()
                    .type(this.sound)
                    .volume(1)
                    .pitch(1)
                    .seed((int) (Math.random() * 10000))
                    .build();
            var entities = loc.getNearbyEntities(10.0, 10.0, 10.0);
            for (var nearby : entities) {
                nearby.playSound(snd);
            }
        });
    }

    @Override
    public ActionType getType() {
        return TYPE;
    }
}
