package com.github.alexqp.betterconcrete.command;

import com.github.alexqp.commons.command.AlexSubCommand;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DiscoverSubCmd extends AlexSubCommand {

    private final HashSet<NamespacedKey> keys;
    private final BaseComponent success;

    protected DiscoverSubCmd(@NotNull String helpLine, @NotNull AlexSubCommand parent, @NotNull Set<NamespacedKey> keys, @NotNull BaseComponent[] success) {
        super("discover", helpLine, parent);
        this.setIsConsoleCmd(false);
        this.keys = new HashSet<>(keys);
        this.success = this.getPrefixMessage(success);
        this.makeFinal();
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull List<AlexSubCommand> previousCmds, @NotNull List<String> previousExtraArguments, @NotNull String[] args, int startIndex) {
        ((Player) sender).discoverRecipes(keys);
        sendMessage(sender, success);
        return true;
    }
}
