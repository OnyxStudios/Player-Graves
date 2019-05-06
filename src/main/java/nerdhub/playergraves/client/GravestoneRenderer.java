package nerdhub.playergraves.client;

import com.mojang.blaze3d.platform.GlStateManager;
import nerdhub.playergraves.PlayerGraves;
import nerdhub.playergraves.blocks.BlockEntityGravestone;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.util.math.MathHelper;

public class GravestoneRenderer extends BlockEntityRenderer<BlockEntityGravestone> {

    @Override
    public void render(BlockEntityGravestone tile, double x, double y, double z, float float_1, int int_1) {
        super.render(tile, x, y, z, float_1, int_1);

        if(PlayerGraves.config.getBoolean("render-skull") && tile.playerName != null && !FabricLoader.getInstance().isDevelopmentEnvironment()) {
            ItemStack skull = new ItemStack(Items.PLAYER_HEAD);
            skull.setTag(new CompoundTag());
            skull.getTag().put("SkullOwner", new StringTag(tile.playerName));

            GlStateManager.pushMatrix();
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.translated(x + 0.5, y + 0.35, z + 0.5);

            GlStateManager.rotatef((-MathHelper.lerp(1, MinecraftClient.getInstance().player.prevYaw, MinecraftClient.getInstance().player.yaw)) + 180, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotatef(MathHelper.lerp(1, -MinecraftClient.getInstance().player.prevPitch, -MinecraftClient.getInstance().player.pitch), 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
            this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);

            GlStateManager.scalef(0.75f, 0.75f, 0.75f);
            GlStateManager.pushLightingAttributes();
            GlStateManager.disableLighting();
            MinecraftClient.getInstance().getItemRenderer().renderItem(skull, ModelTransformation.Type.FIXED);
            GlStateManager.enableLighting();
            GlStateManager.popAttributes();
            GlStateManager.popMatrix();
        }
    }

    @Override
    public boolean method_3563(BlockEntityGravestone blockEntity_1) {
        return true;
    }
}
