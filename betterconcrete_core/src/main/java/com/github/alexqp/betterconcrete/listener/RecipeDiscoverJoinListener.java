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
