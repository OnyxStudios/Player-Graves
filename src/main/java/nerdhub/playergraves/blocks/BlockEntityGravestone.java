package nerdhub.playergraves.blocks;

import nerdhub.playergraves.PlayerGraves;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.MobSpawnerEntry;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.World;

public class BlockEntityGravestone extends BlockEntity implements Tickable {

    public String playerName = "";
    public ListTag playerInv = new ListTag();
    public MobSpawnerLogic logic = new MobSpawnerLogic() {
        @Override
        public void setSpawnEntry(MobSpawnerEntry entry) {
            CompoundTag tag = new CompoundTag();
            tag.putString("Entity", "minecraft:skeleton");
            super.setSpawnEntry(new MobSpawnerEntry(tag));
            if (this.getWorld() != null) {
                BlockState blockState_1 = this.getWorld().getBlockState(this.getPos());
                this.getWorld().updateListeners(BlockEntityGravestone.this.pos, blockState_1, blockState_1, 4);
            }
        }

        @Override
        public void method_8273(int i) {
            BlockEntityGravestone.this.world.addBlockAction(BlockEntityGravestone.this.pos, PlayerGraves.BLOCK_GRAVESTONE, i, 0);
        }

        @Override
        public World getWorld() {
            return BlockEntityGravestone.this.world;
        }

        @Override
        public BlockPos getPos() {
            return BlockEntityGravestone.this.pos;
        }
    };

    public BlockEntityGravestone() {
        super(PlayerGraves.GRAVESTONE);
    }

    @Override
    public CompoundTag toTag(CompoundTag compoundTag) {
        super.toTag(compoundTag);
        compoundTag.putString("playername", this.playerName);
        compoundTag.put("inventory", playerInv);
        logic.serialize(compoundTag);
        return compoundTag;
    }

    @Override
    public void fromTag(CompoundTag compoundTag) {
        super.fromTag(compoundTag);
        this.playerName = compoundTag.getString("playername");
        this.playerInv = compoundTag.getList("inventory", NbtType.COMPOUND);
        logic.deserialize(compoundTag);
    }

    @Override
    public void tick() {
        if(PlayerGraves.config.getBoolean("grave-spawner")) {
            logic.setEntityId(EntityType.SKELETON);
            logic.update();
        }
    }

    @Override
    public boolean onBlockAction(int int_1, int int_2) {
        return this.logic.method_8275(int_1) ? true : super.onBlockAction(int_1, int_2);
    }
}
