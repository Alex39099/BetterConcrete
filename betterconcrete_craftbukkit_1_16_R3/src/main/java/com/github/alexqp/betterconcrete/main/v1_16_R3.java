package com.github.alexqp.betterconcrete.main;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.jetbrains.annotations.NotNull;

public class v1_16_R3 extends InternalsProvider {

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
