package com.genghiskahn1992.irregularitems.containers;

import com.genghiskahn1992.irregularitems.inventory.ModItemInventory;
import com.genghiskahn1992.irregularitems.items.EZBuilderUpgradeItem;
import com.genghiskahn1992.irregularitems.items.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import static com.genghiskahn1992.irregularitems.items.ModItems.EZBUILDERUPGRADEITEM_CONTAINER;

public class EZBuilderUpgradeContainer extends Container {

    private EZBuilderUpgradeItem heldItem;
//    private PlayerEntity playerEntity;
    private IItemHandler playerInventory;
    private IInventory itemInventory;

    public EZBuilderUpgradeContainer(int windowId, PlayerInventory playerInventory, ModItemInventory itemInventory, EZBuilderUpgradeItem item){
        super(EZBUILDERUPGRADEITEM_CONTAINER, windowId);

        this.playerInventory = new InvWrapper(playerInventory);
        this.itemInventory = itemInventory;
        this.heldItem = item;

//        heldItem.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> addSlot(new SlotItemHandler(h, 0, 64, 24)));
        for(int i = 0; i < 3; i++){
            for (int j = 0; j < 3; j++) {
                addSlot(new Slot(itemInventory, i + (j * 3), j * 18 + 62, i * 18 + 16));
            }

        }

        layoutPlayerInventorySlots(8, 84);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
//        return isWithinUsableDistance(IWorldPosCallable.of(tileEntity.getWorld(), tileEntity.getPos()), playerEntity, ModBlocks.FIRSTBLOCK);
        return (playerIn.getHeldItemMainhand().getItem() == ModItems.EZBUILDERUPGRADEITEM);
    }

    private int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0; i < amount; i++){
            addSlot(new SlotItemHandler(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private int addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy){
        for (int j = 0; j < verAmount; j++){
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    private void layoutPlayerInventorySlots(int leftCol, int topRow){
        // Player Inventory
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);

        // Hotbar
        topRow += 58;
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if(slot != null && slot.getHasStack()) {
            if(slot.getStack().getItem() != ModItems.EZBUILDERUPGRADEITEM) {
                ItemStack stack = slot.getStack();
                itemstack = stack.copy();
                if (index < 9) {
                    if (!this.mergeItemStack(stack, 9, this.inventorySlots.size(), true)) {
                        return ItemStack.EMPTY;
                    }
                    slot.onSlotChange(stack, itemstack);
                } else {
                    if (stack.getItem() == ModItems.SCHEMATICITEM) {
                        if (!this.mergeItemStack(stack, 0, 9, false)) {
                            return ItemStack.EMPTY;
                        } else {
                            if(!this.mergeItemStack(stack, 9, this.inventorySlots.size(), false)){
                                return ItemStack.EMPTY;
                            }
                        }
                    } else if (index < 36) {
                        if (!this.mergeItemStack(stack, 36, this.inventorySlots.size(), false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!this.mergeItemStack(stack, 9, 36, false)) {
                        return ItemStack.EMPTY;
                    }
                }

                if (stack.isEmpty()) {
                    slot.putStack(ItemStack.EMPTY);
                } else {
                    slot.onSlotChanged();
                }

                if (stack.getCount() == itemstack.getCount()) {
                    return ItemStack.EMPTY;
                }

                slot.onTake(playerIn, stack);
            }
        }
        return itemstack;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        if(itemInventory instanceof ModItemInventory)
            ((ModItemInventory) itemInventory).writeItemStack();
    }
}
