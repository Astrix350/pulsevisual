package com.pulsevisual.client.hud;

import com.pulsevisual.client.effect.HitEffectHandler;
import com.pulsevisual.config.PulseConfig;
import com.pulsevisual.util.ColorUtil;
import com.pulsevisual.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;

import java.util.Collection;

public class PulseHudRenderer {

    private static final MinecraftClient MC = MinecraftClient.getInstance();

    // Animation smoothing
    private static float smoothHealth = 20f;
    private static float smoothArmor = 0f;
    private static float smoothFood = 20f;
    private static float smoothXp = 0f;
    private static float smoothAir = 300f;

    private static long lastRenderTime = System.currentTimeMillis();

    public static void render(DrawContext ctx, int screenW, int screenH, float tickDelta) {
        PulseConfig cfg = PulseConfig.get();
        if (!cfg.hudEnabled) return;

        ClientPlayerEntity player = MC.player;
        if (player == null) return;

        long now = System.currentTimeMillis();
        float dt = (now - lastRenderTime) / 1000f;
        lastRenderTime = now;

        updateSmoothedValues(player, dt);

        TextRenderer font = MC.textRenderer;

        // ─── Hit effects ───────────────────────────────────────────────
        HitEffectHandler.tick(dt * 20f);
        HitEffectHandler.render(ctx, screenW, screenH);

        // ─── Bottom-left panel: Health + Armor + Food + Air ────────────
        if (cfg.showHealth || cfg.showArmor || cfg.showSaturation) {
            renderStatsBars(ctx, font, player, screenW, screenH, cfg);
        }

        // ─── Bottom-right: Potions ──────────────────────────────────────
        if (cfg.showPotions) {
            renderPotions(ctx, font, player, screenW, screenH);
        }

        // ─── Top-left: Coordinates + Biome + Compass ───────────────────
        renderInfoPanel(ctx, font, player, screenW, screenH, cfg);

        // ─── Top-right: FPS + Ping + Time ──────────────────────────────
        renderPerformancePanel(ctx, font, screenW, cfg);

        // ─── Center: Custom Crosshair ───────────────────────────────────
        if (cfg.customCrosshair) {
            renderCrosshair(ctx, screenW, screenH, cfg);
        }
    }

