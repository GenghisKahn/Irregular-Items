package com.genghiskahn1992.irregularitems.tileentities;

import com.genghiskahn1992.irregularitems.blocks.ModBlocks;
import com.genghiskahn1992.irregularitems.capabilities.CapabilityQuantumEnergy;
import com.genghiskahn1992.irregularitems.capabilities.IQuantumEnergyStorage;
import com.genghiskahn1992.irregularitems.containers.QuantumCapacitorContainer;
import com.genghiskahn1992.irregularitems.utilites.Config;
import com.genghiskahn1992.irregularitems.utilites.QuantumEnergyStorage;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraft.tileentity.ITickableTileEntity;
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
import java.util.concurrent.atomic.AtomicInteger;


public class QuantumCapacitorTile extends TileEntity implements ITickableTileEntity, INamedContainerProvider {

    private LazyOptional<IItemHandler> handler = LazyOptional.of(this::createHandler);
    private LazyOptional<IQuantumEnergyStorage> energy = LazyOptional.of(this::createEnergy);

    private int level;

    public QuantumCapacitorTile(){
        super(ModBlocks.QUANTUMCAPACITOR_TILE);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent(getType().getRegistryName().getPath());
    }

    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new QuantumCapacitorContainer(i, world, pos, playerInventory, playerEntity);
    }

    @Override
    public void tick() {
        if(world.isRemote){
            return;
        }

        if(energy.isPresent()){
            energy.ifPresent(e -> {
                level = (int)Math.round(((double)e.getEnergyStored()/(double)e.getMaxEnergyStored()) * 15);
            });
            markDirty();
        }

        BlockState blockstate = world.getBlockState(pos);

        if (blockstate.get(BlockStateProperties.LEVEL_0_15) != level) {
            world.setBlockState(pos, blockstate.with(BlockStateProperties.LEVEL_0_15, level), 3);
        }

        sendOutPower();
    }

    @Override
    public void read(CompoundNBT tag) {
        CompoundNBT invTag = tag.getCompound("inv");
        handler.ifPresent(h -> ((INBTSerializable<CompoundNBT>)h).deserializeNBT(invTag));

        CompoundNBT energyTag = tag.getCompound("energy");
        energy.ifPresent(h -> ((INBTSerializable<CompoundNBT>)h).deserializeNBT(energyTag));

        level = tag.getInt("level");

        super.read(tag);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        handler.ifPresent(h -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>)h).serializeNBT();
            tag.put("inv", compound);
        });

        energy.ifPresent(h -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>)h).serializeNBT();
            tag.put("energy", compound);
        });

        tag.putInt("level", level);

        return super.write(tag);
    }

    private IItemHandler createHandler(){
        return new ItemStackHandler(1){
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return FurnaceTileEntity.getBurnTimes().keySet().contains(stack.getItem()) && !stack.hasContainerItem();
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if (!FurnaceTileEntity.getBurnTimes().keySet().contains(stack.getItem()) ||
                    stack.hasContainerItem()){
                    return stack;
                }

                return super.insertItem(slot, stack, simulate);
            }

            @Override
            protected void onContentsChanged(int slot) {
                markDirty();
            }
        };
    }

    private IQuantumEnergyStorage createEnergy() {
        return new QuantumEnergyStorage(Config.QUANTUMCAPACITOR_MAXPOWER.get(), 1000);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            return handler.cast();
        }

        if (cap == CapabilityQuantumEnergy.ENERGY) {
            return energy.cast();
        }

        return super.getCapability(cap, side);
    }

    private void sendOutPower(){
        energy.ifPresent(energy -> {
            AtomicInteger capacity = new AtomicInteger(energy.getEnergyStored());
            if (capacity.get() > 0){
                for(Direction direction : Direction.values()){
                    TileEntity te = world.getTileEntity(pos.offset(direction));
                    if (te != null){
                        boolean doContinue = te.getCapability(CapabilityQuantumEnergy.ENERGY, direction).map(handler -> {
                            if (handler.canReceive()){
                                int received = handler.receiveEnergy(Math.min(capacity.get(), Config.QUANTUMCAPACITOR_SEND.get()), false);
                                capacity.addAndGet(-received);
                                ((QuantumEnergyStorage)energy).consumeEnergy(received);
                                markDirty();
                                return capacity.get() > 0;
                            } else {
                                return true;
                            }
                        }).orElse(true);
                        if (!doContinue){
                            return;
                        }
                    }
                }
            }
        });
    }
}
