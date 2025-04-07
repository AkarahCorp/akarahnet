package akarahnet.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.akarah.pluginpacks.data.PackRepository;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public class RegistryArgumentType<T> implements CustomArgumentType<T, NamespacedKey> {
    PackRepository.RegistryInstance<T> instance;

    public static <T> RegistryArgumentType<T> forRegistry(PackRepository.RegistryInstance<T> instance) {
        var t = new RegistryArgumentType<T>();
        t.instance = instance;
        return t;
    }

    @Override
    public @NotNull T parse(@NotNull StringReader stringReader) throws CommandSyntaxException {
        var key = ArgumentTypes.namespacedKey().parse(stringReader);
        var message = MessageComponentSerializer.message().serialize(Component.text("Not a valid entry in registry."));
        return this.instance.get(key).orElseThrow(() -> new CommandSyntaxException(new SimpleCommandExceptionType(message), message));
    }

    @Override
    public @NotNull ArgumentType<NamespacedKey> getNativeType() {
        return ArgumentTypes.namespacedKey();
    }
}
