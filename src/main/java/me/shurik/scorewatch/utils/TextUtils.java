package me.shurik.scorewatch.utils;

import me.shurik.scorewatch.ScorewatchMod;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class TextUtils {
    //                                                                                                🔎
    public static final MutableText SHORT_PREFIX = Text.translatable("[%s]", Text.literal("🔎").styled(style -> style.withColor(Formatting.GREEN))).styled(style -> style.withColor(Formatting.GREEN));
    public static final MutableText DEFAULT_PREFIX = Text.translatable("[%s %s]", Text.literal("🔎").styled(style -> style.withColor(Formatting.GREEN)), Text.literal("Scorewatch").styled(style -> style.withColor(Formatting.GOLD))).styled(style -> style.withColor(Formatting.GOLD));

    public static MutableText withDefaultPrefix(Text text) {
        return Text.translatable("%s %s", DEFAULT_PREFIX, text);
    }

    public static MutableText withDefaultPrefix(String string) {
        return Text.translatable("%s %s", DEFAULT_PREFIX, string);
    }

    public static MutableText withShortPrefix(Text text) {
        return Text.translatable("%s %s", SHORT_PREFIX, text);
    }

    public static MutableText withShortPrefix(String string) {
        return Text.translatable("%s %s", SHORT_PREFIX, string);
    }

    public static MutableText format(String string, Object... args) {
        return withDefaultPrefix(Text.translatable(string, args));
    }

    public static MutableText shortFormat(String string, Object... args) {
        return withShortPrefix(Text.translatable(string, args));
    }

    public static MutableText coloredFormat(String string, Formatting formatting, Object... args) {
        return withDefaultPrefix(Text.translatable(string, args).styled(style -> style.withColor(formatting)));
    }

    public static MutableText coloredFormat(String string, String color, Object... args) {
        return withDefaultPrefix(Text.translatable(string, args).styled(style -> style.withColor(TextColor.parse(color))));
    }

    public static String functionStackString() {
        String result = "Call stack:";
        // Iterate over the function stack with index
        // Add function id to the text
        // One function id per line
        // Prepend each line with indentation based on the index
        int indent = 1;
        int recursion = 0;
        for (int i = 0; i < ScorewatchMod.functionStack.size(); i++) {
            // Check for repeated function ids
            Identifier id = ScorewatchMod.functionStack.get(i);
            if (i + 1 < ScorewatchMod.functionStack.size() && id.equals(ScorewatchMod.functionStack.get(i + 1))) {
                recursion++;
                continue;
            }

            // Add the function id to the text
            if (recursion > 0) {
                result += String.format("\n%s%s (🔁 x%s)", "  ".repeat(indent++), id.toString(), recursion + 1);
                recursion = 0;
            } else {
                result += String.format("\n%s%s", "  ".repeat(indent++), id.toString());
            }
        }

        return result;
    }
}