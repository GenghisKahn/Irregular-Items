package com.genghiskahn1992.irregularitems.inventory;

import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;

public class ModItemInventory extends Inventory {

    private final ItemStack stack;

    public ModItemInventory(ItemStack stack, int count){
        super(count);
        this.stack = stack;
        readItemStack();
    }

    public void readItemStack(){
        if (stack.hasTag()) {
            readNBT(stack.getTag());
        }
    }

    private void readNBT(CompoundNBT tag){
        final NonNullList<ItemStack> inv = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(tag, inv);
        for (int i = 0; i < inv.size(); i++) {
            setInventorySlotContents(i, inv.get(i));
        }
    }

    public ItemStack getStack(){
        return stack;
    }

    public void writeItemStack(){
        if (isEmpty()){
            stack.removeChildTag("Items");
        } else {
            writeNBT(stack.getOrCreateTag());
        }
    }

    private void writeNBT(CompoundNBT tag){
        final NonNullList<ItemStack> inv = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
        for (int i = 0; i < inv.size(); i++) {
            inv.set(i, getStackInSlot(i));
        }
        ItemStackHelper.saveAllItems(tag, inv, false);
    }
}
