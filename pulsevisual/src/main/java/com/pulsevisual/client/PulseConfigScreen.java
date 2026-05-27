package com.pulsevisual.client;

import com.pulsevisual.config.PulseConfig;
import com.pulsevisual.util.ColorUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class PulseConfigScreen extends Screen {

    private final Screen parent;
    private int category = 0;
    private static final String[] CATEGORIES = {"HUD", "Visual", "Camera", "Gameplay"};

    // Options list state
    private final List<OptionWidget> options = new ArrayList<>();
    private int scrollOffset = 0;
    private static final int OPTION_HEIGHT = 22;
    private static final int CONTENT_TOP = 60;
    private static final int CONTENT_BOTTOM_OFFSET = 30;

    public PulseConfigScreen(Screen parent) {
        super(Text.translatable("pulsevisual.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        options.clear();
        scrollOffset = 0;
        buildOptions();
        addCategoryButtons();
        addCloseButton();
    }

    private void addCategoryButtons() {
        int bw = 70;
        int totalW = CATEGORIES.length * (bw + 4);
        int startX = (width - totalW) / 2;
        for (int i = 0; i < CATEGORIES.length; i++) {
            final int cat = i;
            addDrawableChild(ButtonWidget.builder(
                    Text.literal(CATEGORIES[i]),
                    b -> { category = cat; clearAndInit(); })
                    .dimensions(startX + i * (bw + 4), 30, bw, 18)
                    .build());
        }
    }

    private void addCloseButton() {
        addDrawableChild(ButtonWidget.builder(
                Text.literal("✕ Close"),
                b -> this.client.setScreen(parent))
                .dimensions(width / 2 - 50, height - 24, 100, 18)
                .build());
    }

    private void buildOptions() {
        PulseConfig cfg = PulseConfig.get();
        switch (category) {
            case 0 -> { // HUD
                options.add(new BoolOption("Enable HUD", () -> cfg.hudEnabled, v -> cfg.hudEnabled = v));
                options.add(new BoolOption("Health Bar", () -> cfg.showHealth, v -> cfg.showHealth = v));
                options.add(new BoolOption("Armor Overlay", () -> cfg.showArmor, v -> cfg.showArmor = v));
                options.add(new BoolOption("Food Bar", () -> cfg.showSaturation, v -> cfg.showSaturation = v));
                options.add(new BoolOption("XP Bar", () -> cfg.showExperience, v -> cfg.showExperience = v));
                options.add(new BoolOption("Custom Crosshair", () -> cfg.customCrosshair, v -> cfg.customCrosshair = v));
                options.add(new BoolOption("Coordinates", () -> cfg.showCoords, v -> cfg.showCoords = v));
                options.add(new BoolOption("FPS Counter", () -> cfg.showFps, v -> cfg.showFps = v));
                options.add(new BoolOption("Ping Display", () -> cfg.showPing, v -> cfg.showPing = v));
                options.add(new BoolOption("In-Game Time", () -> cfg.showTime, v -> cfg.showTime = v));
                options.add(new BoolOption("Biome Display", () -> cfg.showBiome, v -> cfg.showBiome = v));
                options.add(new BoolOption("Direction Compass", () -> cfg.showCompass, v -> cfg.showCompass = v));
                options.add(new BoolOption("Speed Display", () -> cfg.showSpeed, v -> cfg.showSpeed = v));
                options.add(new BoolOption("Active Potions", () -> cfg.showPotions, v -> cfg.showPotions = v));
                options.add(new SliderOption("HUD Scale", () -> cfg.hudScale, v -> cfg.hudScale = v, 0.5f, 2.0f));
            }
            case 1 -> { // Visual
                options.add(new BoolOption("Motion Blur", () -> cfg.motionBlur, v -> cfg.motionBlur = v));
                options.add(new SliderOption("Blur Strength", () -> cfg.motionBlurStrength, v -> cfg.motionBlurStrength = v, 0.1f, 0.95f));
                options.add(new BoolOption("Damage Overlay", () -> cfg.damageOverlay, v -> cfg.damageOverlay = v));
                options.add(new BoolOption("Fullbright", () -> cfg.fullBright, v -> cfg.fullBright = v));
                options.add(new BoolOption("Night Vision Tweak", () -> cfg.nightVisionTweak, v -> cfg.nightVisionTweak = v));
                options.add(new BoolOption("Chroma Mode", () -> cfg.chromaMode, v -> cfg.chromaMode = v));
                options.add(new SliderOption("Chroma Speed", () -> cfg.chromaSpeed, v -> cfg.chromaSpeed = v, 0.1f, 5.0f));
                options.add(new BoolOption("Low Fire", () -> cfg.lowFire, v -> cfg.lowFire = v));
                options.add(new BoolOption("No Pumpkin Blur", () -> cfg.noPumpkinBlur, v -> cfg.noPumpkinBlur = v));
                options.add(new BoolOption("No Nausea Effect", () -> cfg.noNauseaEffect, v -> cfg.noNauseaEffect = v));
                options.add(new BoolOption("No Void Fog", () -> cfg.noVoidFog, v -> cfg.noVoidFog = v));
                options.add(new BoolOption("Item Physics", () -> cfg.itemPhysics, v -> cfg.itemPhysics = v));
            }
            case 2 -> { // Camera
                options.add(new BoolOption("Zoom Enabled", () -> cfg.zoomEnabled, v -> cfg.zoomEnabled = v));
                options.add(new BoolOption("Smooth Zoom", () -> cfg.smoothZoom, v -> cfg.smoothZoom = v));
                options.add(new SliderOption("Zoom Level", () -> cfg.zoomLevel, v -> cfg.zoomLevel = v, 1.5f, 16f));
                options.add(new BoolOption("Custom FOV", () -> cfg.customFov, v -> cfg.customFov = v));
                options.add(new SliderOption("FOV Value", () -> cfg.fovValue, v -> cfg.fovValue = v, 30f, 120f));
                options.add(new BoolOption("Sprint FOV Effect", () -> cfg.sprintFovEffect, v -> cfg.sprintFovEffect = v));
                options.add(new BoolOption("View Bobbing", () -> cfg.viewBobbing, v -> cfg.viewBobbing = v));
                options.add(new SliderOption("Hand Sway", () -> cfg.handSwayStrength, v -> cfg.handSwayStrength = v, 0f, 2f));
            }
            case 3 -> { // Gameplay
                options.add(new BoolOption("FPS Limiter", () -> cfg.fpsLimit, v -> cfg.fpsLimit = v));
                options.add(new SliderOption("FPS Cap", () -> (float)cfg.fpsLimitValue, v -> cfg.fpsLimitValue = (int)v, 15f, 300f));
            }
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Background
        this.renderBackground(ctx, mouseX, mouseY, delta);

        // Title
        int titleColor = PulseConfig.get().chromaMode ? ColorUtil.getChromaColor(1f) : 0xFFFFFFFF;
        ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("⚙ PulseVisual"), width / 2, 8, titleColor);

        // Options list area background
        ctx.fill(0, CONTENT_TOP - 4, width, height - CONTENT_BOTTOM_OFFSET + 4, 0x88000000);

        // Draw options
        int contentH = height - CONTENT_TOP - CONTENT_BOTTOM_OFFSET;
        int oy = CONTENT_TOP - scrollOffset;
        for (OptionWidget opt : options) {
            if (oy + OPTION_HEIGHT > CONTENT_TOP && oy < height - CONTENT_BOTTOM_OFFSET) {
                opt.render(ctx, mouseX, mouseY, oy, width, textRenderer);
            }
            oy += OPTION_HEIGHT;
        }

        // Scrollbar
        int totalH = options.size() * OPTION_HEIGHT;
        if (totalH > contentH) {
            float barRatio = (float)contentH / totalH;
            int barH = (int)(contentH * barRatio);
            int barY = CONTENT_TOP + (int)((float)scrollOffset / totalH * contentH);
            ctx.fill(width - 5, barY, width - 2, barY + barH, 0xAAFFFFFF);
        }

        // Category highlight
        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double hScroll, double vScroll) {
        int totalH = options.size() * OPTION_HEIGHT;
        int contentH = height - CONTENT_TOP - CONTENT_BOTTOM_OFFSET;
        int maxScroll = Math.max(0, totalH - contentH);
        scrollOffset = (int)Math.max(0, Math.min(maxScroll, scrollOffset - vScroll * 12));
        return true;
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (my >= CONTENT_TOP && my <= height - CONTENT_BOTTOM_OFFSET) {
            int oy = CONTENT_TOP - scrollOffset;
            for (OptionWidget opt : options) {
                if (my >= oy && my < oy + OPTION_HEIGHT) {
                    opt.onClick(mx, my, oy, width);
                    PulseConfig.save();
                    break;
                }
                oy += OPTION_HEIGHT;
            }
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        if (button == 0 && my >= CONTENT_TOP && my <= height - CONTENT_BOTTOM_OFFSET) {
            int oy = CONTENT_TOP - scrollOffset;
            for (OptionWidget opt : options) {
                if (opt instanceof SliderOption s && my >= oy && my < oy + OPTION_HEIGHT) {
                    s.onDrag(mx, oy, width);
                    PulseConfig.save();
                }
                oy += OPTION_HEIGHT;
            }
        }
        return super.mouseDragged(mx, my, button, dx, dy);
    }

    @Override
    public void close() {
        PulseConfig.save();
        this.client.setScreen(parent);
    }

    // ────────────────────────────────────────────────────────────────────────────
    // Option widget types
    // ────────────────────────────────────────────────────────────────────────────
    interface OptionWidget {
        void render(DrawContext ctx, int mx, int my, int y, int w, net.minecraft.client.font.TextRenderer font);
        void onClick(double mx, double my, int y, int w);
    }

    record BoolOption(String label,
                      java.util.function.Supplier<Boolean> getter,
                      java.util.function.Consumer<Boolean> setter) implements OptionWidget {
        @Override
        public void render(DrawContext ctx, int mx, int my, int y, int w,
                           net.minecraft.client.font.TextRenderer font) {
            boolean val = getter.get();
            ctx.fill(4, y, w - 4, y + OPTION_HEIGHT - 2, 0x22FFFFFF);
            ctx.drawTextWithShadow(font, Text.literal(label), 10, y + 6, 0xFFDDDDDD);
            String tog = val ? "§a● ON" : "§c● OFF";
            ctx.drawTextWithShadow(font, Text.literal(tog), w - 50, y + 6, 0xFFFFFFFF);
        }
        @Override
        public void onClick(double mx, double my, int y, int w) {
            setter.accept(!getter.get());
        }
    }

    static class SliderOption implements OptionWidget {
        private final String label;
        private final java.util.function.Supplier<Float> getter;
        private final java.util.function.Consumer<Float> setter;
        private final float min, max;

        SliderOption(String label, java.util.function.Supplier<Float> getter,
                     java.util.function.Consumer<Float> setter, float min, float max) {
            this.label = label; this.getter = getter;
            this.setter = setter; this.min = min; this.max = max;
        }

        @Override
        public void render(DrawContext ctx, int mx, int my, int y, int w,
                           net.minecraft.client.font.TextRenderer font) {
            ctx.fill(4, y, w - 4, y + OPTION_HEIGHT - 2, 0x22FFFFFF);
            ctx.drawTextWithShadow(font, Text.literal(label), 10, y + 6, 0xFFDDDDDD);
            float val = getter.get();
            float pct = (val - min) / (max - min);
            int barX = w / 2 + 10;
            int barW = w / 2 - 60;
            ctx.fill(barX, y + 7, barX + barW, y + 12, 0x55FFFFFF);
            ctx.fill(barX, y + 7, barX + (int)(barW * pct), y + 12, 0xFFAADDFF);
            ctx.drawTextWithShadow(font, Text.literal(String.format("%.2f", val)),
                    barX + barW + 4, y + 6, 0xFFFFFFFF);
        }

        @Override
        public void onClick(double mx, double my, int y, int w) {
            onDrag(mx, y, w);
        }

        public void onDrag(double mx, int y, int w) {
            int barX = w / 2 + 10;
            int barW = w / 2 - 60;
            float pct = (float)((mx - barX) / barW);
            pct = Math.max(0f, Math.min(1f, pct));
            setter.accept(min + pct * (max - min));
        }
    }
}
