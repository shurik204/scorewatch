package me.shurik.scorewatch;

import net.fabricmc.api.ModInitializer;
import net.minecraft.server.PlayerManager;
import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class ScorewatchMod implements ModInitializer {
	// Mod ID
	public static final String MOD_ID = "scorewatch";
	
	// Logger
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	
	// Stores the currently executed command
	// Since Minecraft's commands are single-threaded, this *should* be fine
	public static String currentCommand = "";
	// Stores the currently executed function id
	public static ObjectArrayList<Identifier> functionStack = new ObjectArrayList<>();
	// public static Identifier currentFunction = null;
	// I need a reference to the player list
	// Bonus: it also provides MinecraftServer
	public static PlayerManager playerManager = null;

	@Override
	public void onInitialize() {}
}