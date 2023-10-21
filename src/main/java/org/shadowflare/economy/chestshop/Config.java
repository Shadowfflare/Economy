package org.shadowflare.economy.chestshop;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

public class Config {
    private final Plugin plugin;
    private final String fileName;
    private FileConfiguration configuration;
    private File configFile;

    public Config(Plugin plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
        configFile = new File(plugin.getDataFolder(), this.fileName);
    }

    public void saveDefaultConfig() {
        if (!configFile.exists()) {
            plugin.saveResource(fileName, false);
        }
    }

    public void reloadConfig() {
        configuration = YamlConfiguration.loadConfiguration(configFile);
        InputStreamReader defaultConfigStream = new InputStreamReader(plugin.getResource(fileName), StandardCharsets.UTF_8);
        if (defaultConfigStream != null) {
            configuration.setDefaults(YamlConfiguration.loadConfiguration(defaultConfigStream));
        }
    }

    public FileConfiguration config() {
        if (configuration == null) {
            reloadConfig();
        }
        return configuration;
    }

    public void saveConfig() {
        if (configuration == null) {
            return;
        }
        try {
            config().save(configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to " + configFile, e);
        }
    }
}