    // ────────────────────────────────────────────────────────────────────────────
    //  STATS BARS
    // ────────────────────────────────────────────────────────────────────────────
    private static void renderStatsBars(DrawContext ctx, TextRenderer font,
                                         ClientPlayerEntity player, int sw, int sh,
                                         PulseConfig cfg) {
        float scale = cfg.hudScale;
        int panelW = (int)(200 * scale);
        int panelH = (int)(62 * scale);
        int px = (int)(8 * scale);
        int py = sh - panelH - (int)(8 * scale);

        // Panel background
        RenderUtil.drawRoundedRect(ctx, px - 4, py - 4, panelW + 8, panelH + 8, 6f,
                0xCC111111);
        RenderUtil.drawRoundedRectOutline(ctx, px - 4, py - 4, panelW + 8, panelH + 8,
                6f, 1f, 0x40FFFFFF);

        int barH = (int)(9 * scale);
        int barW = panelW;
        int spacing = (int)(14 * scale);
        int barY = py;

        // ─ Health bar ─────────────────────────────────────────────────
        if (cfg.showHealth) {
            float maxHp = player.getMaxHealth();
            float pct = smoothHealth / maxHp;
            int fgColor = cfg.chromaMode ? ColorUtil.getChromaColor(1f, px) : ColorUtil.healthColor(pct);

            RenderUtil.drawProgressBar(ctx, px, barY, barW, barH, pct, 0x55FF0000, fgColor);

            String hpText = "❤ " + (int)smoothHealth + " / " + (int)maxHp;
            RenderUtil.drawText(ctx, font, hpText, px + 4, barY + 1, 0xFFFFFFFF, true);
            barY += spacing;
        }

        // ─ Armor bar ──────────────────────────────────────────────────
        if (cfg.showArmor && smoothArmor > 0) {
            float pct = smoothArmor / 20f;
            int fgColor = cfg.chromaMode ? ColorUtil.getChromaColor(1f, px + 30) : 0xFF5599FF;
            RenderUtil.drawProgressBar(ctx, px, barY, barW, barH, pct, 0x55334488, fgColor);

            String armorText = "🛡 " + (int)smoothArmor + " / 20";
            RenderUtil.drawText(ctx, font, armorText, px + 4, barY + 1, 0xFFCCDDFF, true);
            barY += spacing;
        }

        // ─ Food bar ───────────────────────────────────────────────────
        if (cfg.showSaturation) {
            float pct = smoothFood / 20f;
            int fgColor = cfg.chromaMode ? ColorUtil.getChromaColor(1f, px + 60) : 0xFFF4A440;
            RenderUtil.drawProgressBar(ctx, px, barY, barW, barH, pct, 0x55885533, fgColor);

            String foodText = "🍗 " + (int)smoothFood + " / 20";
            RenderUtil.drawText(ctx, font, foodText, px + 4, barY + 1, 0xFFFFDD99, true);
            barY += spacing;
        }

        // ─ Air bar (when underwater) ───────────────────────────────────
        if (player.getAir() < player.getMaxAir()) {
            float pct = (float)player.getAir() / player.getMaxAir();
            RenderUtil.drawProgressBar(ctx, px, barY, barW, barH, pct, 0x553355AA, 0xFF55AAFF);
            RenderUtil.drawText(ctx, font, "💧 Air", px + 4, barY + 1, 0xFFAADDFF, true);
        }

        // ─ XP bar ─────────────────────────────────────────────────────
        if (cfg.showExperience) {
            int xpBarY = sh - (int)(8 * scale) - (int)(5 * scale);
            int xpColor = cfg.chromaMode ? ColorUtil.getChromaColor(1f, sw / 2f) : 0xFF55FF55;
            RenderUtil.drawProgressBar(ctx, px, xpBarY, barW, (int)(4 * scale),
                    smoothXp, 0x55003300, xpColor);
        }
    }

    // ────────────────────────────────────────────────────────────────────────────
    //  POTION EFFECTS
    // ────────────────────────────────────────────────────────────────────────────
    private static void renderPotions(DrawContext ctx, TextRenderer font,
                                       ClientPlayerEntity player, int sw, int sh) {
        Collection<StatusEffectInstance> effects = player.getStatusEffects();
        if (effects.isEmpty()) return;

        int i = 0;
        int panelX = sw - 100;
        int startY = sh - 14;

        for (StatusEffectInstance effect : effects) {
            int dur = effect.getDuration();
            String durText = dur > 6000 ? "∞" : formatDuration(dur);
            String name = effect.getEffectType().value().getName().getString();
            // shorten long names
            if (name.length() > 12) name = name.substring(0, 12);

            int amplifier = effect.getAmplifier();
            String label = (amplifier > 0 ? (amplifier + 1) + "x " : "") + name;

            boolean positive = isPositiveEffect(effect.getEffectType());
            int bgColor = positive ? 0xCC114411 : 0xCC441111;
            int textColor = positive ? 0xFF88FF88 : 0xFFFF8888;

            int ey = startY - i * 16;
            RenderUtil.drawRoundedRect(ctx, panelX - 4, ey - 2, 98, 13, 3f, bgColor);
            RenderUtil.drawText(ctx, font, label, panelX, ey, textColor, true);
            RenderUtil.drawText(ctx, font, durText, panelX + 78, ey, 0xFFAAAAAA, true);
            i++;
        }
    }

