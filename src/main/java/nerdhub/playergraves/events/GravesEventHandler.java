package nerdhub.playergraves.events;

import nerdhub.playergraves.PlayerGraves;
import nerdhub.playergraves.blocks.BlockEntityGravestone;
import nerdhub.playergraves.blocks.BlockGravestone;
import nerdhub.playergraves.data.PlayerInventoryPersistentState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GravesEventHandler {

    public static void registerEventHandlers() {
        EntityDeathDropsCallback.EVENT.register((world, livingEntity, damageSource, ci) -> {
            if(livingEntity instanceof PlayerEntity && !livingEntity.world.getGameRules().getBoolean("keepInventory")) {
                BlockPos deathPos = findValidPos(livingEntity.world, livingEntity.getPos());

                if(deathPos != null) {
                    if(livingEntity.world.isAir(deathPos.down()) || livingEntity.world.getBlockState(deathPos.down()).getBlock() instanceof FluidBlock || livingEntity.world.getBlockState(deathPos.down()).getBlock() == Blocks.TALL_GRASS) {
                        livingEntity.world.setBlockState(deathPos.down(), Blocks.DIRT.getDefaultState());
                    }
                    livingEntity.world.setBlockState(deathPos, PlayerGraves.BLOCK_GRAVESTONE.getDefaultState().with(BlockGravestone.FACING, livingEntity.getHorizontalFacing()));
                    BlockEntity blockEntity = livingEntity.world.getBlockEntity(deathPos);

                    if(blockEntity instanceof BlockEntityGravestone) {
                        ((BlockEntityGravestone) blockEntity).playerName = livingEntity.getName().getText();
                        ((BlockEntityGravestone) blockEntity).playerInv = ((PlayerEntity) livingEntity).inventory.serialize(new ListTag());
                        blockEntity.markDirty();

                        if(!livingEntity.world.isClient) {
                            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) livingEntity;
                            PlayerInventoryPersistentState persistentState = PlayerInventoryPersistentState.get(serverPlayerEntity.getServerWorld());
                            persistentState.savePlayerInventory(serverPlayerEntity);
                        }
                        livingEntity.world.updateListeners(deathPos, livingEntity.world.getBlockState(deathPos), livingEntity.world.getBlockState(deathPos), 3);
                        ci.cancel();
                    }
                }
            }
        });
    }

    public static BlockPos findValidPos(World world, BlockPos pos) {
        if(world.isAir(pos) || world.getBlockState(pos).getBlock() == Blocks.TALL_GRASS || world.getBlockState(pos).getBlock() instanceof FluidBlock) {
            return pos;
        }

        int skyLimit = 256 - pos.getY();
        for (int i = 0; i < skyLimit; i++) {
            BlockPos offsetPos = pos.up(i);
            if(!world.isAir(offsetPos) || offsetPos.getY() > 256 || world.getBlockState(offsetPos).getBlock() != Blocks.TALL_GRASS || !(world.getBlockState(offsetPos).getBlock() instanceof FluidBlock)) {
                continue;
            }

            return offsetPos;
        }

        for (int i = pos.getY(); i > 0; i--) {
            BlockPos offsetPos = pos.down(i);
            if(!world.isAir(offsetPos) || offsetPos.getY() < 0 || world.getBlockState(offsetPos).getBlock() != Blocks.TALL_GRASS || !(world.getBlockState(offsetPos).getBlock() instanceof FluidBlock)) {
                continue;
            }

            return offsetPos;
        }

        return null;
    }
}
