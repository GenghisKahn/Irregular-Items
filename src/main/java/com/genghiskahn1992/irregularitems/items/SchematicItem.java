package com.genghiskahn1992.irregularitems.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class SchematicItem extends ModItem {

    public SchematicItem(){
        super("schematicitem", new Properties().maxStackSize(16));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if(!worldIn.isRemote()){
            if(playerIn.isSneaking()) {
                if (playerIn.getHeldItem(handIn).getItem() == ModItems.SCHEMATICITEM) {
                    ((SchematicItem) playerIn.getHeldItem(handIn).getItem()).clearSchematic(playerIn.getHeldItem(handIn));
                    return ActionResult.newResult(ActionResultType.SUCCESS, playerIn.getHeldItem(handIn));
                }
            }
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        return this.onItemRightClick(context.getWorld(), context.getPlayer(), context.getHand()).getType();
    }

    private void clearSchematic(ItemStack stack){
        if(stack.hasTag() && stack.getTag().contains("SchematicID")){
            stack.getTag().remove("SchematicID");
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if(stack.hasTag() && stack.getTag().contains("SchematicID"))
            tooltip.add(new StringTextComponent("\u00A77\u00A7o" + stack.getTag().getString("SchematicID")));
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        if(stack.hasTag() && stack.getTag().contains("SchematicID"))
        {
            return true;
        }
        return super.hasEffect(stack);
    }
}
