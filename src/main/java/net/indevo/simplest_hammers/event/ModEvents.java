package net.indevo.simplest_hammers.event;

import net.indevo.simplest_hammers.SimplestHammers;
import net.indevo.simplest_hammers.item.custom.HammerItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

import java.util.HashSet;
import java.util.Set;

@EventBusSubscriber(modid = SimplestHammers.MODID)
public class ModEvents {
    // Done with the help of https://github.com/CoFH/CoFHCore/blob/1.19.x/src/main/java/cofh/core/event/AreaEffectEvents.java
    // Don't be a jerk License
    private static final Set<BlockPos> HARVESTED_BLOCKS = new HashSet<>();
    @SubscribeEvent
    public static void onHammerUsage(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        ItemStack mainHandItem = player.getMainHandItem();

        if(mainHandItem.getItem() instanceof HammerItem hammer && player instanceof ServerPlayer serverPlayer) {
            BlockPos initialBlockPos = event.getPos();
            if (HARVESTED_BLOCKS.contains(initialBlockPos)) {
                return;
            }
            if(!hammer.isCorrectToolForDrops(mainHandItem, event.getLevel().getBlockState(initialBlockPos))) {
                return;
            }

            // var blocksToMine = HammerItem.getBlocksToBeDestroyed(1, initialBlockPos, serverPlayer);
            // SimplestHammers.getLogger().info("%d blocks to mine".formatted(blocksToMine.size()));
            for (BlockPos pos : HammerItem.getBlocksToBeDestroyed(1, initialBlockPos, serverPlayer)) {
                if(!hammer.isCorrectToolForDrops(mainHandItem, event.getLevel().getBlockState(pos))) {
                    // event.setCanceled(true);
                    continue;
                }
                if(pos.equals(initialBlockPos)) {
                    SimplestHammers.getLogger().warn("Mined block that was already set to be mined");
                    // event.setCanceled(true);
                    // continue;
                }
                // Have to add them to a Set otherwise, the same code right here will get called for each block!
                HARVESTED_BLOCKS.add(pos);
                // Level level = serverPlayer.level();
                // level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, pos, 0);
                SimplestHammers.getLogger().info("Adding particles");
                /*Level level = serverPlayer.level();
                BlockState blockState = level.getBlockState(pos);
                level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockState), (float) pos.getX(), (float) pos.getY(), (float) pos.getZ(), 0.0, 0.0, 0.0);*/
                serverPlayer.gameMode.destroyBlock(pos);
                HARVESTED_BLOCKS.remove(pos);
            }
        }
    }
}