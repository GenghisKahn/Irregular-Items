package com.genghiskahn1992.irregularitems.capabilities;

import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityQuantumEnergy {
    @CapabilityInject(IQuantumEnergyStorage.class)
    public static Capability<IQuantumEnergyStorage> ENERGY = null;

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IQuantumEnergyStorage.class, new Capability.IStorage<IQuantumEnergyStorage>()
                {
                    @Override
                    public INBT writeNBT(Capability<IQuantumEnergyStorage> capability, IQuantumEnergyStorage instance, Direction side)
                    {
                        return new IntNBT(instance.getEnergyStored());
                    }

                    @Override
                    public void readNBT(Capability<IQuantumEnergyStorage> capability, IQuantumEnergyStorage instance, Direction side, INBT nbt)
                    {
                        if (!(instance instanceof QEnergyStorage))
                            throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
                        ((QEnergyStorage)instance).energy = ((IntNBT)nbt).getInt();
                    }
                },
                () -> new QEnergyStorage(1000));
    }
}
