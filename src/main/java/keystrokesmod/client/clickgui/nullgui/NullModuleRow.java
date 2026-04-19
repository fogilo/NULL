package keystrokesmod.client.clickgui.nullgui;

import java.util.ArrayList;
import java.util.List;

import keystrokesmod.client.clickgui.nullgui.settings.NBind;
import keystrokesmod.client.clickgui.nullgui.settings.NCombo;
import keystrokesmod.client.clickgui.nullgui.settings.NDoubleSlider;
import keystrokesmod.client.clickgui.nullgui.settings.NRgb;
import keystrokesmod.client.clickgui.nullgui.settings.NSettingComponent;
import keystrokesmod.client.clickgui.nullgui.settings.NSlider;
import keystrokesmod.client.clickgui.nullgui.settings.NToggle;
import keystrokesmod.client.module.Module;
import keystrokesmod.client.module.setting.Setting;
import keystrokesmod.client.module.setting.impl.ComboSetting;
import keystrokesmod.client.module.setting.impl.DoubleSliderSetting;
import keystrokesmod.client.module.setting.impl.RGBSetting;
import keystrokesmod.client.module.setting.impl.SliderSetting;
import keystrokesmod.client.module.setting.impl.TickSetting;
import keystrokesmod.client.utils.CoolDown;
import keystrokesmod.client.utils.RenderUtils;
import keystrokesmod.client.utils.Utils;
import keystrokesmod.client.utils.font.FontUtil;
import net.minecraft.client.gui.Gui;

