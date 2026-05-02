package keystrokesmod.client.clickgui.nullgui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import keystrokesmod.client.clickgui.nullgui.settings.NBind;
import keystrokesmod.client.clickgui.nullgui.settings.NCombo;
import keystrokesmod.client.clickgui.nullgui.settings.NProfileCombo;
import keystrokesmod.client.clickgui.nullgui.settings.NDoubleSlider;
import keystrokesmod.client.clickgui.nullgui.settings.NRgb;
import keystrokesmod.client.clickgui.nullgui.settings.NSettingComponent;
import keystrokesmod.client.clickgui.nullgui.settings.NSlider;
import keystrokesmod.client.clickgui.nullgui.settings.NToggle;
import keystrokesmod.client.module.Module;
import keystrokesmod.client.module.setting.Setting;
import keystrokesmod.client.module.setting.impl.ComboSetting;
import keystrokesmod.client.module.setting.impl.ProfileComboSetting;
import keystrokesmod.client.module.setting.impl.DoubleSliderSetting;
import keystrokesmod.client.module.setting.impl.RGBSetting;
import keystrokesmod.client.module.setting.impl.SliderSetting;
import keystrokesmod.client.module.setting.impl.TickSetting;
import keystrokesmod.client.utils.CoolDown;
import keystrokesmod.client.utils.RenderUtils;
import keystrokesmod.client.utils.Utils;
import keystrokesmod.client.utils.font.FontUtil;
import keystrokesmod.client.utils.font.FontRenderer;
import net.minecraft.client.gui.Gui;

/**
 * A single module row/card in the NULL Client GUI.
 *
 * Features:
 * - Hover glow effect (subtle purple aura, no size change)
 * - Favorite star icon (★ filled pink when active, ☆ outline when inactive)
 * - Toggle switch + chevron for settings expansion
 */
public class NullModuleRow {

    private final Module mod;
    private final List<NSettingComponent> settings = new ArrayList<NSettingComponent>();
    private NBind bindRow;
    private boolean expanded = false;

    private int x, y, width;

    // Animation
    private final CoolDown toggleAnim = new CoolDown(NullTheme.ANIM_TOGGLE);
    private final CoolDown expandAnim = new CoolDown(NullTheme.ANIM_EXPAND);
    private int prevSettingsH = 0;
    private int targetSettingsH = 0;

    // Star icon dimensions
    private static final int STAR_SIZE = 14;

    public NullModuleRow(Module mod) {
        this.mod = mod;
        for (Setting s : mod.getSettings()) {
            NSettingComponent c = buildComponent(s);
            if (c != null) settings.add(c);
        }
        if (mod.isBindable())
            bindRow = new NBind(mod);
    }

    @SuppressWarnings("unchecked")
    private NSettingComponent buildComponent(Setting s) {
        if (s instanceof TickSetting)         return new NToggle((TickSetting) s, mod);
        if (s instanceof SliderSetting)       return new NSlider((SliderSetting) s);
        if (s instanceof ComboSetting)        return new NCombo((ComboSetting<?>) s, mod);
        if (s instanceof ProfileComboSetting) return new NProfileCombo((ProfileComboSetting) s, mod);
        if (s instanceof RGBSetting)          return new NRgb((RGBSetting) s);
        if (s instanceof DoubleSliderSetting) return new NDoubleSlider((DoubleSliderSetting) s);
        return null;
    }

    public void setPosition(int x, int y, int width) {
        this.x = x;
        this.y = y;
        this.width = width;
    }

    public boolean isFavorited() {
        return NullClickGui.favorites.contains(mod.getName());
    }

    public void toggleFavorite() {
        if (isFavorited()) {
            NullClickGui.favorites.remove(mod.getName());
        } else {
            NullClickGui.favorites.add(mod.getName());
        }
    }

    // ────────────────────────────────────────────────────────────
    //  DRAWING
    // ────────────────────────────────────────────────────────────

