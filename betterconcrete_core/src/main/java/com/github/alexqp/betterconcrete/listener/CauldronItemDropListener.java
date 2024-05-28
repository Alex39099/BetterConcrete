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

package com.github.alexqp.betterconcrete.listener;

import com.github.alexqp.betterconcrete.main.InternalsProvider;
import com.github.alexqp.commons.messages.ConsoleMessage;
import com.google.common.collect.Range;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Objects;

public class CauldronItemDropListener implements Listener {

    private final JavaPlugin plugin;
    private final InternalsProvider internals;
    private final int checkEmpty;
    private final int changeWater;
    private final int maxStackSize;

    private final HashMap<Item, BukkitRunnable> cauldronDrops = new HashMap<>();

    public CauldronItemDropListener(JavaPlugin plugin, InternalsProvider internals, int checkEmpty, int changeWater, int maxStackSize)
        throws IllegalArgumentException {
        this.plugin = plugin;
        this.internals = internals;

        if (!Range.closed(0, 2).contains(checkEmpty))
            throw new IllegalArgumentException("checkEmpty must be between 0 and 2 (inclusive)");
        this.checkEmpty = checkEmpty;

        if (changeWater < 0)
            throw new IllegalArgumentException("changeWater must be positive");
        this.changeWater = changeWater;

        if (!Range.closed(1, 64).contains(maxStackSize))
            throw new IllegalArgumentException("maxStackSize must be between 1 and 64 (inclusive)");
        this.maxStackSize = maxStackSize;

    }

    @EventHandler(ignoreCancelled = true)
    private void onItemDrop(PlayerDropItemEvent e) {
        String dropItemType = e.getItemDrop().getItemStack().getType().name();
        if (!dropItemType.contains("CONCRETE_POWDER"))
            return;

        if (!e.getPlayer().hasPermission("betterconcrete.cauldron")) {
            ConsoleMessage.debug(this.getClass(), plugin, "player is not permitted to use cauldron mechanic.");
            return;
        }

        this.initiateTransformation(e.getItemDrop());
        ConsoleMessage.debug(this.getClass(), plugin, "Scheduled itemStack to be transformed.");
    }

    private void initiateTransformation(Item drop) {
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                ConsoleMessage.debug(CauldronItemDropListener.class, plugin, "Starting transformation...");
                Block cauldron = drop.getLocation().getBlock();
                if (cauldron.getBlockData().getMaterial().equals(internals.getWaterCauldron())) {
                    ConsoleMessage.debug(CauldronItemDropListener.class, plugin, "Item was thrown into a cauldron!");
                    Levelled cauldronData = (Levelled) cauldron.getBlockData();

                    int neededWater;
                    if (checkEmpty == 2)
                        neededWater = changeWater;
                    else
                        neededWater = checkEmpty;

                    ItemStack concrete = new ItemStack(drop.getItemStack());
                    concrete.setType(Material.valueOf(drop.getItemStack().getType().name().replace("_POWDER", "")));
                    concrete.setAmount(0);

                    int powderAmount, water;
                    for (powderAmount = drop.getItemStack().getAmount(), water = cauldronData.getLevel(); powderAmount > 0 && neededWater <= Math.max(0, water); water = water - changeWater) {
                        int transformAmount = Math.min(powderAmount, maxStackSize);
                        powderAmount = powderAmount - transformAmount;
                        concrete.setAmount(concrete.getAmount() + transformAmount);
                    }

                    if (water <= 0) {
                        internals.emptyCauldron(cauldron);
                    } else {
                        cauldronData.setLevel(water);
                        cauldron.setBlockData(cauldronData);
                    }

                    if (concrete.getAmount() > 0) {
                        ItemStack powder = new ItemStack(drop.getItemStack());
                        drop.setItemStack(concrete);
                        if (powderAmount > 0) {
                            powder.setAmount(powderAmount);
                            Objects.requireNonNull(drop.getLocation().getWorld()).dropItem(drop.getLocation(), powder);
                        }
                    }
                } else {
                    ConsoleMessage.debug(CauldronItemDropListener.class, plugin, "Item was NOT thrown into cauldron but " + drop.getLocation().getBlock().getBlockData().getMaterial().name());
                }
            }
        };
        task.runTaskLater(plugin, 20);
        cauldronDrops.put(drop, task);
    }

    private boolean cancelTransformation(Item item) {
        BukkitRunnable task = cauldronDrops.remove(item);
        if (task != null) {
            task.cancel();
            return true;
        }
        return false;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onDropMerge(ItemMergeEvent e) {
        boolean reschedule = this.cancelTransformation(e.getEntity());
        if (this.cancelTransformation(e.getTarget()) || reschedule) {
            this.initiateTransformation(e.getTarget());
            ConsoleMessage.debug(this.getClass(), plugin, "Transformation was rescheduled because of itemMerge.");
        }
    }
}
