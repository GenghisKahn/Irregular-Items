package com.genghiskahn1992.irregularitems.utilites;

import com.genghiskahn1992.irregularitems.IrregularItems;
import com.genghiskahn1992.irregularitems.capabilities.CapabilityQuantumEnergy;
import com.genghiskahn1992.irregularitems.items.ModItems;
import com.genghiskahn1992.irregularitems.network.IrregularItemPacketHandler;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModSetup {


    public ItemGroup itemGroup = new ItemGroup(IrregularItems.MODID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.EZBUILDERITEM);
        }
    };

    public void init() {
        IrregularItemPacketHandler.register();

        CapabilityQuantumEnergy.register();
    }
}
