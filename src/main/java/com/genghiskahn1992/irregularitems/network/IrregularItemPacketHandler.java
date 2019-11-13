package com.genghiskahn1992.irregularitems.network;

import com.genghiskahn1992.irregularitems.IrregularItems;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class IrregularItemPacketHandler {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(IrregularItems.MODID, "main_channel"))
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();

    public static void register(){
        int id=0;

        CHANNEL.registerMessage(id++, SchematicPacket.class, SchematicPacket::encode, SchematicPacket::decode, SchematicPacket.Handler::handle);
    }

}