    // ────────────────────────────────────────────────────────────────────────────
    //  INFO PANEL (coords, biome, compass)
    // ────────────────────────────────────────────────────────────────────────────
    private static void renderInfoPanel(DrawContext ctx, TextRenderer font,
                                         ClientPlayerEntity player, int sw, int sh,
                                         PulseConfig cfg) {
        int x = 8;
        int y = 8;
        int lineH = 11;
        int lines = 0;

        java.util.List<String[]> entries = new java.util.ArrayList<>();

        if (cfg.showCoords) {
            BlockPos pos = player.getBlockPos();
            entries.add(new String[]{"XYZ", pos.getX() + " / " + pos.getY() + " / " + pos.getZ()});
        }

        if (cfg.showBiome && player.getWorld() != null) {
            var biomeEntry = player.getWorld().getBiome(player.getBlockPos());
            String biomeName = biomeEntry.getKey()
                    .map(k -> capitalize(k.getValue().getPath().replace("_", " ")))
                    .orElse("Unknown");
            entries.add(new String[]{"Biome", biomeName});
        }

        if (cfg.showCompass) {
            entries.add(new String[]{"Dir", getDirection(player.getYaw())});
        }

        if (cfg.showSpeed) {
            double spd = Math.sqrt(player.getVelocity().x * player.getVelocity().x
                    + player.getVelocity().z * player.getVelocity().z);
            entries.add(new String[]{"Speed", String.format("%.2f b/s", spd * 20)});
        }

        if (entries.isEmpty()) return;

        int panelH = entries.size() * lineH + 6;
        int panelW = 150;
        RenderUtil.drawRoundedRect(ctx, x - 4, y - 4, panelW, panelH, 5f, 0xCC111111);
        RenderUtil.drawRoundedRectOutline(ctx, x - 4, y - 4, panelW, panelH, 5f, 1f, 0x40FFFFFF);

        for (String[] entry : entries) {
            int labelColor = cfg.chromaMode ? ColorUtil.getChromaColor(1f, y) : 0xFF88AAFF;
            RenderUtil.drawText(ctx, font, entry[0] + ":", x, y, labelColor, true);
            RenderUtil.drawText(ctx, font, entry[1], x + 38, y, 0xFFFFFFFF, true);
            y += lineH;
        }
    }

    // ────────────────────────────────────────────────────────────────────────────
    //  PERFORMANCE PANEL (FPS, Ping, Time)
    // ────────────────────────────────────────────────────────────────────────────
    private static void renderPerformancePanel(DrawContext ctx, TextRenderer font,
                                                int sw, PulseConfig cfg) {
        int x = sw - 80;
        int y = 8;
        int lineH = 11;
        java.util.List<String[]> entries = new java.util.ArrayList<>();

        if (cfg.showFps) {
            int fps = MC.getCurrentFps();
            String fpsColor;
            if (fps >= 60) fpsColor = "§a";
            else if (fps >= 30) fpsColor = "§e";
            else fpsColor = "§c";
            entries.add(new String[]{"FPS", fpsColor + fps});
        }

        if (cfg.showPing && MC.player != null && MC.getNetworkHandler() != null) {
            PlayerListEntry entry = MC.getNetworkHandler().getPlayerListEntry(MC.player.getUuid());
            if (entry != null) {
                int ping = entry.getLatency();
                String pingColor = ping < 80 ? "§a" : ping < 150 ? "§e" : "§c";
                entries.add(new String[]{"Ping", pingColor + ping + "ms"});
            }
        }

        if (cfg.showTime && MC.world != null) {
            long time = MC.world.getTimeOfDay() % 24000;
            long hours = (time / 1000 + 6) % 24;
            long minutes = (time % 1000) * 60 / 1000;
            entries.add(new String[]{"Time", String.format("%02d:%02d", hours, minutes)});
        }

        if (entries.isEmpty()) return;

        int panelH = entries.size() * lineH + 6;
        RenderUtil.drawRoundedRect(ctx, x - 4, y - 4, 80, panelH, 5f, 0xCC111111);
        RenderUtil.drawRoundedRectOutline(ctx, x - 4, y - 4, 80, panelH, 5f, 1f, 0x40FFFFFF);

        for (String[] entry : entries) {
            int labelColor = cfg.chromaMode ? ColorUtil.getChromaColor(1f, x) : 0xFFAAAAAA;
            RenderUtil.drawText(ctx, font, entry[0] + ":", x, y, labelColor, true);
            // Note: Minecraft text renderer handles §-codes natively
            RenderUtil.drawText(ctx, font, entry[1], x + 35, y, 0xFFFFFFFF, true);
            y += lineH;
        }
    }

