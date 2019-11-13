package com.genghiskahn1992.irregularitems.blocks;

import com.genghiskahn1992.irregularitems.IrregularItems;
import com.genghiskahn1992.irregularitems.containers.*;
import com.genghiskahn1992.irregularitems.tileentities.*;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ObjectHolder;

public class ModBlocks {

    //Blocks
    @ObjectHolder(IrregularItems.MODID+":firstblock")
    public static FirstBlock FIRSTBLOCK;
    @ObjectHolder(IrregularItems.MODID+":schematicwriterblock")
    public static SchematicWriterBlock SCHEMATICWRITER_BLOCK;
    @ObjectHolder(IrregularItems.MODID+":combustiongenerator")
    public static CombustionGeneratorBlock COMBUSTIONGENERATOR_BLOCK;
    @ObjectHolder(IrregularItems.MODID+":basicquantumgenerator")
    public static BasicQuantumGeneratorBlock BASICQUANTUMGENERATOR_BLOCK;
    @ObjectHolder(IrregularItems.MODID+":quantumcapacitor")
    public static QuantumCapacitorBlock QUANTUMCAPACITOR_BLOCK;

    //TileEntities
    @ObjectHolder(IrregularItems.MODID+":firstblock")
    public static TileEntityType<FirstBlockTile> FIRSTBLOCK_TILE;
    @ObjectHolder(IrregularItems.MODID+":schematicwriterblock")
    public static TileEntityType<SchematicWriterTile> SCHEMATICWRITER_TILE;
    @ObjectHolder(IrregularItems.MODID+":combustiongenerator")
    public static TileEntityType<CombustionGeneratorTile> COMBUSTIONGENERATOR_TILE;
    @ObjectHolder(IrregularItems.MODID+":basicquantumgenerator")
    public static TileEntityType<BasicQuantumGeneratorTile> BASICQUANTUMGENERATOR_TILE;
    @ObjectHolder(IrregularItems.MODID+":quantumcapacitor")
    public static TileEntityType<QuantumCapacitorTile> QUANTUMCAPACITOR_TILE;

    //Containers
    @ObjectHolder(IrregularItems.MODID+":firstblock")
    public static ContainerType<FirstBlockContainer> FIRSTBLOCK_CONTAINER;
    @ObjectHolder(IrregularItems.MODID+":schematicwriterblock")
    public static ContainerType<SchematicWriterContainer> SCHEMATICWRITER_CONTAINER;
    @ObjectHolder(IrregularItems.MODID+":combustiongenerator")
    public static ContainerType<CombustionGeneratorContainer> COMBUSTIONGENERATOR_CONTAINER;
    @ObjectHolder(IrregularItems.MODID+":basicquantumgenerator")
    public static ContainerType<BasicQuantumGeneratorContainer> BASICQUANTUMGENERATOR_CONTAINER;
    @ObjectHolder(IrregularItems.MODID+":quantumcapacitor")
    public static ContainerType<QuantumCapacitorContainer> QUANTUMCAPACITOR_CONTAINER;

    public static void registerBlocks(final RegistryEvent.Register<Block> event){
        event.getRegistry().register(new FirstBlock());
        event.getRegistry().register(new SchematicWriterBlock());
        event.getRegistry().register(new CombustionGeneratorBlock());
        event.getRegistry().register(new BasicQuantumGeneratorBlock());
        event.getRegistry().register(new QuantumCapacitorBlock());
    }

    public static void registerBlockContainers(final RegistryEvent.Register<ContainerType<?>> event){
        event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            return new FirstBlockContainer(windowId, IrregularItems.proxy.getClientWorld(), pos, inv, IrregularItems.proxy.getClientPlayer());
        }).setRegistryName(ModBlocks.FIRSTBLOCK.getRegistryName()));

        event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            return new SchematicWriterContainer(windowId, IrregularItems.proxy.getClientWorld(), pos,inv, IrregularItems.proxy.getClientPlayer());
        }).setRegistryName(ModBlocks.SCHEMATICWRITER_BLOCK.getRegistryName()));

        event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            return new CombustionGeneratorContainer(windowId, IrregularItems.proxy.getClientWorld(), pos, inv, IrregularItems.proxy.getClientPlayer());
        }).setRegistryName(ModBlocks.COMBUSTIONGENERATOR_BLOCK.getRegistryName()));

        event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            return new BasicQuantumGeneratorContainer(windowId, IrregularItems.proxy.getClientWorld(), pos, inv, IrregularItems.proxy.getClientPlayer());
        }).setRegistryName(ModBlocks.BASICQUANTUMGENERATOR_BLOCK.getRegistryName()));

        event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            return new QuantumCapacitorContainer(windowId, IrregularItems.proxy.getClientWorld(), pos, inv, IrregularItems.proxy.getClientPlayer());
        }).setRegistryName(ModBlocks.QUANTUMCAPACITOR_BLOCK.getRegistryName()));
    }

    public static void registerTileEntities(final RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().register(TileEntityType.Builder.create(FirstBlockTile::new, ModBlocks.FIRSTBLOCK)
                .build(null).setRegistryName(ModBlocks.FIRSTBLOCK.getRegistryName()));
        event.getRegistry().register(TileEntityType.Builder.create(SchematicWriterTile::new,
                ModBlocks.SCHEMATICWRITER_BLOCK).build(null)
                .setRegistryName(ModBlocks.SCHEMATICWRITER_BLOCK.getRegistryName()));
        event.getRegistry().register(TileEntityType.Builder.create(CombustionGeneratorTile::new, ModBlocks.COMBUSTIONGENERATOR_BLOCK)
                .build(null).setRegistryName(ModBlocks.COMBUSTIONGENERATOR_BLOCK.getRegistryName()));
        event.getRegistry().register(TileEntityType.Builder.create(BasicQuantumGeneratorTile::new, ModBlocks.BASICQUANTUMGENERATOR_BLOCK)
                .build(null).setRegistryName(ModBlocks.BASICQUANTUMGENERATOR_BLOCK.getRegistryName()));
        event.getRegistry().register(TileEntityType.Builder.create(QuantumCapacitorTile::new, ModBlocks.QUANTUMCAPACITOR_BLOCK)
                .build(null).setRegistryName(ModBlocks.QUANTUMCAPACITOR_BLOCK.getRegistryName()));
    }

}