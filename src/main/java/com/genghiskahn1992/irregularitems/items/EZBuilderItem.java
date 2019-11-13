package com.genghiskahn1992.irregularitems.items;

import com.genghiskahn1992.irregularitems.utilites.TemplateReader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraftforge.fml.loading.FMLPaths;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.*;

public class EZBuilderItem extends ModItem {

    protected String structureName = "";
    protected Template activeTemplate = new Template();
    private Path structureDir = FMLPaths.CONFIGDIR.get().resolve("structures");
    protected Map<Block, Integer> neededBlocks;
    private TemplateReader templateReader = new TemplateReader();

    public EZBuilderItem() {
        this("ezbuilderitem");
    }

    protected EZBuilderItem(String name) { super(name);}

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        if(!context.getWorld().isRemote) {
            if (!context.getPlayer().isSneaking()) {
                if(context.getItem().hasTag() && context.getItem().getTag().contains("SchematicID"))
                    structureName = context.getItem().getTag().getString("SchematicID");
                activeTemplate = processTemplate(structureName, context.getPlayer());

                build(context);
            }
        }
        return ActionResultType.SUCCESS;
    }


    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        return super.onItemRightClick(world, player, hand);
    }

    private void reportMissingBlocks(PlayerEntity player, Map<Block, Integer> missingBlocks){
        if (missingBlocks.isEmpty()) {
            return;
        } else {
            Set<Block> missingBlock = missingBlocks.keySet();
            Iterator<Block> ite = missingBlock.iterator();

            player.sendStatusMessage(new StringTextComponent("Missing Blocks for structure: "), false);
            while (ite.hasNext()) {
                Block temp = ite.next();
                player.sendStatusMessage(new StringTextComponent(temp.getNameTextComponent().getFormattedText() + " : " + missingBlocks.get(temp)), false);
            }
        }
    }

