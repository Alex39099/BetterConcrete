/*
 * Copyright (C) 2018-2024 Alexander Schmid
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

import com.github.alexqp.commons.messages.ConsoleMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class InternalsProvider {

    private HashSet<String> colorSet;

    public InternalsProvider() {
        this.createColorSet();
    }

    private void createColorSet() {
        colorSet = new HashSet<>();
        colorSet.add("BLACK_");
        colorSet.add("BLUE_");
        colorSet.add("BROWN_");
        colorSet.add("CYAN_");
        colorSet.add("GREEN_");
        colorSet.add("LIGHT_BLUE_");
        colorSet.add("LIGHT_GRAY_");
        colorSet.add("GRAY_");
        colorSet.add("LIME_");
        colorSet.add("MAGENTA_");
        colorSet.add("ORANGE_");
        colorSet.add("PINK_");
        colorSet.add("PURPLE_");
        colorSet.add("RED_");
        colorSet.add("WHITE_");
        colorSet.add("YELLOW_");
    }

    /**
     * Adds for every color a recipe to cook color_matName (source) into color_cookedMatName
     * @param plugin the plugin
     * @param matName the source material which gets cooked
     * @param cookedMatName the result
     * @param exp the experience
     * @param speed the cooking speed
     * @return a HashSet containing all NamespacedKeys
     */
    protected HashSet<NamespacedKey> addFurnaceRecipesColorColor(JavaPlugin plugin, String matName, String cookedMatName, float exp, int speed) {
        HashSet<NamespacedKey> keys = new HashSet<>();
        for (String color : colorSet) {

            String colorMatName = color + matName;
            String colorCookedMatName = color + cookedMatName;
            if ((Material.matchMaterial(colorMatName) == null) || (Material.matchMaterial(colorCookedMatName) == null)) {
                ConsoleMessage.debug(this.getClass(), plugin, "checkMaterialName failed for " + colorMatName + " or " + colorCookedMatName);
                continue;
            }

            NamespacedKey key = new NamespacedKey(plugin, colorMatName);
            Material source = Material.valueOf(colorMatName);
            ItemStack result = new ItemStack(Material.valueOf(colorCookedMatName));

            FurnaceRecipe recipe = new FurnaceRecipe(key, result, source, exp, speed);

            Bukkit.getServer().addRecipe(recipe);
            keys.add(key);
        }
        return keys;
    }

    public Material getWaterCauldron() {
        return Material.WATER_CAULDRON;
    }

    public void emptyCauldron(@NotNull Block cauldron) {
        cauldron.setType(Material.CAULDRON);
    }
}
