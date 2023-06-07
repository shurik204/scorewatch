package me.shurik.scorewatch.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import me.shurik.scorewatch.ScorewatchMod;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.function.CommandFunctionManager;

// Reset stored function on new function tick
// Store currently running function
@Mixin(CommandFunctionManager.class)
public class CommandFunctionManagerMixin {
    @Inject(at=@At("HEAD"), method="tick")
    void tick(CallbackInfo info) {
        // Reset currently running function
        ScorewatchMod.functionStack.clear();
    }

    @Inject(at=@At("HEAD"), method="execute(Lnet/minecraft/server/function/CommandFunction;Lnet/minecraft/server/command/ServerCommandSource;Lnet/minecraft/server/function/CommandFunctionManager$Tracer;)I")
    void pushFunctionId(CommandFunction function, ServerCommandSource source, CommandFunctionManager.Tracer tracer, CallbackInfoReturnable<Integer> info) throws CommandSyntaxException {
        ScorewatchMod.functionStack.push(function.getId());
    }

    @Inject(at=@At("TAIL"), method="execute(Lnet/minecraft/server/function/CommandFunction;Lnet/minecraft/server/command/ServerCommandSource;Lnet/minecraft/server/function/CommandFunctionManager$Tracer;)I")
    void popFunctionId(CommandFunction function, ServerCommandSource source, CommandFunctionManager.Tracer tracer, CallbackInfoReturnable<Integer> info) throws CommandSyntaxException {
        ScorewatchMod.functionStack.pop();
    }
}