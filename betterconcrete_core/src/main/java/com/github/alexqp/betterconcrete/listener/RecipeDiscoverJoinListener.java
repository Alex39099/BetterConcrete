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

package com.github.alexqp.betterconcrete.listener;

import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;


import java.util.HashSet;

public class RecipeDiscoverJoinListener implements Listener {

    private final HashSet<NamespacedKey> allKeys;

    public RecipeDiscoverJoinListener(HashSet<NamespacedKey> allKeys) {
        this.allKeys = allKeys;
    }

    @SuppressWarnings({"unused"})
    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent e) {
        e.getPlayer().discoverRecipes(allKeys);
    }
}
