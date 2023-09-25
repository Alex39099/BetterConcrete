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

package com.github.alexqp.betterconcrete.main;

import com.github.alexqp.betterconcrete.command.BetterConcreteCommand;
import com.github.alexqp.betterconcrete.listener.CauldronItemDropListener;
import com.github.alexqp.commons.bstats.bukkit.Metrics;
import com.github.alexqp.betterconcrete.listener.RecipeDiscoverJoinListener;
import com.github.alexqp.commons.messages.ConsoleMessage;
import com.google.common.collect.Range;
import com.github.alexqp.commons.config.ConfigChecker;
import com.github.alexqp.commons.config.ConsoleErrorType;
import com.github.alexqp.commons.messages.Debugable;
import com.jeff_media.updatechecker.UpdateCheckSource;
import com.jeff_media.updatechecker.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Level;

public class BetterConcrete extends JavaPlugin implements Debugable {

    @Override
    public boolean getDebug() {
        return false;
    }

    private final String[] furnaceConfigNames = {"furnace_recipes", "add_recipes_on_login", "enable", "exp_amount", "cooking_time"};
    private final String[] cauldronConfigNames = {"cauldron_mechanic", "enable", "check_empty", "change_waterlevel", "max_stack_size"};

    private static final String defaultInternalsVersion = "v1_20_R1";
    private static InternalsProvider internals;
    static {
        try {
            String packageName = BetterConcrete.class.getPackage().getName();
            String internalsName = getInternalsName(Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3]);
            if (internalsName.equals(defaultInternalsVersion)) {
                Bukkit.getLogger().log(Level.INFO, BetterConcrete.class.getSimpleName() + " is using the latest implementation (last tested for " + defaultInternalsVersion + ").");
                internals = new InternalsProvider();
            } else {
                Bukkit.getLogger().log(Level.INFO, BetterConcrete.class.getSimpleName() + " is using the implementation for version " + internalsName + ".");
                internals = (InternalsProvider) Class.forName(packageName + "." + internalsName).getDeclaredConstructor().newInstance();
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | ClassCastException | NoSuchMethodException | InvocationTargetException exception) {
            Bukkit.getLogger().log(Level.WARNING, BetterConcrete.class.getSimpleName() + " could not find an updated implementation for this server version. " +
                    "However the plugin is trying to use the latest implementation which should work if Minecraft did not change drastically (last tested version: " + defaultInternalsVersion + ").");            internals = new InternalsProvider();
        }
    }

    private static String getInternalsName(String internalsName) {
        Map<String, String> internalsVersions = new HashMap<>();
        internalsVersions.put("v1_13_R1", "v1_16_R3");
        internalsVersions.put("v1_13_R2", "v1_16_R3");
        internalsVersions.put("v1_14_R1", "v1_16_R3");
        internalsVersions.put("v1_15_R1", "v1_16_R3");
        internalsVersions.put("v1_16_R1", "v1_16_R3");
        internalsVersions.put("v1_16_R2", "v1_16_R3");
        internalsVersions.put("v1_16_R3", "v1_16_R3");
        return internalsVersions.getOrDefault(internalsName, defaultInternalsVersion);
    }

