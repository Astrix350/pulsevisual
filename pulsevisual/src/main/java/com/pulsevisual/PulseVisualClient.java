package com.pulsevisual;

import com.pulsevisual.client.PulseConfigScreen;
import com.pulsevisual.client.ZoomHandler;
import com.pulsevisual.config.PulseConfig;
import com.pulsevisual.util.ColorUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PulseVisualClient implements ClientModInitializer {

    public static final String MOD_ID = "pulsevisual";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // Key bindings
    public static KeyBinding ZOOM_KEY;
    public static KeyBinding CONFIG_KEY;
    public static KeyBinding FULLBRIGHT_KEY;
    public static KeyBinding CHROMA_KEY;

    @Override
    public void onInitializeClient() {
        LOGGER.info("[PulseVisual] Initializing...");

        // Load config
        PulseConfig.load();

        // Register keybindings
        ZOOM_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.pulsevisual.zoom",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_C,
                "PulseVisual"
        ));

        CONFIG_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.pulsevisual.config",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "PulseVisual"
        ));

        FULLBRIGHT_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.pulsevisual.fullbright",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F7,
                "PulseVisual"
        ));

        CHROMA_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.pulsevisual.chroma",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F8,
                "PulseVisual"
        ));

        // Client tick handler
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            float delta = 1.0f; // 1 tick

            // Chroma tick
            ColorUtil.tick(delta / 20f);

            // Zoom tick
            ZoomHandler.isZooming = ZOOM_KEY.isPressed();
            ZoomHandler.tick();

            // Config screen
            while (CONFIG_KEY.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new PulseConfigScreen(null));
                }
            }

            // Fullbright toggle
            while (FULLBRIGHT_KEY.wasPressed()) {
                PulseConfig cfg = PulseConfig.get();
                cfg.fullBright = !cfg.fullBright;
                PulseConfig.save();
                String state = cfg.fullBright ? "§aEnabled" : "§cDisabled";
                if (client.player != null) {
                    client.player.sendMessage(
                            net.minecraft.text.Text.literal("§6[PulseVisual] §fFullbright: " + state), true);
                }
            }

            // Chroma toggle
            while (CHROMA_KEY.wasPressed()) {
                PulseConfig cfg = PulseConfig.get();
                cfg.chromaMode = !cfg.chromaMode;
                PulseConfig.save();
                String state = cfg.chromaMode ? "§aEnabled" : "§cDisabled";
                if (client.player != null) {
                    client.player.sendMessage(
                            net.minecraft.text.Text.literal("§6[PulseVisual] §fChroma: " + state), true);
                }
            }

            // FPS limiter
            if (PulseConfig.get().fpsLimit) {
                applyFpsLimit(client, PulseConfig.get().fpsLimitValue);
            }
        });

        LOGGER.info("[PulseVisual] Loaded successfully! Press RIGHT SHIFT to open settings.");
    }

    private void applyFpsLimit(MinecraftClient client, int limit) {
        if (client.options.getMaxFps().getValue() != limit) {
            client.options.getMaxFps().setValue(limit);
        }
    }
}