/**
 * A single module row/card in the NULL Client GUI.
 *
 * Collapsed: uniform CARD_HEIGHT, shows name + toggle + arrow
 * Expanded (right-click): smoothly reveals settings area below the header
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
        if (s instanceof RGBSetting)          return new NRgb((RGBSetting) s);
        if (s instanceof DoubleSliderSetting) return new NDoubleSlider((DoubleSliderSetting) s);
        return null;
    }

    public void setPosition(int x, int y, int width) {
        this.x = x;
        this.y = y;
        this.width = width;
    }

    // ────────────────────────────────────────────────────────────
    //  DRAWING
    // ────────────────────────────────────────────────────────────

    public void draw(int mx, int my) {
        int cardH = getTotalHeight() - NullTheme.CARD_GAP;

        // ── Card background ──
        boolean hoverHeader = mx >= x && mx <= x + width && my >= y && my <= y + NullTheme.CARD_HEIGHT;
        int bgColor = hoverHeader ? NullTheme.CARD_HOVER : NullTheme.CARD_BG;
        if (expanded) bgColor = NullTheme.CARD_EXPANDED;
        RenderUtils.drawRoundedRect(x, y, x + width, y + cardH, NullTheme.CARD_RADIUS, bgColor);

        // ── Accent bar on left when enabled ──
        if (mod.isEnabled()) {
            RenderUtils.drawRoundedRect(x, y + 4, x + 3, y + NullTheme.CARD_HEIGHT - 4, 2, NullTheme.ACCENT);
        }

        // ── Module name ──
        int nameColor = mod.isEnabled() ? NullTheme.TEXT_ENABLED : NullTheme.TEXT_DISABLED;
        float nameY = y + (NullTheme.CARD_HEIGHT - FontUtil.poppinsBold.getHeight()) / 2f;
        FontUtil.poppinsBold.drawSmoothString(mod.getName(), x + 10, nameY, nameColor);

        // ── Toggle switch ──
        drawToggle();

        // ── Arrow icon ──
        drawArrow(mx, my);

        // ── Expanded settings area ──
        if (expanded || getAnimPercent() < 1f) {
            // Separator line
            float animP = getAnimPercent();
            if (animP > 0.1f) {
                int sepAlpha = (int) (animP * 0x30);
                int sepColor = (sepAlpha << 24) | 0xCC97FF;
                Gui.drawRect(x + 8, y + NullTheme.CARD_HEIGHT, x + width - 8,
                        y + NullTheme.CARD_HEIGHT + 1, sepColor);
            }

            int settingY = y + NullTheme.CARD_HEIGHT + 6;
            for (NSettingComponent comp : settings) {
                if (!comp.visable) continue;
                comp.setPosition(x + 14, settingY, width - 28);
                comp.draw(mx, my);
                settingY += comp.getHeight() + 4;
            }
            if (bindRow != null) {
                bindRow.setPosition(x + 14, settingY, width - 28);
                bindRow.draw(mx, my);
            }
        }
    }

    private void drawToggle() {
        int tw = NullTheme.TOGGLE_W;
        int th = NullTheme.TOGGLE_H;
        int knob = NullTheme.TOGGLE_KNOB_SIZE;
        int tx = x + width - 40 - tw;
        int ty = y + (NullTheme.CARD_HEIGHT - th) / 2;

        float percent = Utils.Client.smoothPercent(
            (mod.isEnabled() ? toggleAnim.getElapsedTime() : toggleAnim.getTimeLeft())
            / (float) toggleAnim.getCooldownTime()
        );

        int trackColor = NullTheme.lerpColor(NullTheme.TOGGLE_OFF_TRACK, NullTheme.TOGGLE_ON_TRACK, percent);
        RenderUtils.drawRoundedRect(tx, ty, tx + tw, ty + th, th / 2f, trackColor);

        // Glow effect when on
        if (percent > 0.5f) {
            RenderUtils.drawRoundedRect(tx - 2, ty - 2, tx + tw + 2, ty + th + 2,
                    (th + 4) / 2f, NullTheme.ACCENT_GLOW_SOFT);
        }

        // Knob
        int knobX = (int) (tx + NullTheme.TOGGLE_INSET + percent * (tw - knob - NullTheme.TOGGLE_INSET * 2));
        int knobY = ty + (th - knob) / 2;
        RenderUtils.drawRoundedRect(knobX, knobY, knobX + knob, knobY + knob, knob / 2f, NullTheme.TOGGLE_KNOB);
    }

    private void drawArrow(int mx, int my) {
        int arrowAreaX = x + width - 30;
        int arrowAreaW = 20;
        boolean hoverArrow = mx >= arrowAreaX && mx <= arrowAreaX + arrowAreaW
                && my >= y && my <= y + NullTheme.CARD_HEIGHT;

        int arrowColor = hoverArrow ? NullTheme.ACCENT : NullTheme.TEXT_SECONDARY;
        String arrow = expanded ? "\u25B2" : "\u25BC"; // ▲ or ▼
        float arrowY = y + (NullTheme.CARD_HEIGHT - FontUtil.poppinsRegular.getHeight()) / 2f;
        FontUtil.poppinsRegular.drawSmoothString(arrow, arrowAreaX + 4, arrowY, arrowColor);
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
        int h = 6; // top padding
        for (NSettingComponent c : settings) {
            if (c.visable) h += c.getHeight() + 4;
        }
        if (bindRow != null) h += bindRow.getHeight() + 4;
        h += 4; // bottom padding
        return h;
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
            // Toggle switch click (left-click)
            int tw = NullTheme.TOGGLE_W;
            int tx = x + width - 40 - tw;
            int ty = y + (NullTheme.CARD_HEIGHT - NullTheme.TOGGLE_H) / 2;
            if (button == 0 && mx >= tx - 2 && mx <= tx + tw + 2
                    && my >= ty - 2 && my <= ty + NullTheme.TOGGLE_H + 2) {
                toggleAnim.setCooldown(NullTheme.ANIM_TOGGLE);
                toggleAnim.start();
                mod.toggle();
                return true;
            }

            // Right-click anywhere on header → expand/collapse
            if (button == 1 && (!settings.isEmpty() || bindRow != null)) {
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
                return true;
            }

            // Left-click on name area → toggle module
            if (button == 0) {
                toggleAnim.setCooldown(NullTheme.ANIM_TOGGLE);
                toggleAnim.start();
                mod.toggle();
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
