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

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.jetbrains.annotations.NotNull;

public class v1_16_R2 extends InternalsProvider {

    @Override
    public Material getWaterCauldron() {
        return Material.CAULDRON;
    }

    @Override
    public void emptyCauldron(@NotNull Block cauldron) {
        BlockData cauldronData = cauldron.getBlockData();
        if (cauldronData instanceof Levelled) {
            ((Levelled) cauldronData).setLevel(0);
            cauldron.setBlockData(cauldronData);
        }
    }
}
