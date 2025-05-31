package com.kara251.deathcoords;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeathCoordinatesMod implements ModInitializer {
    public static final String MOD_ID = "deathcoords";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Death Coordinates mod initialized!");
        
        // 注册玩家死亡事件监听器
        ServerLivingEntityEvents.AFTER_DEATH.register(this::onPlayerDeath);
    }

    private void onPlayerDeath(LivingEntity entity, DamageSource damageSource) {
        // 检查死亡的实体是否为玩家
        if (entity instanceof ServerPlayerEntity player) {
            // 获取玩家死亡位置
            BlockPos deathPos = player.getBlockPos();
            String playerName = player.getName().getString();
            String dimensionName = getDimensionName(player);
            
            // 创建死亡坐标消息
            String message = String.format("§c[死亡坐标] §f%s 在 %s 死亡了！坐标: §e%d, %d, %d", 
                playerName, dimensionName, deathPos.getX(), deathPos.getY(), deathPos.getZ());
            
            // 向所有玩家广播消息
            Text broadcastMessage = Text.literal(message);
            player.getServer().getPlayerManager().broadcast(broadcastMessage, false);
            
            LOGGER.info("Player {} died at coordinates: {}, {}, {} in dimension: {}", 
                playerName, deathPos.getX(), deathPos.getY(), deathPos.getZ(), dimensionName);
        }
    }

    private String getDimensionName(ServerPlayerEntity player) {
        String dimensionId = player.getServerWorld().getRegistryKey().getValue().toString();
        
        return switch (dimensionId) {
            case "minecraft:overworld" -> "主世界";
            case "minecraft:the_nether" -> "下界";
            case "minecraft:the_end" -> "末地";
            default -> dimensionId.replace("minecraft:", "");
        };
    }
}