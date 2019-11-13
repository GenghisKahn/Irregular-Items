package com.genghiskahn1992.irregularitems.items;

import com.genghiskahn1992.irregularitems.IrregularItems;
import com.genghiskahn1992.irregularitems.blocks.ModBlocks;
import com.genghiskahn1992.irregularitems.containers.EZBuilderUpgradeContainer;
import com.genghiskahn1992.irregularitems.inventory.ModItemInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ObjectHolder;

public class ModItems {

    public static void registerItems(final RegistryEvent.Register<Item> event){
        Item.Properties properties = new Item.Properties().group(IrregularItems.setup.itemGroup);
        event.getRegistry().register(new BlockItem(ModBlocks.FIRSTBLOCK, properties).setRegistryName(ModBlocks.FIRSTBLOCK.getRegistryName()));
        event.getRegistry().register(new BlockItem(ModBlocks.SCHEMATICWRITER_BLOCK, properties).setRegistryName(ModBlocks.SCHEMATICWRITER_BLOCK.getRegistryName()));
        event.getRegistry().register(new BlockItem(ModBlocks.COMBUSTIONGENERATOR_BLOCK, properties).setRegistryName(ModBlocks.COMBUSTIONGENERATOR_BLOCK.getRegistryName()));
        event.getRegistry().register(new BlockItem(ModBlocks.BASICQUANTUMGENERATOR_BLOCK, properties).setRegistryName(ModBlocks.BASICQUANTUMGENERATOR_BLOCK.getRegistryName()));
        event.getRegistry().register(new BlockItem(ModBlocks.QUANTUMCAPACITOR_BLOCK, properties).setRegistryName(ModBlocks.QUANTUMCAPACITOR_BLOCK.getRegistryName()));

        event.getRegistry().register(new FirstItem());
        event.getRegistry().register(new EZBuilderItem());
        event.getRegistry().register(new EZBuilderUpgradeItem());
        event.getRegistry().register(new SchematicItem());
    }

    public static void registerItemContainers(final RegistryEvent.Register<ContainerType<?>> event){
        event.getRegistry().register(IForgeContainerType.create(((windowId, inv, data) -> {
            return new EZBuilderUpgradeContainer(windowId, inv, new ModItemInventory(IrregularItems.proxy.getClientPlayer().getHeldItemMainhand(), 9), (EZBuilderUpgradeItem) IrregularItems.proxy.getClientPlayer().getHeldItemMainhand().getItem());
        })).setRegistryName(ModItems.EZBUILDERUPGRADEITEM.getRegistryName()));
    }

    @ObjectHolder(IrregularItems.MODID+":firstitem")
    public static FirstItem FIRSTITEM;

    //Items
    @ObjectHolder(IrregularItems.MODID+":ezbuilderitem")
    public static EZBuilderItem EZBUILDERITEM;
    @ObjectHolder(IrregularItems.MODID+":ezbuilderupgradeitem")
    public static EZBuilderUpgradeItem EZBUILDERUPGRADEITEM;
    @ObjectHolder(IrregularItems.MODID+":schematicitem")
    public static SchematicItem SCHEMATICITEM;

    //Item Containers
    @ObjectHolder(IrregularItems.MODID+":ezbuilderupgradeitem")
    public static ContainerType<EZBuilderUpgradeContainer> EZBUILDERUPGRADEITEM_CONTAINER;



}