    // ────────────────────────────────────────────────────────────────────────────
    //  CUSTOM CROSSHAIR
    // ────────────────────────────────────────────────────────────────────────────
    private static void renderCrosshair(DrawContext ctx, int sw, int sh, PulseConfig cfg) {
        int cx = sw / 2;
        int cy = sh / 2;

        int color = cfg.chromaMode ? ColorUtil.getChromaColor(1f) : cfg.crosshairColor;

        // Horizontal
        ctx.fill(cx - 6, cy - 1, cx - 2, cy + 1, color);
        ctx.fill(cx + 2, cy - 1, cx + 6, cy + 1, color);
        // Vertical
        ctx.fill(cx - 1, cy - 6, cx + 1, cy - 2, color);
        ctx.fill(cx - 1, cy + 2, cx + 1, cy + 6, color);
        // Center dot
        ctx.fill(cx - 1, cy - 1, cx + 1, cy + 1, color);
    }

    // ────────────────────────────────────────────────────────────────────────────
    //  HELPERS
    // ────────────────────────────────────────────────────────────────────────────
    private static void updateSmoothedValues(ClientPlayerEntity player, float dt) {
        float speed = Math.min(1f, dt * 5f);
        smoothHealth += (player.getHealth() - smoothHealth) * speed;
        smoothArmor += (player.getArmor() - smoothArmor) * speed;
        smoothFood += (player.getHungerManager().getFoodLevel() - smoothFood) * speed;
        smoothXp += (player.experienceProgress - smoothXp) * speed;
        smoothAir += (player.getAir() - smoothAir) * speed;
    }

    private static String getDirection(float yaw) {
        yaw = ((yaw % 360) + 360) % 360;
        if (yaw < 22.5f || yaw >= 337.5f) return "S ↓";
        if (yaw < 67.5f)  return "SW ↙";
        if (yaw < 112.5f) return "W ←";
        if (yaw < 157.5f) return "NW ↖";
        if (yaw < 202.5f) return "N ↑";
        if (yaw < 247.5f) return "NE ↗";
        if (yaw < 292.5f) return "E →";
        return "SE ↘";
    }

    private static String formatDuration(int ticks) {
        int seconds = ticks / 20;
        return seconds / 60 + ":" + String.format("%02d", seconds % 60);
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private static boolean isPositiveEffect(RegistryEntry<StatusEffect> effect) {
        StatusEffect e = effect.value();
        return e == StatusEffects.SPEED.value() || e == StatusEffects.HASTE.value()
                || e == StatusEffects.STRENGTH.value() || e == StatusEffects.REGENERATION.value()
                || e == StatusEffects.FIRE_RESISTANCE.value() || e == StatusEffects.WATER_BREATHING.value()
                || e == StatusEffects.INVISIBILITY.value() || e == StatusEffects.NIGHT_VISION.value()
                || e == StatusEffects.JUMP_BOOST.value() || e == StatusEffects.RESISTANCE.value()
                || e == StatusEffects.ABSORPTION.value() || e == StatusEffects.SATURATION.value()
                || e == StatusEffects.LUCK.value() || e == StatusEffects.DOLPHINS_GRACE.value()
                || e == StatusEffects.HERO_OF_THE_VILLAGE.value() || e == StatusEffects.CONDUIT_POWER.value();
    }}