    public void draw(int mx, int my) {
        int cardH = getTotalHeight() - NullTheme.CARD_GAP;

        boolean hoverHeader = mx >= x && mx <= x + width && my >= y && my <= y + NullTheme.CARD_HEIGHT;

        // ── Hover glow — subtle purple aura behind the card ──
        if (hoverHeader && !expanded) {
            RenderUtils.drawRoundedRect(x - 2, y - 2, x + width + 2, y + cardH + 2,
                    NullTheme.CARD_RADIUS + 2, NullTheme.HOVER_GLOW);
        }

        // ── Card background ──
        int bgColor = hoverHeader ? NullTheme.CARD_HOVER : NullTheme.CARD_BG;
        if (expanded) bgColor = NullTheme.CARD_EXPANDED;
        RenderUtils.drawRoundedRect(x, y, x + width, y + cardH, NullTheme.CARD_RADIUS, bgColor);

        // ── Ghost Border for the card ──
        if (expanded) {
            RenderUtils.drawRoundedOutline(x, y, x + width, y + cardH, NullTheme.CARD_RADIUS, 1,
                    NullTheme.GHOST_BORDER_STRONG);
        }

        // ── HOVER OUTLINE — distinct purple stroke on the entire card bounding box ──
        if (hoverHeader && !expanded) {
            RenderUtils.drawRoundedOutline(x, y, x + width, y + cardH, NullTheme.CARD_RADIUS, 1.5f,
                    NullTheme.HOVER_OUTLINE);
        }

        // ── Active Border Glow (Tracing) ──
        float smx = NullClickGui.smoothMx;
        float smy = NullClickGui.smoothMy;
        float cx = Math.max(x, Math.min(smx, x + width));
        float cy = Math.max(y, Math.min(smy, y + cardH));
        float dist = (float) Math.hypot(smx - cx, smy - cy);
        
        float traceRadius = 150f;
        if (dist < traceRadius) {
            float intensity = 1.0f - (dist / traceRadius);
            intensity = intensity * intensity; // quadratic falloff for softer edges
            int glowAlpha = (int) (intensity * 200); // max alpha 200/255
            int glowColor = (glowAlpha << 24) | (NullTheme.ACCENT & 0x00FFFFFF);
            // Draw an outer glow tracing the edge
            RenderUtils.drawRoundedOutline(x - 0.5f, y - 0.5f, x + width + 0.5f, y + cardH + 0.5f, NullTheme.CARD_RADIUS + 0.5f, 1.5f, glowColor);
        }

        // ── Accent bar on left when enabled — 3px wide with glow ──
        if (mod.isEnabled()) {
            int barTop = y + 8;
            int barBot = y + NullTheme.CARD_HEIGHT - 8;
            RenderUtils.drawRoundedRect(x - 1, barTop - 1, x + 4, barBot + 1, 2, NullTheme.ACCENT_GLOW_SOFT);
            RenderUtils.drawRoundedRect(x, barTop, x + 3, barBot, 2, NullTheme.ACCENT);
        }

        // ── Module name with dynamic text scaling (LARGE font for module names) ──
        FontRenderer nameFont = FontUtil.guiDynamicLarge != null ? FontUtil.guiDynamicLarge : FontUtil.poppinsBold;
        FontRenderer descFont = FontUtil.guiDynamicSmall != null ? FontUtil.guiDynamicSmall : FontUtil.poppinsRegular;
        int nameColor = mod.isEnabled() ? NullTheme.TEXT_ENABLED : NullTheme.TEXT_DISABLED;
        String name = mod.getName();
        if (mod.getName().equalsIgnoreCase("Profiles") && keystrokesmod.client.main.Raven.profileManager != null) {
            name = "Profiles: " + keystrokesmod.client.main.Raven.profileManager.getActiveProfile();
        }
        float rawNameW = (float) nameFont.getStringWidth(name);
        int maxNameW = width - 110; // space for toggle + star + chevron
        float nameScale = 1.0f;
        if (rawNameW > maxNameW && maxNameW > 10) {
            nameScale = maxNameW / rawNameW;
            if (nameScale < 0.55f) nameScale = 0.55f;
        }
        float nameY = y + (NullTheme.CARD_HEIGHT - nameFont.getHeight()) / 2f;
        if (nameScale < 1.0f) {
            GL11.glPushMatrix();
            GL11.glTranslatef(x + 16, nameY, 0);
            GL11.glScalef(nameScale, nameScale, 1.0f);
            nameFont.drawSmoothString(name, 0, 0, nameColor);
            GL11.glPopMatrix();
        } else {
            nameFont.drawSmoothString(name, x + 16, nameY, nameColor);
        }
        float nameW = rawNameW * nameScale;



        // ── Right-side controls (from right to left): Chevron → Star → Toggle ──

        // 1. Chevron (expand/collapse)
        drawChevron(mx, my);

        // 2. Favorite star
        drawStar(mx, my);

        // 3. Toggle switch
        if (!mod.getName().equalsIgnoreCase("Font") && !mod.getName().equalsIgnoreCase("Profiles")) {
            drawToggle();
        }

        // ── Expanded settings area ──
        if (expanded || getAnimPercent() < 1f) {
            float animP = getAnimPercent();
            // Separator line between header and settings
            if (animP > 0.1f) {
                int sepAlpha = (int) (animP * 0x30);
                int sepColor = (sepAlpha << 24) | 0x484849;
                Gui.drawRect(x + 14, y + NullTheme.CARD_HEIGHT, x + width - 14,
                        y + NullTheme.CARD_HEIGHT + 1, sepColor);
            }

            int settingY = y + NullTheme.CARD_HEIGHT + 8;
            
            List<String> descLines = getWrappedDescription();
            if (!descLines.isEmpty()) {
                FontRenderer df = FontUtil.guiDynamicSmall != null ? FontUtil.guiDynamicSmall : FontUtil.poppinsRegular;
                for (String line : descLines) {
                    df.drawSmoothString(line, x + 16, settingY, NullTheme.TEXT_DESCRIPTION);
                    settingY += df.getHeight() + 2;
                }
                settingY += 6;
            }

            for (NSettingComponent comp : settings) {
                if (!comp.visable) continue;
                comp.setPosition(x + 16, settingY, width - 32);
                comp.draw(mx, my);
                settingY += comp.getHeight() + 5;
            }
            if (bindRow != null) {
                bindRow.setPosition(x + 16, settingY, width - 32);
                bindRow.draw(mx, my);
            }
        }
    }

