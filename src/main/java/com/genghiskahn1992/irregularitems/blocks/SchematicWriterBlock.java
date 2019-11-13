package com.genghiskahn1992.irregularitems.blocks;

import com.genghiskahn1992.irregularitems.tileentities.SchematicWriterTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public class SchematicWriterBlock extends ModBlock {

    private static final VoxelShape baseBoundingBox = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
    private static final VoxelShape postBoundingBox = Block.makeCuboidShape(4.0D, 2.0D, 4.0D, 12.0D, 14.0D, 12.0D);
    private static final VoxelShape topBoundingBox = Block.makeCuboidShape(0.0D, 15.0D, 0.0D, 16.0D, 15.0D, 16.0D);
    private static final VoxelShape baseAndPostBoundingBox = VoxelShapes.or(baseBoundingBox, postBoundingBox);
    private static final VoxelShape collisionBox = VoxelShapes.or(baseAndPostBoundingBox, topBoundingBox);
    private static final VoxelShape boundingBoxWest = VoxelShapes.or(Block.makeCuboidShape(1.0D, 10.0D, 0.0D, 5.333333D, 14.0D, 16.0D), Block.makeCuboidShape(5.333333D, 12.0D, 0.0D, 9.666667D, 16.0D, 16.0D), Block.makeCuboidShape(9.666667D, 14.0D, 0.0D, 14.0D, 18.0D, 16.0D), baseAndPostBoundingBox);
    private static final VoxelShape boundingBoxNorth = VoxelShapes.or(Block.makeCuboidShape(0.0D, 10.0D, 1.0D, 16.0D, 14.0D, 5.333333D), Block.makeCuboidShape(0.0D, 12.0D, 5.333333D, 16.0D, 16.0D, 9.666667D), Block.makeCuboidShape(0.0D, 14.0D, 9.666667D, 16.0D, 18.0D, 14.0D), baseAndPostBoundingBox);
    private static final VoxelShape boundingBoxEast = VoxelShapes.or(Block.makeCuboidShape(15.0D, 10.0D, 0.0D, 10.666667D, 14.0D, 16.0D), Block.makeCuboidShape(10.666667D, 12.0D, 0.0D, 6.333333D, 16.0D, 16.0D), Block.makeCuboidShape(6.333333D, 14.0D, 0.0D, 2.0D, 18.0D, 16.0D), baseAndPostBoundingBox);
    private static final VoxelShape boundingBoxSouth = VoxelShapes.or(Block.makeCuboidShape(0.0D, 10.0D, 15.0D, 16.0D, 14.0D, 10.666667D), Block.makeCuboidShape(0.0D, 12.0D, 10.666667D, 16.0D, 16.0D, 6.333333D), Block.makeCuboidShape(0.0D, 14.0D, 6.333333D, 16.0D, 18.0D, 2.0D), baseAndPostBoundingBox);

    public SchematicWriterBlock() {
        super("schematicwriterblock",
                Properties.create(Material.WOOD)
                        .sound(SoundType.WOOD)
                        .hardnessAndResistance(2.0f)
                        .lightValue(14));
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        if (entity != null) {
            world.setBlockState(pos, state.with(BlockStateProperties.FACING, getRotation(entity.rotationYaw)).with(BlockStateProperties.HAS_BOOK,
                    stack.getOrCreateTag().getBoolean("has_schematic")), 2);
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder){
        builder.add(BlockStateProperties.FACING, BlockStateProperties.HAS_BOOK);
    }

    @Override
    @Nullable
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new SchematicWriterTile();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
        if(player.isSneaking())
            return false;

        if (world.isRemote)
            return true;

        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof INamedContainerProvider) {
            NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tileEntity, tileEntity.getPos());
        } else {
            throw new IllegalStateException("Named container provider is missing");
        }
        return true;
    }

    private Direction getRotation(Float yaw){

        if (yaw < 0) {
            yaw += 360;
        }
        if (yaw >= 315 || yaw < 45) {
            return Direction.NORTH;
        } else if (yaw < 135) {
            return Direction.EAST;
        } else if (yaw < 225) {
            return Direction.SOUTH;
        } else if (yaw < 315) {
            return Direction.WEST;
        }
        return Direction.NORTH;
    }

    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return collisionBox;
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        switch(state.get(BlockStateProperties.FACING)) {
            case NORTH:
                return boundingBoxNorth;
            case SOUTH:
                return boundingBoxSouth;
            case EAST:
                return boundingBoxEast;
            case WEST:
                return boundingBoxWest;
            default:
                return baseAndPostBoundingBox;
        }
    }

    public void setHasSchematic(World world, BlockPos pos, BlockState state, Boolean bool){
        world.setBlockState(pos, state.with(BlockStateProperties.HAS_BOOK, bool));
    }

    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            LazyOptional<IItemHandler> handler = tileentity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
            handler.ifPresent(h ->{
                for(int i=0; i<h.getSlots(); i++) {
                    InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), h.getStackInSlot(i));
                }
            });
        }

        super.onReplaced(state, worldIn, pos, newState, isMoving);
    }
}