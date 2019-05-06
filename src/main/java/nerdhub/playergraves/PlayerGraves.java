package nerdhub.playergraves;

import com.mojang.brigadier.arguments.StringArgumentType;
import nerdhub.playergraves.blocks.BlockEntityGravestone;
import nerdhub.playergraves.blocks.BlockGravestone;
import nerdhub.playergraves.data.PlayerInventoryPersistentState;
import nerdhub.playergraves.events.GravesEventHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.CommandRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Style;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class PlayerGraves implements ModInitializer {

    public static final String MODID = "playergraves";
    public static Config config = new Config();
    public static BlockGravestone BLOCK_GRAVESTONE = new BlockGravestone();
    public static BlockEntityType<BlockEntityGravestone> GRAVESTONE = BlockEntityType.Builder.create(BlockEntityGravestone::new).build(null);

    @Override
    public void onInitialize() {
        Registry.register(Registry.BLOCK, new Identifier(MODID, "gravestone"), BLOCK_GRAVESTONE);
        Registry.register(Registry.ITEM, new Identifier(MODID, "gravestone"), new BlockItem(BLOCK_GRAVESTONE, new Item.Settings()));
        Registry.register(Registry.BLOCK_ENTITY, new Identifier(MODID, "blockentity_gravestone"), GRAVESTONE);
        GravesEventHandler.registerEventHandlers();

        CommandRegistry.INSTANCE.register(false, serverCommandSourceCommandDispatcher -> serverCommandSourceCommandDispatcher.register(
                CommandManager.literal("recover")
                        .requires(source -> source.hasPermissionLevel(4))
                        .then(CommandManager.argument("name", StringArgumentType.string())
                                .executes(context -> {
                                    ServerPlayerEntity targetPlayer = context.getSource().getMinecraftServer().getPlayerManager().getPlayer(StringArgumentType.getString(context, "name"));
                                    ServerPlayerEntity senderPlayer = context.getSource().getPlayer();
                                    ServerWorld world = context.getSource().getWorld();
                                    PlayerInventoryPersistentState persistentState = PlayerInventoryPersistentState.get(world);

                                    if (targetPlayer != null) {
                                        if (persistentState.isPlayerInventorySaved(targetPlayer)) {
                                            BlockPos deathPos = GravesEventHandler.findValidPos(world, senderPlayer.getBlockPos());

                                            if (deathPos != null) {
                                                world.setBlockState(deathPos, PlayerGraves.BLOCK_GRAVESTONE.getDefaultState().with(BlockGravestone.FACING, senderPlayer.getHorizontalFacing().getOpposite()));
                                                BlockEntity blockEntity = world.getBlockEntity(deathPos);

                                                if (blockEntity instanceof BlockEntityGravestone) {
                                                    ((BlockEntityGravestone) blockEntity).playerName = targetPlayer.getEntityName();
                                                    ((BlockEntityGravestone) blockEntity).playerInv = persistentState.getPlayerInventory(targetPlayer);
                                                    blockEntity.markDirty();
                                                    world.updateListeners(deathPos, world.getBlockState(deathPos), world.getBlockState(deathPos), 3);
                                                }

                                                senderPlayer.addChatMessage(new TranslatableTextComponent("graves.spawnedgrave", deathPos.getX(), deathPos.getY(), deathPos.getZ()).setStyle(new Style().setColor(TextFormat.GOLD)), false);
                                            } else {
                                                senderPlayer.addChatMessage(new TranslatableTextComponent("graves.nullspawn").setStyle(new Style().setColor(TextFormat.LIGHT_PURPLE)), false);
                                            }
                                        } else {
                                            senderPlayer.addChatMessage(new TranslatableTextComponent("graves.nullinv").setStyle(new Style().setColor(TextFormat.LIGHT_PURPLE)), false);
                                        }
                                    } else {
                                        senderPlayer.addChatMessage(new TranslatableTextComponent("graves.nullplayer").setStyle(new Style().setColor(TextFormat.LIGHT_PURPLE)), false);
                                    }
                                    return 1;
                                })
                        )));
    }
}