    @Override
    public void onEnable() {
        new Metrics(this, 3196);
        this.saveDefaultConfig();
        this.getLogger().info("This plugin was made by alex_qp");

        ConfigChecker configChecker = new ConfigChecker(this);
        HashSet<NamespacedKey> keys = new HashSet<>();

        // FURNACE RECIPE
        ConfigurationSection section = configChecker.checkConfigSection(this.getConfig(), furnaceConfigNames[0], ConsoleErrorType.ERROR);
        if (section != null) {

            ConfigurationSection subSection = configChecker.checkConfigSection(section, "concrete_to_powder", ConsoleErrorType.ERROR);
            if (subSection != null && configChecker.checkBoolean(subSection, furnaceConfigNames[2], ConsoleErrorType.WARN, true)) {
                this.getLogger().info("furnace recipes concrete_to_powder were enabled in config.");
                float exp = (float) configChecker.checkDouble(subSection, furnaceConfigNames[3], ConsoleErrorType.WARN, 0.1);
                int cookingTime = configChecker.checkInt(subSection, furnaceConfigNames[4], ConsoleErrorType.WARN, 200);
                keys = internals.addFurnaceRecipesColorColor(this, "CONCRETE", "CONCRETE_POWDER", exp, cookingTime);
            }

            subSection = configChecker.checkConfigSection(section, "powder_to_glass", ConsoleErrorType.ERROR);
            if (subSection != null && configChecker.checkBoolean(subSection, furnaceConfigNames[2], ConsoleErrorType.WARN, true)) {
                this.getLogger().info("furnace recipes powder_to_glass were enabled in config.");
                float exp = (float) configChecker.checkDouble(subSection, furnaceConfigNames[3], ConsoleErrorType.WARN, 0.1);
                int cookingTime = configChecker.checkInt(subSection, furnaceConfigNames[4], ConsoleErrorType.WARN, 200);
                keys = internals.addFurnaceRecipesColorColor(this, "CONCRETE_POWDER", "STAINED_GLASS", exp, cookingTime);
            }

            if (!keys.isEmpty() && configChecker.checkBoolean(section, furnaceConfigNames[1], ConsoleErrorType.WARN, false)) {
                Bukkit.getServer().getPluginManager().registerEvents(new RecipeDiscoverJoinListener(keys), this);
                ConsoleMessage.debug((Debugable) this, "registered RecipeDiscoverJoinListener");
            }
        }

        // CAULDRON MECHANIC
        section = configChecker.checkConfigSection(this.getConfig(), cauldronConfigNames[0], ConsoleErrorType.ERROR);
        if (section != null) {

            if (configChecker.checkBoolean(section, cauldronConfigNames[1], ConsoleErrorType.WARN, false)) {
                this.getLogger().info("cauldron mechanic was enabled in config.");

                int checkEmpty = configChecker.checkInt(section, cauldronConfigNames[2], ConsoleErrorType.WARN, 1, Range.closed(0, 2));
                int changeWater = configChecker.checkInt(section, cauldronConfigNames[3], ConsoleErrorType.WARN, 1, Range.closed(0, 3));
                int maxStackSize = configChecker.checkInt(section, cauldronConfigNames[4], ConsoleErrorType.WARN, 64, Range.closed(1, 64));

                CauldronItemDropListener cauldronListener = new CauldronItemDropListener(this, internals, checkEmpty, changeWater, maxStackSize);
                Bukkit.getPluginManager().registerEvents(cauldronListener, this);
                ConsoleMessage.debug((Debugable) this, "registered CauldronItemDropListener");
            }
        }

        new BetterConcreteCommand(this, keys).register();
        this.updateChecker();
    }

    private void updateChecker() {
        int spigotResourceID = 58276;
        ConfigChecker configChecker = new ConfigChecker(this);
        ConfigurationSection updateCheckerSection = configChecker.checkConfigSection(this.getConfig(), "updatechecker", ConsoleErrorType.ERROR);
        if (updateCheckerSection != null && configChecker.checkBoolean(updateCheckerSection, "enable", ConsoleErrorType.WARN, true)) {
            ConsoleMessage.debug((Debugable) this, "enabled UpdateChecker");

            new UpdateChecker(this, UpdateCheckSource.SPIGOT, String.valueOf(spigotResourceID))
                    .setDownloadLink(spigotResourceID)
                    .setChangelogLink("https://www.spigotmc.org/resources/" + spigotResourceID + "/updates")
                    .setDonationLink("https://paypal.me/alexqpplugins")
                    .setNotifyOpsOnJoin(configChecker.checkBoolean(updateCheckerSection, "notify_op_on_login", ConsoleErrorType.WARN, true))
                    .setNotifyByPermissionOnJoin("betterconcrete.updatechecker")
                    .checkEveryXHours(24).checkNow();
        }
    }
}
