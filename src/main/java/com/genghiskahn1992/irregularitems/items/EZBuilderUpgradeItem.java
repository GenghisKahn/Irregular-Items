package com.genghiskahn1992.irregularitems.items;

import com.genghiskahn1992.irregularitems.containers.EZBuilderUpgradeContainer;
import com.genghiskahn1992.irregularitems.inventory.ModItemInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class EZBuilderUpgradeItem extends EZBuilderItem{

    private int structureIndex = 0;

    public EZBuilderUpgradeItem() {
        super("ezbuilderupgradeitem");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        if(!world.isRemote) {
            if (player.isSneaking()) {
                openItemInventory(player, hand);
            } else {
                cycleStructures(player, player.getHeldItem(hand));
            }
        }
        return super.onItemRightClick(world, player, hand);
    }

    private void openItemInventory(PlayerEntity player, Hand hand){
        final ItemStack stack = player.getHeldItem(hand);

        if (stack.getItem() == ModItems.EZBUILDERUPGRADEITEM) {
            NetworkHooks.openGui((ServerPlayerEntity) player, new INamedContainerProvider() {
                @Override
                public ITextComponent getDisplayName() {
                    return stack.getDisplayName();
                }

                @Nullable
                @Override
                public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity playerEntity) {
                    return new EZBuilderUpgradeContainer(id, playerInv, new ModItemInventory(stack, 9), (EZBuilderUpgradeItem) stack.getItem());
                }
            });
        }
    }

    private void cycleStructures(PlayerEntity player, ItemStack item){

        String nameHolder = "";

        if(item.hasTag()){
            if(item.getTag().contains("Items")){
                final NonNullList<ItemStack> inv = NonNullList.withSize(9, ItemStack.EMPTY);
                ItemStackHelper.loadAllItems(item.getTag(), inv);
                for(int i=0; i<inv.size(); i++){
                    structureIndex = ++structureIndex%inv.size();
                    if(inv.get(structureIndex) != ItemStack.EMPTY)
                    {
                        if(inv.get(structureIndex).hasTag() && inv.get(structureIndex).getTag().contains("SchematicID")){
                            nameHolder = inv.get(structureIndex).getTag().getString("SchematicID");
                            break;
                        }
                    }
                }
            }
        }
        structureName = nameHolder;
        if (structureName.length() == 0) {
            player.sendStatusMessage(new StringTextComponent("Error reading schematics or none installed"), true);
            item.removeChildTag("SchematicID");
        } else {
            player.sendStatusMessage(new StringTextComponent(structureName), true);
            item.getOrCreateTag().putString("SchematicID", structureName);
        }

    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

}