    private void drawToggle() {
        int tw = NullTheme.TOGGLE_W;
        int th = NullTheme.TOGGLE_H;
        int knob = NullTheme.TOGGLE_KNOB_SIZE;
        // Position: left of star
        int tx = x + width - 74;
        int ty = y + (NullTheme.CARD_HEIGHT - th) / 2;

        float percent = Utils.Client.smoothPercent(
            (mod.isEnabled() ? toggleAnim.getElapsedTime() : toggleAnim.getTimeLeft())
            / (float) toggleAnim.getCooldownTime()
        );

        int trackColor = NullTheme.lerpColor(NullTheme.TOGGLE_OFF_TRACK, NullTheme.TOGGLE_ON_TRACK, percent);
        RenderUtils.drawRoundedRect(tx, ty, tx + tw, ty + th, th / 2f, trackColor);

        // Glow effect when on
        if (percent > 0.3f) {
            int glowAlpha = (int) (percent * 0x28);
            int glowColor = (glowAlpha << 24) | (NullTheme.ACCENT & 0x00FFFFFF);
            RenderUtils.drawRoundedRect(tx - 2, ty - 2, tx + tw + 2, ty + th + 2,
                    (th + 4) / 2f, glowColor);
        }

        // Knob
        int knobX = (int) (tx + NullTheme.TOGGLE_INSET + percent * (tw - knob - NullTheme.TOGGLE_INSET * 2));
        int knobY = ty + (th - knob) / 2;
        RenderUtils.drawRoundedRect(knobX, knobY, knobX + knob, knobY + knob, knob / 2f, NullTheme.TOGGLE_KNOB);
    }

