package me.shurik.scorewatch.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.shurik.scorewatch.ScorewatchMod;
import me.shurik.scorewatch.subscription.ScoreSubscriptions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;

// Store PlayerManager instance for later
@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    void resetPlayerManager(CallbackInfo info) {
        ScorewatchMod.playerManager = null;
    }

    @Inject(method = "shutdown", at = @At("HEAD"))
    void resetSubscriptions(CallbackInfo info) {
        ScoreSubscriptions.reset();
    }

    @Inject(method = "setPlayerManager", at = @At("HEAD"))
    void storePlayerManager(PlayerManager playerManager, CallbackInfo info) {
        ScorewatchMod.playerManager = playerManager;
    }
}