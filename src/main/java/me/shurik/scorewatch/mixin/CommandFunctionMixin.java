package me.shurik.scorewatch.mixin;

import static me.shurik.scorewatch.ScorewatchMod.LOGGER;

import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import me.shurik.scorewatch.subscription.ScoreSubscription;
import me.shurik.scorewatch.subscription.ScoreSubscriptions;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.util.Identifier;

// Add score subscription using #scorewatch comments
@Debug(export = true)
@Mixin(CommandFunction.class)
public class CommandFunctionMixin {
    @ModifyVariable(at = @At("STORE"), method = "create", ordinal = 0)
    private static String modifyString(String command, Identifier id) {
        if (command.startsWith("#scorewatch")) {
            String[] args = command.split(" ");
            String objective;
            String scoreHolder = null;

            if (args.length == 2) {
                objective = args[1];
            } else if (args.length == 3) {
                objective = args[1];
                scoreHolder = args[2];
            } else {
                return command;
            }

            if (!ScoreSubscriptions.subscribeGlobal(objective, scoreHolder)) {
                // java.util.MissingFormatArgumentException: Format specifier '%s'
                LOGGER.warn(String.format("Subscription for %s already exists. (Defined in %s)", ScoreSubscription.format(objective, scoreHolder), id.toString()));
            } else {
                LOGGER.info(String.format("Added subscription for %s. (Defined in %s)", ScoreSubscription.format(objective, scoreHolder), id.toString()));
            }
        }

        return command;
    }
}