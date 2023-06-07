package me.shurik.scorewatch.mixin;

import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.UUID;
import java.util.function.UnaryOperator;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.shurik.scorewatch.ScorewatchMod;
import me.shurik.scorewatch.subscription.ScoreSubscription;
import me.shurik.scorewatch.subscription.ScoreSubscriptions;
import me.shurik.scorewatch.utils.TextUtils;

@Mixin(ScoreboardPlayerScore.class)
public class ScoreboardPlayerScoreMixin {
	@Shadow
	@Final
	private String playerName;

	@Shadow
	@Final
	@Nullable
	private ScoreboardObjective objective;

	@Shadow
	private int score;

	// setScore(int score)
	@Inject(at = @At("HEAD"), method = "setScore")
	void onScoreChanged(int newScore, CallbackInfo info) {
		// Prioritize global subscriptions
		if (ScoreSubscriptions.globalEnabled() && ScoreSubscriptions.isGloballySubscribed(objective.getName(), playerName)) {
			ScorewatchMod.playerManager.broadcast(getText(newScore), false);
			return;
		}

		for (UUID playerUuid : ScoreSubscriptions.getSubscribers(objective.getName(), playerName)) {
			ServerPlayerEntity player = ScorewatchMod.playerManager.getPlayer(playerUuid);
			if (player == null) continue;
			// TODO: config: log score initialization

			player.sendMessage(getText(newScore), false);
		}
	}

	private Text getText(int newScore) {
		MutableText text = this.score == 0 && newScore == 0 ? TextUtils.shortFormat("%s: init", ScoreSubscription.format(objective, playerName)) : TextUtils.shortFormat("%s: %s -> %s", ScoreSubscription.format(objective, playerName), this.score, newScore);
		return text.styled(HOVER_TEXT_INFO_STYLER);
	}

	private static final UnaryOperator<Style> HOVER_TEXT_INFO_STYLER = (style) -> {
		if (ScorewatchMod.functionStack.isEmpty())
			return style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of(ScorewatchMod.currentCommand)));
		else
			return style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of(TextUtils.functionStackString() + "\n" + ScorewatchMod.currentCommand)));
	};
}