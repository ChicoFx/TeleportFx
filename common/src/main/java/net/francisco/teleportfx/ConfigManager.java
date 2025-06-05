package net.francisco.teleportfx;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {

    private static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir();
    private static final Path CONFIG_FILE = CONFIG_DIR.resolve(TeleportFX.MOD_ID + ".json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static ModConfig CONFIG;

    public static void loadConfig() {
        if (Files.exists(CONFIG_FILE)) {
            try (FileReader reader = new FileReader(CONFIG_FILE.toFile())) {
                CONFIG = GSON.fromJson(reader, ModConfig.class);
                if (CONFIG == null) {
                    TeleportFX.LOGGER.warn("Config file was empty or malformed. Loading defaults and creating new instance.");
                    CONFIG = new ModConfig(); // Cria uma nova instância se nulo
                }

                // Garante que todas as seções sejam inicializadas se não vierem do JSON
                // (útil para atualizações de config de versões anteriores)
                if (CONFIG.general == null) CONFIG.general = new ModConfig.GeneralSettings();
                if (CONFIG.particles == null) CONFIG.particles = new ModConfig.ParticleSettings();
                if (CONFIG.sound == null) CONFIG.sound = new ModConfig.SoundSettings();
                if (CONFIG.lightBeam == null) CONFIG.lightBeam = new ModConfig.LightBeamSettings();
                if (CONFIG.timing == null) CONFIG.timing = new ModConfig.TimingSettings();
                if (CONFIG.permissions == null) CONFIG.permissions = new ModConfig.PermissionSettings();
                if (CONFIG.coordinateValidation == null) CONFIG.coordinateValidation = new ModConfig.CoordinateValidationSettings();
                if (CONFIG.messages == null) CONFIG.messages = new ModConfig.MessageSettings();

                TeleportFX.LOGGER.info("Config loaded successfully from " + CONFIG_FILE.toString());
                // Salvar novamente garante que quaisquer novos campos com valores padrão sejam adicionados ao JSON existente.
                saveConfig();

            } catch (IOException e) {
                TeleportFX.LOGGER.error("Failed to read config file. Loading defaults.", e);
                loadDefaultConfigAndSave();
            } catch (com.google.gson.JsonSyntaxException e) {
                TeleportFX.LOGGER.error("Config file has syntax errors. Loading defaults and overwriting.", e);
                loadDefaultConfigAndSave();
            }
        } else {
            TeleportFX.LOGGER.info("Config file not found. Creating default config.");
            loadDefaultConfigAndSave();
        }
    }

    private static void loadDefaultConfigAndSave() {
        CONFIG = new ModConfig();
        saveConfig();
    }

    public static void saveConfig() {
        if (CONFIG == null) {
            TeleportFX.LOGGER.warn("Attempted to save a null config. Initializing to defaults before saving.");
            CONFIG = new ModConfig();
        }
        try {
            if (!Files.exists(CONFIG_DIR)) {
                Files.createDirectories(CONFIG_DIR);
            }
            try (FileWriter writer = new FileWriter(CONFIG_FILE.toFile())) {
                GSON.toJson(CONFIG, writer);
                // Não logar "Config saved" aqui se chamado de dentro do loadConfig para evitar spam.
                // O log de sucesso ou criação já é feito no loadConfig.
            }
        } catch (IOException e) {
            TeleportFX.LOGGER.error("Failed to save config file.", e);
        }
    }
}