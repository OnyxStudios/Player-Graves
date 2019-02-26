package nerdhub.playergraves.blocks;

import com.sun.istack.internal.Nullable;
import nerdhub.playergraves.data.PlayerInventoryPersistentState;
import nerdhub.playergraves.utils.InventoryHelper;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BlockGravestone extends BlockWithEntity {

    public static final DirectionProperty FACING = HorizontalFacingBlock.field_11177;

    public BlockGravestone() {
        super(FabricBlockSettings.of(Material.STONE).hardness(0.2f).build());
        this.setDefaultState(this.stateFactory.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity playerEntity) {
        super.onBreak(world, pos, state, playerEntity);
        if (!world.isClient) {
            BlockEntityGravestone gravestone = (BlockEntityGravestone) world.getBlockEntity(pos);

            if (gravestone.playerName != null && gravestone.playerName.equals(playerEntity.getName().getText())) {
                ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) playerEntity;
                PlayerInventoryPersistentState persistentState = PlayerInventoryPersistentState.get((ServerWorld) world);

                if(gravestone.playerInv != null) {
                    InventoryHelper.deserializeInv(playerEntity, gravestone.playerInv);
                    world.setBlockState(pos, Blocks.AIR.getDefaultState());
                    return;
                }else if (persistentState.isPlayerInventorySaved(serverPlayerEntity)) {
                    persistentState.recoverPlayerInventory(serverPlayerEntity);
                    world.setBlockState(pos, Blocks.AIR.getDefaultState());
                    return;
                }
            }
        }
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isSimpleFullBlock(BlockState blockState_1, BlockView blockView_1, BlockPos blockPos_1) {
        return false;
    }

    @Override
    public boolean skipRenderingSide(BlockState blockState_1, BlockState blockState_2, Direction direction_1) {
        return blockState_1.getBlock() == this ? true : super.skipRenderingSide(blockState_1, blockState_2, direction_1);
    }

    @Override
    public boolean isTranslucent(BlockState blockState_1, BlockView blockView_1, BlockPos blockPos_1) {
        return true;
    }

    @Override
    public BlockRenderType getRenderType(BlockState var1) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext_1) {
        return this.getDefaultState().with(FACING, itemPlacementContext_1.getPlayerHorizontalFacing().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState blockState_1, Rotation rotation_1) {
        return blockState_1.with(FACING, rotation_1.rotate(blockState_1.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState blockState_1, Mirror mirror_1) {
        return blockState_1.rotate(mirror_1.getRotation(blockState_1.get(FACING)));
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> stateFactory$Builder_1) {
        stateFactory$Builder_1.with(new Property[]{FACING});
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, VerticalEntityPosition verticalEntityPosition_1) {
        VoxelShape defaultShape = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D);
        switch (state.get(FACING)) {
            case NORTH:
                return VoxelShapes.union(defaultShape, Block.createCuboidShape(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D));
            case SOUTH:
                return VoxelShapes.union(defaultShape, Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D));
            case EAST:
                return VoxelShapes.union(defaultShape, Block.createCuboidShape(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D));
            case WEST:
                return VoxelShapes.union(defaultShape, Block.createCuboidShape(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D));
        }

        return defaultShape;
    }

    @Override
    public BlockEntity createBlockEntity(BlockView blockView) {
        return new BlockEntityGravestone();
    }
}
