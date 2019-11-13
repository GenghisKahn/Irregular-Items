package com.genghiskahn1992.irregularitems.network;

import com.genghiskahn1992.irregularitems.containers.SchematicWriterContainer;
import com.genghiskahn1992.irregularitems.items.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SchematicPacket {
    private ItemStack stack;
    private String schematicID;

    public SchematicPacket(ItemStack stackIn, String schematicIDIn) {
        this.stack = stackIn;
        this.schematicID = schematicIDIn;
    }

    static void encode(SchematicPacket msg, PacketBuffer buffer) {
        buffer.writeItemStack(msg.stack);
        buffer.writeString(msg.schematicID);
    }

    static SchematicPacket decode(PacketBuffer buffer) {
        return new SchematicPacket(buffer.readItemStack(), buffer.readString());
    }

    public static class Handler
    {
        static void handle(SchematicPacket msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                if(msg.stack.getItem() == ModItems.SCHEMATICITEM || msg.stack.getItem() == ModItems.EZBUILDERITEM){
                    if(ctx.get().getSender() != null && ctx.get().getSender().openContainer instanceof SchematicWriterContainer) {
                        ctx.get().getSender().openContainer.getSlot(0).getStack().getOrCreateTag().putString("SchematicID", msg.schematicID);
                    }
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }

}