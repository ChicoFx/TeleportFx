package net.francisco.teleportfx;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TeleportFX implements ModInitializer {
	public static final String MOD_ID = "tpmod"; // Certifique-se que este é o ID do seu mod
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("TPMOD carregado e inicializando...");

		ConfigManager.loadConfig(); // Carrega ou cria a configuração

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			TeleportCommands.register(dispatcher, environment.dedicated);
		});

		LOGGER.info("TPMOD inicialização completa!");
	}
}