package com.khazoda.breakerplacer;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class BreakerPlacerConfig {
  private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("breakerplacer.properties");
  private static BreakerPlacerConfig INSTANCE;
  private final Properties properties;

  private BreakerPlacerConfig() {
    this.properties = new Properties();
  }

  public static BreakerPlacerConfig getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new BreakerPlacerConfig();
    }
    return INSTANCE;
  }

  public void load() {
    if (Files.exists(CONFIG_PATH)) {
      try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
        properties.load(reader);
      } catch (IOException e) {
        Constants.LOG.error("[BreakerPlacer] Failed to load config: {}", e.getMessage());
      }
    }

    boolean modified = false;

    if (!properties.containsKey("tool_takes_damage")) {
      properties.setProperty("tool_takes_damage", "false");
      modified = true;
    }

    if (modified || !Files.exists(CONFIG_PATH)) {
      save();
    }
  }

  public void save() {
    try {
      Files.createDirectories(CONFIG_PATH.getParent());
      try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
        writer.write("# Block Breaker & Placer Configuration\n\n");

        writer.write("# If true, the tool in the breaker will lose durability when breaking blocks.\n");
        writer.write("# Default: false\n");
        writer.write("tool_takes_damage=" + properties.getProperty("tool_takes_damage") + "\n");
      }
    } catch (IOException e) {
      Constants.LOG.error("[BreakerPlacer] Failed to save config: {}", e.getMessage());
    }
  }

  public boolean toolTakesDamage() {
    return Boolean.parseBoolean(properties.getProperty("tool_takes_damage", "false"));
  }

  public void setToolTakesDamage(boolean value) {
    properties.setProperty("tool_takes_damage", String.valueOf(value));
  }
}