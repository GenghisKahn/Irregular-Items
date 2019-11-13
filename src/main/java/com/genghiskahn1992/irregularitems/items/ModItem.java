package com.genghiskahn1992.irregularitems.items;

import com.genghiskahn1992.irregularitems.IrregularItems;
import net.minecraft.item.Item;

public class ModItem extends Item {

    public ModItem(String name){
        super(new Properties()
                .maxStackSize(1)
                .group(IrregularItems.setup.itemGroup));
        setRegistryName(name);
    }

    public ModItem(String name, Properties properties)
    {
        super(properties.group(IrregularItems.setup.itemGroup));
        setRegistryName(name);
    }

}
