package com.genghiskahn1992.irregularitems.tileentities;

import com.genghiskahn1992.irregularitems.blocks.ModBlocks;
import com.genghiskahn1992.irregularitems.containers.SchematicWriterContainer;
import com.genghiskahn1992.irregularitems.items.ModItems;
import com.genghiskahn1992.irregularitems.network.IrregularItemPacketHandler;
import com.genghiskahn1992.irregularitems.network.SchematicPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.genghiskahn1992.irregularitems.blocks.ModBlocks.SCHEMATICWRITER_BLOCK;

public class SchematicWriterTile extends TileEntity implements INamedContainerProvider {

    private LazyOptional<IItemHandler> handler = LazyOptional.of(this::createHandler);
    private String structureName = "";

    public SchematicWriterTile(){
        super(ModBlocks.SCHEMATICWRITER_TILE);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent(getType().getRegistryName().getPath());
    }

    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new SchematicWriterContainer(i, world, pos, playerInventory, playerEntity);
    }

    public void setStructureName(String name){
        structureName = name;
        handler.ifPresent(h -> {
            ItemStack stack = h.getStackInSlot(0);
            if (stack.getItem() == ModItems.SCHEMATICITEM || stack.getItem() == ModItems.EZBUILDERITEM) {
                IrregularItemPacketHandler.CHANNEL.sendToServer(new SchematicPacket(stack, structureName));
//                stack.getOrCreateTag().putString("SchematicID", structureName);
                markDirty();
            }
        });

    }

    @Override
    public void read(CompoundNBT tag) {
        CompoundNBT invTag = tag.getCompound("inv");
        handler.ifPresent(h -> ((INBTSerializable<CompoundNBT>)h).deserializeNBT(invTag));

        super.read(tag);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        handler.ifPresent(h -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>)h).serializeNBT();
            tag.put("inv", compound);
        });

        return super.write(tag);
    }

    private IItemHandler createHandler(){
        return new ItemStackHandler(1){
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return (stack.getItem() == ModItems.SCHEMATICITEM || stack.getItem() == ModItems.EZBUILDERITEM);
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if (stack.getItem() != ModItems.SCHEMATICITEM && stack.getItem() != ModItems.EZBUILDERITEM){
                    return stack;
                }

                return super.insertItem(slot, stack, simulate);
            }

            @Override
            protected void onContentsChanged(int slot) {
                if(getStackInSlot(slot).getCount() > 0){
                    SCHEMATICWRITER_BLOCK.setHasSchematic(world, pos, getBlockState(), true);
                } else {
                    SCHEMATICWRITER_BLOCK.setHasSchematic(world, pos, getBlockState(), false);
                }
                markDirty();
            }
        };
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            return handler.cast();
        }

        return super.getCapability(cap, side);
    }

}
