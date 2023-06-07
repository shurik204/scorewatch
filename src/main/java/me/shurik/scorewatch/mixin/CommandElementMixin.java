package me.shurik.scorewatch.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import me.shurik.scorewatch.ScorewatchMod;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction.CommandElement;
import net.minecraft.server.function.CommandFunctionManager;

// Hacky way to know what command is being executed
@Mixin(CommandElement.class)
public class CommandElementMixin {
    @Shadow
    public String toString() {return null;}

    // execute(Lnet/minecraft/server/function/CommandFunctionManager;Lnet/minecraft/server/command/ServerCommandSource;)I
    @Inject(at = @At("HEAD"), method = "execute(Lnet/minecraft/server/function/CommandFunctionManager;Lnet/minecraft/server/command/ServerCommandSource;)I")
    void storeFunctionCommand(CommandFunctionManager manager, ServerCommandSource source, CallbackInfoReturnable<Integer> info) throws CommandSyntaxException {
        ScorewatchMod.currentCommand = toString();
    }
}