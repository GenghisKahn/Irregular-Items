package com.genghiskahn1992.irregularitems.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class ModBlock extends Block {

    public ModBlock(String name){
        super(Properties.create(Material.IRON)
                .sound(SoundType.METAL)
                .hardnessAndResistance(2.0f)
        );

        setRegistryName(name);
    }

    public ModBlock(String name, Properties properties){
        super(properties);

        setRegistryName(name);
    }
}
