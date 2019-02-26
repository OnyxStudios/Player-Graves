package nerdhub.playergraves.data;

import com.google.common.collect.Maps;
import nerdhub.playergraves.utils.InventoryHelper;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TagHelper;
import net.minecraft.world.PersistentState;

import java.util.*;

public class PlayerInventoryPersistentState extends PersistentState {

    private Map<UUID, ListTag> inventories = Maps.newHashMap();

    public PlayerInventoryPersistentState() {
        super("PlayerGravesInventoryPersistentState");
    }

    public void recoverPlayerInventory(ServerPlayerEntity playerEntity) {
        InventoryHelper.deserializeInv(playerEntity, getPlayerInventory(playerEntity));
    }

    public ListTag getPlayerInventory(ServerPlayerEntity playerEntity) {
        return this.inventories.get(playerEntity.getUuid());
    }

    public boolean isPlayerInventorySaved(ServerPlayerEntity playerEntity) {
        return this.inventories.containsKey(playerEntity.getUuid());
    }

    public void savePlayerInventory(ServerPlayerEntity playerEntity) {
        this.inventories.put(playerEntity.getUuid(), playerEntity.inventory.serialize(new ListTag()));
        this.markDirty();
    }

    public static PlayerInventoryPersistentState get(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(PlayerInventoryPersistentState::new, "PlayerGravesInventoryPersistentState");
    }

    @Override
    public void fromTag(CompoundTag compoundTag) {
        ListTag listTag = compoundTag.getList("inventories", NbtType.COMPOUND);

        for (Iterator<Tag> it = listTag.iterator(); it.hasNext();) {
            CompoundTag tag = (CompoundTag) it.next();
            UUID uuid = TagHelper.deserializeUuid(tag.getCompound("uuid"));
            ListTag inventoryTag = tag.getList("inventory", NbtType.COMPOUND);
            inventories.put(uuid, inventoryTag);
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag compoundTag) {
        ListTag listTag = new ListTag();

        for (UUID uuid : inventories.keySet()) {
            CompoundTag tag = new CompoundTag();
            tag.put("uuid", TagHelper.serializeUuid(uuid));
            tag.put("inventory", inventories.get(uuid));
        }

        compoundTag.put("inventories", listTag);

        return compoundTag;
    }
}
