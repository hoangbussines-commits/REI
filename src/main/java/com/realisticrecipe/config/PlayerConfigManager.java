package com.realisticrecipe.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.realisticrecipe.RealisticRecipe;
import com.realisticrecipe.item.reader.ReaderMode;
import net.minecraft.world.entity.player.Player;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class PlayerConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_DIR = Paths.get("config", RealisticRecipe.MODID, "player");

    public static class PlayerData {
        public UUID uuid;
        public String name;
        public ReaderMode mode = ReaderMode.ENERGY;

        public PlayerData() {}

        public PlayerData(UUID uuid, String name, ReaderMode mode) {
            this.uuid = uuid;
            this.name = name;
            this.mode = mode;
        }
    }

    public static void save(Player player, ReaderMode mode) {
        try {
            Files.createDirectories(CONFIG_DIR);

            PlayerData data = new PlayerData(player.getUUID(), player.getName().getString(), mode);

            Path file = CONFIG_DIR.resolve(player.getUUID().toString() + ".json");
            try (Writer writer = new FileWriter(file.toFile())) {
                GSON.toJson(data, writer);
            }
        } catch (IOException e) {
            System.err.println("[REI] Failed to save config for player " + player.getName().getString());
        }
    }

    public static ReaderMode load(Player player) {
        Path file = CONFIG_DIR.resolve(player.getUUID().toString() + ".json");
        if (!Files.exists(file)) {
            return ReaderMode.ENERGY;
        }

        try (Reader reader = new FileReader(file.toFile())) {
            PlayerData data = GSON.fromJson(reader, PlayerData.class);
            return data.mode != null ? data.mode : ReaderMode.ENERGY;
        } catch (IOException e) {
            System.err.println("[REI] Failed to load config for player " + player.getName().getString());
            return ReaderMode.ENERGY;
        }
    }
}