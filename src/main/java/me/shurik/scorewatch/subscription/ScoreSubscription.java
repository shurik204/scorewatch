package me.shurik.scorewatch.subscription;

import org.jetbrains.annotations.Nullable;

import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.Text;

public class ScoreSubscription {
    public final String objective;
    @Nullable
    public final String scoreHolder;

    public ScoreSubscription(String objective, @Nullable String scoreHolder) {
        this.scoreHolder = scoreHolder;
        this.objective = objective;
    }

    public boolean match(String objective, @Nullable String scoreHolder) {
        return (this.scoreHolder == null || this.scoreHolder.equals(scoreHolder)) && this.objective.equals(objective);
    }

    public boolean exactMatch(String objective, @Nullable String scoreHolder) {
        if (this.scoreHolder != null && scoreHolder != null) {
            return this.objective.equals(objective) && this.scoreHolder.equals(scoreHolder);
        } else if (this.scoreHolder == null && scoreHolder == null) {
            return this.objective.equals(objective);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return format(objective, scoreHolder);
    }

    public static String format(String objective, @Nullable String scoreHolder) {
        return scoreHolder == null ? objective : objective + " (" + scoreHolder + ")";
    }

    public static Text format(ScoreboardObjective objective, @Nullable String scoreHolder) {
        return scoreHolder == null ? objective.toHoverableText() : Text.translatable("%s (%s)", objective.toHoverableText(), scoreHolder);
    }
}