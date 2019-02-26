package nerdhub.playergraves.utils;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public class InventoryHelper {

    public static void deserializeInv(PlayerEntity player, ListTag listTag) {
        for(int int_1 = 0; int_1 < listTag.size(); ++int_1) {
            CompoundTag compoundTag_1 = listTag.getCompoundTag(int_1);
            int int_2 = compoundTag_1.getByte("Slot") & 255;
            ItemStack itemStack_1 = ItemStack.fromTag(compoundTag_1);
            if (!itemStack_1.isEmpty()) {
                if (int_2 >= 0 && int_2 < player.inventory.main.size()) {
                    if(player.inventory.main.get(int_2).isEmpty()) {
                        player.inventory.main.set(int_2, itemStack_1);
                    }else if(!player.world.isClient) {
                        player.world.spawnEntity(new ItemEntity(player.world, player.x, player.y, player.z, itemStack_1));
                    }
                } else if (int_2 >= 100 && int_2 < player.inventory.armor.size() + 100) {
                    if(player.inventory.armor.get(int_2 - 100).isEmpty()) {
                        player.inventory.armor.set(int_2 - 100, itemStack_1);
                    }else if(!player.world.isClient) {
                        player.world.spawnEntity(new ItemEntity(player.world, player.x, player.y, player.z, itemStack_1));
                    }
                } else if (int_2 >= 150 && int_2 < player.inventory.offHand.size() + 150) {
                    if(player.inventory.offHand.get(int_2 - 150).isEmpty()) {
                        player.inventory.offHand.set(int_2 - 150, itemStack_1);
                    }else if(!player.world.isClient) {
                        player.world.spawnEntity(new ItemEntity(player.world, player.x, player.y, player.z, itemStack_1));
                    }
                }
            }
        }
    }
}
