package nerdhub.playergraves.events;

import nerdhub.playergraves.PlayerGraves;
import nerdhub.playergraves.blocks.BlockEntityGravestone;
import nerdhub.playergraves.blocks.BlockGravestone;
import nerdhub.playergraves.data.PlayerInventoryPersistentState;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GravesEventHandler {

    public static void registerEventHandlers() {
        EntityDeathDropsCallback.EVENT.register((world, livingEntity, damageSource, ci) -> {
            if(livingEntity instanceof PlayerEntity && !livingEntity.world.getGameRules().getBoolean("keepInventory")) {
                BlockPos deathPos = findValidPos(livingEntity.world, livingEntity.getPos());

                if(deathPos != null && !((PlayerEntity) livingEntity).inventory.isInvEmpty()) {
                    if(livingEntity.world.isAir(deathPos.down()) || livingEntity.world.getBlockState(deathPos.down()).getBlock() instanceof FluidBlock || livingEntity.world.getBlockState(deathPos.down()).getBlock() == Blocks.TALL_GRASS) {
                        livingEntity.world.setBlockState(deathPos.down(), Blocks.DIRT.getDefaultState());
                    }
                    livingEntity.world.setBlockState(deathPos, PlayerGraves.BLOCK_GRAVESTONE.getDefaultState().with(BlockGravestone.FACING, livingEntity.getHorizontalFacing().getOpposite()));
                    BlockEntity blockEntity = livingEntity.world.getBlockEntity(deathPos);

                    if(blockEntity instanceof BlockEntityGravestone) {
                        ((BlockEntityGravestone) blockEntity).playerName = livingEntity.getEntityName();
                        ((BlockEntityGravestone) blockEntity).playerInv = ((PlayerEntity) livingEntity).inventory.serialize(new ListTag());
                        blockEntity.markDirty();

                        if(!livingEntity.world.isClient) {
                            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) livingEntity;
                            PlayerInventoryPersistentState persistentState = PlayerInventoryPersistentState.get(serverPlayerEntity.getServerWorld());
                            persistentState.savePlayerInventory(serverPlayerEntity);
                            serverPlayerEntity.addChatMessage(new TranslatableTextComponent("graves.spawnedgrave", deathPos.getX(), deathPos.getY(), deathPos.getZ()).setStyle(new Style().setColor(TextFormat.GOLD)), false);
                        }
                        livingEntity.world.updateListeners(deathPos, livingEntity.world.getBlockState(deathPos), livingEntity.world.getBlockState(deathPos), 3);
                        ci.cancel();
                    }
                }
            }
        });
    }

    public static BlockPos findValidPos(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if(world.isAir(pos) || state.getBlock() == Blocks.TALL_GRASS || state.getBlock() instanceof FluidBlock || state.getBlock() == Blocks.SNOW) {
            return pos;
        }

        int skyLimit = 256 - pos.getY();
        for (int i = 0; i < skyLimit; i++) {
            BlockPos offsetPos = pos.up(i);
            BlockState offsetState = world.getBlockState(offsetPos);
            if(offsetPos.getY() < 257 && (world.isAir(offsetPos) || offsetState.getBlock() == Blocks.TALL_GRASS || offsetState.getBlock() instanceof FluidBlock || offsetState.getBlock() == Blocks.SNOW)) {
                return offsetPos;
            }
        }

        for (int i = pos.getY(); i > 0; i--) {
            BlockPos offsetPos = pos.down(i);
            BlockState offsetState = world.getBlockState(offsetPos);
            if(offsetPos.getY() >= 0 && (world.isAir(offsetPos) || offsetState.getBlock() == Blocks.TALL_GRASS || offsetState.getBlock() instanceof FluidBlock || offsetState.getBlock() == Blocks.SNOW)) {
                return offsetPos;
            }
        }

        return null;
    }
}
