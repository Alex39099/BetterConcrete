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

import com.github.alexqp.betterconcrete.main.BetterConcrete;
import com.github.alexqp.commons.command.AlexCommand;
import com.github.alexqp.commons.config.ConfigChecker;
import com.github.alexqp.commons.config.ConsoleErrorType;
import com.github.alexqp.commons.messages.MessageTranslator;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;

public class BetterConcreteCommand extends AlexCommand {

    public BetterConcreteCommand(@NotNull BetterConcrete plugin, Set<NamespacedKey> keysToDiscover) {
        super("betterconcrete", plugin, ChatColor.BLUE);

        ConfigChecker configChecker = new ConfigChecker(plugin);
        ConfigurationSection msgSection = configChecker.checkConfigSection(plugin.getConfig(), "messages", ConsoleErrorType.ERROR);

        if (msgSection != null) {
            this.addCreditLine(MessageTranslator.translateBukkitColorCodes(Objects.requireNonNull(configChecker.checkString(msgSection, "credits", ConsoleErrorType.WARN, "Use /betterconcrete help for all available commands."))));
            this.setNoPermissionLine(MessageTranslator.translateBukkitColorCodes(Objects.requireNonNull(configChecker.checkString(msgSection, "noPerm", ConsoleErrorType.WARN, "&4You do not have permission."))));
            this.setUsagePrefix(MessageTranslator.translateBukkitColorCodes(Objects.requireNonNull(configChecker.checkString(msgSection, "wrongCmdUsagePrefix", ConsoleErrorType.WARN, "&CUsage:"))));

            String discoverHelpLine = "discovers all available recipes.";
            String discoverSuccess = "&2You discovered all available recipes.";

            ConfigurationSection section = configChecker.checkConfigSection(msgSection, "discover", ConsoleErrorType.ERROR);
            if (section != null) {
                discoverHelpLine = configChecker.checkString(section, "help", ConsoleErrorType.WARN, discoverHelpLine);
                discoverSuccess = configChecker.checkString(section, "success", ConsoleErrorType.WARN, discoverSuccess);
            }

            assert discoverHelpLine != null;
            assert discoverSuccess != null;
            this.addSubCmds(new DiscoverSubCmd(discoverHelpLine, this, keysToDiscover, MessageTranslator.translateBukkitColorCodes(discoverSuccess)));

        }
    }
}
