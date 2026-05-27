package com.pulsevisual.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Path;

public class PulseConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir().resolve("pulsevisual.json");

    private static PulseConfig INSTANCE = new PulseConfig();

    // ─── HUD ───────────────────────────────────────────────
    public boolean hudEnabled = true;
    public boolean showHealth = true;
    public boolean showArmor = true;
    public boolean showSaturation = true;
    public boolean showExperience = true;
    public boolean showHotbar = true;
    public boolean customCrosshair = true;
    public int crosshairColor = 0xFFFFFFFF;
    public boolean showCoords = true;
    public boolean showFps = true;
    public boolean showPing = true;
    public boolean showTime = true;
    public boolean showBiome = true;
    public boolean showCompass = true;
    public boolean showSpeed = false;
    public boolean showPotions = true;
    public boolean customBossBar = true;
    public float hudScale = 1.0f;

    // ─── VISUAL ────────────────────────────────────────────
    public boolean motionBlur = true;
    public float motionBlurStrength = 0.6f;
    public int hitFlashColor = 0x80FF0000;
    public boolean damageOverlay = true;
    public boolean nightVisionTweak = true;
    public boolean fullBright = false;
    public boolean chromaMode = false;
    public float chromaSpeed = 1.0f;
    public boolean armorColorOverlay = true;
    public boolean lowFire = true;
    public boolean noPumpkinBlur = true;
    public boolean noNauseaEffect = true;
    public boolean noVoidFog = true;
    public boolean itemPhysics = true;

    // ─── CAMERA ────────────────────────────────────────────
    public boolean zoomEnabled = true;
    public boolean smoothZoom = true;
    public float zoomLevel = 4.0f;
    public boolean customFov = false;
    public float fovValue = 80.0f;
    public boolean sprintFovEffect = true;
    public boolean viewBobbing = true;
    public float handSwayStrength = 1.0f;

    // ─── GAMEPLAY ──────────────────────────────────────────
    public boolean fpsLimit = false;
    public int fpsLimitValue = 120;

    // ─── SERIALIZATION ─────────────────────────────────────
    public static PulseConfig get() {
        return INSTANCE;
    }

    public static void load() {
        File file = CONFIG_PATH.toFile();
        if (file.exists()) {
            try (Reader r = new FileReader(file)) {
                PulseConfig loaded = GSON.fromJson(r, PulseConfig.class);
                if (loaded != null) INSTANCE = loaded;
            } catch (Exception e) {
                System.err.println("[PulseVisual] Failed to load config: " + e.getMessage());
            }
        }
        save();
    }

    public static void save() {
        try (Writer w = new FileWriter(CONFIG_PATH.toFile())) {
            GSON.toJson(INSTANCE, w);
        } catch (Exception e) {
            System.err.println("[PulseVisual] Failed to save config: " + e.getMessage());
        }
    }
}
