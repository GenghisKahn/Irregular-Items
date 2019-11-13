package com.genghiskahn1992.irregularitems.capabilities;

public interface IQuantumEnergyStorage {

    int receiveEnergy(int maxReceive, boolean simulate);

    int extractEnergy(int maxExtract, boolean simulate);

    int getEnergyStored();

    int getMaxEnergyStored();

    boolean canExtract();

    boolean canReceive();

}
