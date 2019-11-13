package com.genghiskahn1992.irregularitems.proxy;

import com.genghiskahn1992.irregularitems.blocks.ModBlocks;
import com.genghiskahn1992.irregularitems.containers.*;
import com.genghiskahn1992.irregularitems.items.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;


public class ClientProxy implements IProxy {

    @Override
    public void init() {
        ScreenManager.registerFactory(ModBlocks.FIRSTBLOCK_CONTAINER, FirstBlockScreen::new);
        ScreenManager.registerFactory(ModBlocks.SCHEMATICWRITER_CONTAINER, SchematicWriterScreen::new);
        ScreenManager.registerFactory(ModBlocks.COMBUSTIONGENERATOR_CONTAINER, CombustionGeneratorScreen::new);
        ScreenManager.registerFactory(ModBlocks.BASICQUANTUMGENERATOR_CONTAINER, BasicQuantumGeneratorScreen::new);
        ScreenManager.registerFactory(ModBlocks.QUANTUMCAPACITOR_CONTAINER, QuantumCapacitorScreen::new);

        ScreenManager.registerFactory(ModItems.EZBUILDERUPGRADEITEM_CONTAINER, EZBuilderUpgradeScreen::new);
    }

    @Override
    public World getClientWorld() {
        return Minecraft.getInstance().world;
    }

    @Override
    public PlayerEntity getClientPlayer() {
        return Minecraft.getInstance().player;
    }
}
