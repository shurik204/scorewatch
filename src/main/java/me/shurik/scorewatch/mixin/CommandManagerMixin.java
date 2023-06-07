package me.shurik.scorewatch.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;

import me.shurik.scorewatch.ScorewatchMod;
import me.shurik.scorewatch.command.ScorewatchCommand;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.server.command.ServerCommandSource;

// Hacky way to know what command is being executed
@Mixin(CommandManager.class)
public class CommandManagerMixin {
    @Shadow
    @Final
    private CommandDispatcher<ServerCommandSource> dispatcher;

    @Inject(at = @At("TAIL"), method = "<init>")
    private void registerCommands(RegistrationEnvironment environment, CommandRegistryAccess commandRegistryAccess, CallbackInfo info) {
        ScorewatchCommand.register(dispatcher);
    }

    @Inject(at = @At("HEAD"), method = "execute")
    private void storeChatCommand(ParseResults<ServerCommandSource> parseResults, String command, CallbackInfoReturnable<Integer> info) {
        ScorewatchMod.currentCommand = command;
    }
}