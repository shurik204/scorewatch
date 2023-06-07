package me.shurik.scorewatch.mixin;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.shurik.scorewatch.subscription.ScoreSubscriptions;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.server.function.FunctionLoader;
import net.minecraft.util.profiler.Profiler;

// Reset global subscriptions on reload
@Mixin(FunctionLoader.class)
public class FunctionLoaderMixin {
    @Inject(at = @At("HEAD"), method = "reload")
    void reload(ResourceReloader.Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor, CallbackInfoReturnable<CompletableFuture<Void>> info) {
        ScoreSubscriptions.resetGlobal();
    }
}