    private void drawStar(int mx, int my) {
        boolean fav = isFavorited();

        // Position star between toggle and chevron
        // Toggle X is x + width - 74, Chevron X is x + width - 20
        // Center of the space between them: x + width - 32
        int starX = x + width - 32;
        int starY = y + (NullTheme.CARD_HEIGHT) / 2;

        boolean hoverStar = mx >= starX - 8 && mx <= starX + 8
                && my >= starY - 8 && my <= starY + 8;

        int starColor;
        if (fav) {
            starColor = hoverStar ? NullTheme.STAR_HOVER : NullTheme.STAR_ACTIVE;
        } else {
            starColor = hoverStar ? NullTheme.STAR_HOVER : NullTheme.STAR_INACTIVE;
        }

        org.lwjgl.opengl.GL11.glPushMatrix();
        org.lwjgl.opengl.GL11.glDisable(org.lwjgl.opengl.GL11.GL_TEXTURE_2D);
        org.lwjgl.opengl.GL11.glEnable(org.lwjgl.opengl.GL11.GL_BLEND);
        org.lwjgl.opengl.GL11.glEnable(org.lwjgl.opengl.GL11.GL_LINE_SMOOTH);
        org.lwjgl.opengl.GL11.glDisable(org.lwjgl.opengl.GL11.GL_CULL_FACE); // Disable culling for complex polygons
        
        float f3 = (float)(starColor >> 24 & 255) / 255.0F;
        float f = (float)(starColor >> 16 & 255) / 255.0F;
        float f1 = (float)(starColor >> 8 & 255) / 255.0F;
        float f2 = (float)(starColor & 255) / 255.0F;
        org.lwjgl.opengl.GL11.glColor4f(f, f1, f2, f3);

        // Draw 5-point star
        int r = 6; // slightly bigger
        if (fav) {
            org.lwjgl.opengl.GL11.glBegin(org.lwjgl.opengl.GL11.GL_TRIANGLE_FAN);
            org.lwjgl.opengl.GL11.glVertex2d(starX, starY); // True center
            for (int i = 0; i <= 10; i++) {
                double angle = i * Math.PI / 5.0 - Math.PI / 2.0;
                double radius = (i % 2 == 0) ? r : r / 2.2;
                org.lwjgl.opengl.GL11.glVertex2d(starX + Math.cos(angle) * radius, starY + Math.sin(angle) * radius);
            }
            org.lwjgl.opengl.GL11.glEnd();
        } else {
            org.lwjgl.opengl.GL11.glLineWidth(1.5f);
            org.lwjgl.opengl.GL11.glBegin(org.lwjgl.opengl.GL11.GL_LINE_LOOP);
            for (int i = 0; i < 10; i++) {
                double angle = i * Math.PI / 5.0 - Math.PI / 2.0;
                double radius = (i % 2 == 0) ? r : r / 2.2;
                org.lwjgl.opengl.GL11.glVertex2d(starX + Math.cos(angle) * radius, starY + Math.sin(angle) * radius);
            }
            org.lwjgl.opengl.GL11.glEnd();
        }

        org.lwjgl.opengl.GL11.glEnable(org.lwjgl.opengl.GL11.GL_CULL_FACE);
        org.lwjgl.opengl.GL11.glEnable(org.lwjgl.opengl.GL11.GL_TEXTURE_2D);
        org.lwjgl.opengl.GL11.glPopMatrix();
    }