//    protected void buildAndRemoveBlocks(ItemUseContext context, Map<Block, Integer> blockCounts){
////        if(!context.getPlayer().isCreative()) {
////            PlayerInventory inv = context.getPlayer().inventory;
////            Set<Block> blockTypes = blockCounts.keySet();
////            Iterator<Block> blockIterator = blockTypes.iterator();
////            Block tempBlock;
////            ItemStack tempBlockStack;
////            int count;
////
////            while (blockIterator.hasNext()) {
////                tempBlock = blockIterator.next();
////                tempBlockStack = new ItemStack(tempBlock.asItem());
////                count = blockCounts.get(tempBlock);
////                if (tempBlock != Blocks.AIR) {
////                    if (inv.count(tempBlock.asItem()) >= count) {
////                        int removed = 0;
////                        while (removed < count) {
////                            int slot = inv.getSlotFor(tempBlockStack);
////                            ItemStack tempStack = inv.getStackInSlot(slot);
////                            if ((count - removed) >= tempStack.getCount()) {
////                                inv.setInventorySlotContents(inv.getSlotFor(tempStack), ItemStack.EMPTY);
////                                removed += tempStack.getCount();
////                            } else {
////                                inv.getStackInSlot(slot).shrink(count - removed);
////                                removed += (count - removed);
////                            }
////                        }
////                    }
////                }
////            }
////        }
////        build(context);
////    }

    protected void build(ItemUseContext context){
        if (!context.getWorld().isRemote) {
            Rotation rotation = getRotation(context.getPlacementYaw());
            BlockPos size = activeTemplate.getSize();
            BlockPos pos = getOffset(rotation, size, context.getPos().offset(context.getFace()));
            PlacementSettings placementSettings = new PlacementSettings().setMirror(Mirror.NONE).setRotation(rotation);

            Map<BlockPos, CompoundNBT> blockMap = templateReader.getBlockMapping();

            Set<BlockPos> blockPositions = blockMap.keySet();
            Iterator<BlockPos> posIterator = blockPositions.iterator();
            Map<Block, Integer> missingBlocks = new HashMap<>();
            BlockPos tempPos, transPos;
            BlockState tempState;
            boolean shouldBuild;

            while(posIterator.hasNext()) {
                shouldBuild = false;
                tempPos = posIterator.next();
                transPos = Template.transformedBlockPos(placementSettings, tempPos);
                transPos = pos.add(transPos);
                tempState = NBTUtil.readBlockState(blockMap.get(tempPos)).rotate(placementSettings.getRotation());
                if (tempState.getBlock() != Blocks.AIR && context.getWorld().getBlockState(transPos).isReplaceable(new BlockItemUseContext(context))) {
                    if(context.getPlayer().isCreative()) {
                        shouldBuild = true;
                    } else if(context.getPlayer().inventory.count(tempState.getBlock().asItem()) > 0) {
                        int slot = context.getPlayer().inventory.getSlotFor(new ItemStack(tempState.getBlock()));
                        context.getPlayer().inventory.decrStackSize(slot, 1);
                        shouldBuild = true;
                    } else {
                        if(missingBlocks.keySet().contains(tempState.getBlock())){
                            missingBlocks.put(tempState.getBlock(), missingBlocks.get(tempState.getBlock()) + 1);
                        } else {
                            missingBlocks.put(tempState.getBlock(), 1);
                        }
                    }
                    if(shouldBuild)
                        context.getWorld().setBlockState(transPos, tempState);
                }
            }
            if (!missingBlocks.isEmpty()){
                reportMissingBlocks(context.getPlayer(), missingBlocks);
            }
        }
    }

    protected Template processTemplate(String structureName, PlayerEntity player) {
        templateReader.reset();
        if(templateReader.ProcessTemplate(structureDir, structureName)) {
            neededBlocks = templateReader.getBlockRequirements();
            return templateReader.getTemplate();
        }else{
            player.sendStatusMessage(new StringTextComponent("Structure Template Processing Error"), false);
            return new Template();
        }
    }

    private BlockPos getOffset(Rotation rotation, BlockPos size, BlockPos pos){

        switch (rotation){
            case COUNTERCLOCKWISE_90: //Facing North (-half rounded down x, 270 rotation)
                return pos.add(-Math.floorDiv(size.getZ(), 2), 0, 0);
            case CLOCKWISE_180: //Facing West (half rounded down z, 180 rotation)
                return pos.add(0, 0, Math.floorDiv(size.getZ(), 2));
            case CLOCKWISE_90: //Facing South (half rounded down x, 90 rotation)
                return pos.add(Math.floorDiv(size.getZ(), 2), 0, 0);
            case NONE: //Facing East (-half rounded down z no rotation)
            default:
                return pos.add(0, 0, -Math.floorDiv(size.getZ(), 2));
        }

    }

    private Rotation getRotation(Float yaw){

        Direction rotation = Direction.UP;

        if (yaw < 0) {
            yaw += 360;
        }
        if (yaw >= 315 || yaw < 45) {
            rotation = Direction.SOUTH;
        } else if (yaw < 135) {
            rotation = Direction.WEST;
        } else if (yaw < 225) {
            rotation = Direction.NORTH;
        } else if (yaw < 315) {
            rotation = Direction.EAST;
        }


        switch(rotation){
            case NORTH:
                return Rotation.COUNTERCLOCKWISE_90;
            case WEST:
                return Rotation.CLOCKWISE_180;
            case SOUTH:
                return Rotation.CLOCKWISE_90;
            case EAST:
            case UP:
            case DOWN:
            default:
                return Rotation.NONE;
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if(stack.hasTag() && stack.getTag().contains("SchematicID"))
            tooltip.add(new StringTextComponent("\u00A77\u00A7o" + "Currently installed schematic: " + stack.getTag().getString("SchematicID")));
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

//    @Override
//    public ITextComponent getDisplayName(ItemStack stack) {
//        if(stack.hasTag() && stack.getTag().contains("StructureName"))
//            return stack.getItem().getName().appendText(": " + stack.getTag().getString("StructureName"));
//        return stack.getItem().getName().appendText(": " + structureName);
//    }
}
