package me.shurik.scorewatch.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.shurik.scorewatch.ScorewatchMod;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;

// Store currently running function
@Mixin(targets = "net/minecraft/server/function/CommandFunctionManager$Execution")
public class CommandFunctionManager$ExecutionMixin {
    @Inject(at = @At("HEAD"), method = "recursiveRun")
    void pushFunctionId(CommandFunction function, ServerCommandSource source, CallbackInfo info) {
        ScorewatchMod.functionStack.push(function.getId()); //  = function.getId();
    }

    @Inject(at = @At("TAIL"), method = "recursiveRun")
    void popFunctionId(CommandFunction function, ServerCommandSource source, CallbackInfo info) {
        ScorewatchMod.functionStack.pop();
    }
}