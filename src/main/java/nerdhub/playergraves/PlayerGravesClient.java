package nerdhub.playergraves;

import nerdhub.playergraves.blocks.BlockEntityGravestone;
import nerdhub.playergraves.client.GravestoneRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.render.BlockEntityRendererRegistry;

public class PlayerGravesClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.INSTANCE.register(BlockEntityGravestone.class, new GravestoneRenderer());
    }
}
