package me.shurik.scorewatch.command;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import me.shurik.scorewatch.subscription.ScoreSubscription;
import me.shurik.scorewatch.subscription.ScoreSubscriptions;
import me.shurik.scorewatch.utils.TextUtils;
import net.minecraft.command.argument.ScoreHolderArgumentType;
import net.minecraft.command.argument.ScoreboardObjectiveArgumentType;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class ScorewatchCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> rootNode = CommandManager.literal("scorewatch");
        rootNode.requires(source -> source.hasPermissionLevel(2));

        rootNode.executes(ctx -> listPlayerSubscriptions(ctx, ctx.getSource().getPlayerOrThrow()));

        rootNode.then(
            CommandManager.literal("subscribe")
                .then(
                    CommandManager.argument("objective", ScoreboardObjectiveArgumentType.scoreboardObjective())
                        .executes(ctx -> subscribeToScore(ctx, ctx.getSource().getPlayerOrThrow(), ScoreboardObjectiveArgumentType.getObjective(ctx, "objective"), null))
                        .then(
                            CommandManager.argument("scoreHolder", ScoreHolderArgumentType.scoreHolder())
                                .executes(ctx -> subscribeToScore(ctx, ctx.getSource().getPlayerOrThrow(), ScoreboardObjectiveArgumentType.getObjective(ctx, "objective"), ScoreHolderArgumentType.getScoreHolder(ctx, "scoreHolder")))
                        )
                )
        );

        rootNode.then(
            CommandManager.literal("unsubscribe")
                .executes(ctx -> unsubscribeFromAllScores(ctx, ctx.getSource().getPlayerOrThrow()))
                .then(
                    CommandManager.argument("objective", ScoreboardObjectiveArgumentType.scoreboardObjective())
                        .executes(ctx -> unsubscribeFromScore(ctx, ctx.getSource().getPlayerOrThrow(), ScoreboardObjectiveArgumentType.getObjective(ctx, "objective"), null))
                        .then(
                            CommandManager.argument("scoreHolder", ScoreHolderArgumentType.scoreHolder())
                                .executes(ctx -> unsubscribeFromScore(ctx, ctx.getSource().getPlayerOrThrow(), ScoreboardObjectiveArgumentType.getObjective(ctx, "objective"), ScoreHolderArgumentType.getScoreHolder(ctx, "scoreHolder")))
                        )
                )
        );

        rootNode.then(
            CommandManager.literal("list")
                .executes(ctx -> listPlayerSubscriptions(ctx, ctx.getSource().getPlayerOrThrow()))
        );

        LiteralArgumentBuilder<ServerCommandSource> globalNode = CommandManager.literal("global");

        globalNode.executes(ctx -> listGlobalSubscriptions(ctx));

        globalNode.then(CommandManager.literal("list").executes(ctx -> listGlobalSubscriptions(ctx)));
        globalNode.then(CommandManager.literal("disable").executes(ctx -> disableGlobalSubscriptions(ctx)));
        globalNode.then(CommandManager.literal("enable").executes(ctx -> enableGlobalSubscriptions(ctx)));

        rootNode.then(globalNode);

        dispatcher.register(rootNode);
    }

    private static int subscribeToScore(CommandContext<ServerCommandSource> ctx, ServerPlayerEntity player, ScoreboardObjective objective, @Nullable String scoreHolder) {
        if (ScoreSubscriptions.subscribe(player.getUuid(), objective.getName(), scoreHolder)) {
            ctx.getSource().sendFeedback(() -> TextUtils.format("Started watching score %s", ScoreSubscription.format(objective, scoreHolder)), false);
        } else {
            boolean watchingObjective = false;
            for (ScoreSubscription sub : ScoreSubscriptions.getSubscriptions(player.getUuid())) {
                if (sub.objective.equals(objective.getName()) && sub.scoreHolder == null) {
                    watchingObjective = true;
                    break;
                }
            }

            final boolean finalWatchingObjective = watchingObjective;
            ctx.getSource().sendFeedback(() -> TextUtils.format("You're already watching score %s", ScoreSubscription.format(objective, finalWatchingObjective ? null : scoreHolder)), false);
        }

        return 1;
    }

    private static int unsubscribeFromScore(CommandContext<ServerCommandSource> ctx, ServerPlayerEntity player, ScoreboardObjective objective, @Nullable String scoreHolder) {
        if (ScoreSubscriptions.unsubscribe(player.getUuid(), objective.getName(), scoreHolder)) {
            ctx.getSource().sendFeedback(() -> TextUtils.format("Stopped watching score %s", ScoreSubscription.format(objective, scoreHolder)), false);
        } else {
            ctx.getSource().sendFeedback(() -> TextUtils.coloredFormat("You're not watching score %s", "red", ScoreSubscription.format(objective, scoreHolder)), false);
        }

        return 1;
    }

    private static int unsubscribeFromAllScores(CommandContext<ServerCommandSource> ctx, ServerPlayerEntity player) {
        if (ScoreSubscriptions.unsubscribeAll(player.getUuid())) {
            ctx.getSource().sendFeedback(() -> TextUtils.withDefaultPrefix("Stopped watching all scoreboard objectives"), false);
        } else {
            ctx.getSource().sendFeedback(() -> TextUtils.coloredFormat("You're not watching any scores", "red"), false);
        }
        return 1;
    }

    private static int listPlayerSubscriptions(CommandContext<ServerCommandSource> ctx, ServerPlayerEntity player) {
        List<ScoreSubscription> subscriptions = ScoreSubscriptions.getSubscriptions(player.getUuid());

        if (subscriptions.size() == 0) {
            ctx.getSource().sendFeedback(() -> TextUtils.coloredFormat("You're not watching any scores", "red"), false);
            return 1;
        }

        ctx.getSource().sendFeedback(() -> TextUtils.format("Watched scores (%s):", subscriptions.size()), false);
        for (ScoreSubscription sub: subscriptions) {
            ctx.getSource().sendFeedback(() -> Text.of("    - " + sub.toString()), false);
        }

        return 1;
    }

    private static int listGlobalSubscriptions(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        List<ScoreSubscription> subscriptions = ScoreSubscriptions.getGlobalSubscriptions();
        
        if (subscriptions.size() == 0) {
            ctx.getSource().sendFeedback(() -> TextUtils.coloredFormat("No global watched scores found.", "red"), false);
            ctx.getSource().sendFeedback(() -> TextUtils.coloredFormat("You can define them in functions like this:", "gray"), false);
            ctx.getSource().sendFeedback(() -> TextUtils.coloredFormat("#scorewatch <objective> [scoreHolder]", "gray"), false);
            return 1;
        }

        ctx.getSource().sendFeedback(() -> TextUtils.format("Global watched scores (%s):", subscriptions.size()), false);
        for (ScoreSubscription sub: subscriptions) {
            ctx.getSource().sendFeedback(() -> Text.of("    - " + sub.toString()), false);
        }

        return 1;
    }

    private static int enableGlobalSubscriptions(CommandContext<ServerCommandSource> ctx) {
        if (!ScoreSubscriptions.globalEnabled()) {
            ScoreSubscriptions.switchGlobal();
            ctx.getSource().sendFeedback(() -> TextUtils.withDefaultPrefix("Enabled global watched scores."), false);
        } else {
            ctx.getSource().sendFeedback(() -> TextUtils.coloredFormat("Global watched scores are already enabled.", "red"), false);
        }

        return 1;
    }

    private static int disableGlobalSubscriptions(CommandContext<ServerCommandSource> ctx) {
        if (ScoreSubscriptions.globalEnabled()) {
            ScoreSubscriptions.switchGlobal();
            ctx.getSource().sendFeedback(() -> TextUtils.withDefaultPrefix("Disabled global watched scores."), false);
        } else {
            ctx.getSource().sendFeedback(() -> TextUtils.coloredFormat("Global watched scores are already disabled.", "red"), false);
        }

        return 1;
    }
}