    private void drawChevron(int mx, int my) {
        int arrowAreaX = x + width - 20;
        int arrowAreaW = 20;
        boolean hoverArrow = mx >= arrowAreaX && mx <= arrowAreaX + arrowAreaW
                && my >= y && my <= y + NullTheme.CARD_HEIGHT;

        int arrowColor = hoverArrow ? NullTheme.ACCENT : NullTheme.TEXT_SECONDARY;
        int cx = arrowAreaX + arrowAreaW / 2;
        int cy = y + NullTheme.CARD_HEIGHT / 2;

        org.lwjgl.opengl.GL11.glPushMatrix();
        org.lwjgl.opengl.GL11.glDisable(org.lwjgl.opengl.GL11.GL_TEXTURE_2D);
        org.lwjgl.opengl.GL11.glEnable(org.lwjgl.opengl.GL11.GL_BLEND);
        org.lwjgl.opengl.GL11.glEnable(org.lwjgl.opengl.GL11.GL_LINE_SMOOTH);
        org.lwjgl.opengl.GL11.glLineWidth(2.0f);
        
        float af3 = (float)(arrowColor >> 24 & 255) / 255.0F;
        float af = (float)(arrowColor >> 16 & 255) / 255.0F;
        float af1 = (float)(arrowColor >> 8 & 255) / 255.0F;
        float af2 = (float)(arrowColor & 255) / 255.0F;
        org.lwjgl.opengl.GL11.glColor4f(af, af1, af2, af3);
        
        org.lwjgl.opengl.GL11.glBegin(org.lwjgl.opengl.GL11.GL_LINE_STRIP);
        if (expanded) {
            org.lwjgl.opengl.GL11.glVertex2d(cx - 3, cy + 1);
            org.lwjgl.opengl.GL11.glVertex2d(cx, cy - 2);
            org.lwjgl.opengl.GL11.glVertex2d(cx + 3, cy + 1);
        } else {
            org.lwjgl.opengl.GL11.glVertex2d(cx - 3, cy - 1);
            org.lwjgl.opengl.GL11.glVertex2d(cx, cy + 2);
            org.lwjgl.opengl.GL11.glVertex2d(cx + 3, cy - 1);
        }
        org.lwjgl.opengl.GL11.glEnd();
        
        org.lwjgl.opengl.GL11.glEnable(org.lwjgl.opengl.GL11.GL_TEXTURE_2D);
        org.lwjgl.opengl.GL11.glPopMatrix();
    }

    // ────────────────────────────────────────────────────────────
    //  HEIGHT CALCULATION
    // ────────────────────────────────────────────────────────────

    public int getTotalHeight() {
        if (!expanded && getAnimPercent() >= 1f)
            return NullTheme.CARD_HEIGHT + NullTheme.CARD_GAP;

        int settingsH = computeSettingsHeight();

        // Update animation targets
        if (expanded) {
            if (targetSettingsH != settingsH) {
                prevSettingsH = (int) (prevSettingsH + (targetSettingsH - prevSettingsH) * getAnimPercent());
                targetSettingsH = settingsH;
                expandAnim.setCooldown(NullTheme.ANIM_EXPAND);
                expandAnim.start();
            }
        } else {
            if (targetSettingsH != 0) {
                prevSettingsH = (int) (prevSettingsH + (targetSettingsH - prevSettingsH) * getAnimPercent());
                targetSettingsH = 0;
                expandAnim.setCooldown(NullTheme.ANIM_EXPAND);
                expandAnim.start();
            }
        }

        float p = getAnimPercent();
        int animatedH = (int) (prevSettingsH + (targetSettingsH - prevSettingsH) * p);
        return NullTheme.CARD_HEIGHT + animatedH + NullTheme.CARD_GAP;
    }

    private int computeSettingsHeight() {
        int h = 8; // top padding
        
        List<String> descLines = getWrappedDescription();
        if (!descLines.isEmpty()) {
            FontRenderer df = FontUtil.guiDynamicSmall != null ? FontUtil.guiDynamicSmall : FontUtil.poppinsRegular;
            h += descLines.size() * (df.getHeight() + 2) + 6;
        }

        for (NSettingComponent c : settings) {
            if (c.visable) h += c.getHeight() + 5;
        }
        if (bindRow != null) h += bindRow.getHeight() + 5;
        h += 6; // bottom padding
        return h;
    }

