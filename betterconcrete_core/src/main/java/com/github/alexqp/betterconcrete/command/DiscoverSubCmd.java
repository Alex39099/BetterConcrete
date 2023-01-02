/*
 * Copyright (C) 2018-2023 Alexander Schmid
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

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
