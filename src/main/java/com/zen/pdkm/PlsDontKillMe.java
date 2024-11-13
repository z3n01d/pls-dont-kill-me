package com.zen.pdkm;

import com.zen.pdkm.commands.Commands;
import com.zen.pdkm.events.EndServerTickEvent;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlsDontKillMe implements ModInitializer {
	public static final String MOD_ID = "pls-dont-kill-me";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final EndServerTickEvent END_SERVER_TICK = new EndServerTickEvent();

	@Override
	public void onInitialize() {
		Commands.register();
		LOGGER.info("Pls Don't Kill Me successfully initialized!");
		ServerLifecycleEvents.SERVER_STARTED.register(END_SERVER_TICK);
		ServerTickEvents.END_SERVER_TICK.register(END_SERVER_TICK);
	}
}