    private List<String> getWrappedDescription() {
        List<String> lines = new ArrayList<>();
        String desc = mod.getDescription();
        if (desc == null || desc.isEmpty()) return lines;
        
        FontRenderer df = FontUtil.guiDynamicSmall != null ? FontUtil.guiDynamicSmall : FontUtil.poppinsRegular;
        int maxW = width - 32;
        
        String[] words = desc.split(" ");
        StringBuilder currentLine = new StringBuilder();
        
        for (String word : words) {
            if (currentLine.length() == 0) {
                currentLine.append(word);
            } else {
                String testLine = currentLine.toString() + " " + word;
                if (df.getStringWidth(testLine) <= maxW) {
                    currentLine.append(" ").append(word);
                } else {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                }
            }
        }
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }
        return lines;
    }

    private float getAnimPercent() {
        return Utils.Client.smoothPercent(
            expandAnim.getElapsedTime() / (float) expandAnim.getCooldownTime()
        );
    }

    // ────────────────────────────────────────────────────────────
    //  INPUT HANDLING
    // ────────────────────────────────────────────────────────────

    public boolean mouseDown(int mx, int my, int button) {
        if (mx < x || mx > x + width || my < y || my > y + getTotalHeight())
            return false;

        // Header area interactions
        if (my >= y && my <= y + NullTheme.CARD_HEIGHT) {

            // ── Star click (left-click) ──
            int starX = x + width - 32;
            int starY = y + (NullTheme.CARD_HEIGHT) / 2;
            if (button == 0 && mx >= starX - 8 && mx <= starX + 8
                    && my >= starY - 8 && my <= starY + 8) {
                toggleFavorite();
                return true;
            }

            // ── Toggle switch click (left-click) ──
            int tw = NullTheme.TOGGLE_W;
            int tx = x + width - 74;
            int ty = y + (NullTheme.CARD_HEIGHT - NullTheme.TOGGLE_H) / 2;
            if (button == 0 && mx >= tx && mx <= tx + tw
                    && my >= ty && my <= ty + NullTheme.TOGGLE_H) {
                if (!mod.getName().equalsIgnoreCase("Font") && !mod.getName().equalsIgnoreCase("Profiles")) {
                    toggleAnim.setCooldown(NullTheme.ANIM_TOGGLE);
                    toggleAnim.start();
                    mod.toggle();
                    return true;
                }
            }

            // ── Chevron click → expand/collapse ──
            int arrowAreaX = x + width - 20;
            if (button == 0 && mx >= arrowAreaX - 10 && mx <= arrowAreaX + 10
                    && (!settings.isEmpty() || bindRow != null)) {
                toggleExpand();
                return true;
            }

            // ── Right-click anywhere on header → expand/collapse ──
            if (button == 1 && (!settings.isEmpty() || bindRow != null)) {
                toggleExpand();
                return true;
            }

            return true;
        }

        // Settings area clicks
        if (expanded) {
            for (NSettingComponent comp : settings) {
                if (comp.visable && comp.mouseDown(mx, my, button)) return true;
            }
            if (bindRow != null && bindRow.mouseDown(mx, my, button)) return true;
        }

        return true;
    }

    private void toggleExpand() {
        expanded = !expanded;
        expandAnim.setCooldown(NullTheme.ANIM_EXPAND);
        expandAnim.start();
        if (expanded) {
            prevSettingsH = 0;
            targetSettingsH = computeSettingsHeight();
        } else {
            prevSettingsH = computeSettingsHeight();
            targetSettingsH = 0;
        }
    }

    public void mouseReleased(int mx, int my, int button) {
        for (NSettingComponent s : settings)
            s.mouseReleased(mx, my, button);
    }

    public void keyTyped(char c, int key) {
        for (NSettingComponent s : settings)
            s.keyTyped(c, key);
        if (bindRow != null) bindRow.keyTyped(c, key);
    }

    public Module getModule() {
        return mod;
    }
}
