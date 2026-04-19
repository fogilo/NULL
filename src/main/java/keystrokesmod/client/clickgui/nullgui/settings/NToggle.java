package keystrokesmod.client.clickgui.nullgui.settings;

import keystrokesmod.client.clickgui.nullgui.NullTheme;
import keystrokesmod.client.module.Module;
import keystrokesmod.client.module.setting.impl.TickSetting;
import keystrokesmod.client.utils.CoolDown;
import keystrokesmod.client.utils.RenderUtils;
import keystrokesmod.client.utils.Utils;
import keystrokesmod.client.utils.font.FontUtil;

/**
 * Pill-shaped toggle switch for boolean (tick) settings.
 * Matches the module-level toggle style but rendered inline as a setting.
 */
public class NToggle extends NSettingComponent {

    private final TickSetting setting;
    private final Module mod;
    private final CoolDown anim = new CoolDown(NullTheme.ANIM_TOGGLE);

    private static final int SW = 20; // switch width
    private static final int SH = 10; // switch height
    private static final int KNOB = 8;

    public NToggle(TickSetting setting, Module mod) {
        this.setting = setting;
        this.mod = mod;
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        float h = FontUtil.poppinsRegular.getHeight();
        int centerY = y + (int) (h / 2);

        // Toggle track
        float percent = Utils.Client.smoothPercent(
            (setting.isToggled() ? anim.getElapsedTime() : anim.getTimeLeft()) / (float) anim.getCooldownTime()
        );
        int trackColor = NullTheme.lerpColor(NullTheme.TOGGLE_OFF_TRACK, NullTheme.TOGGLE_ON_TRACK, percent);

        int trackX = x;
        int trackY = centerY - SH / 2;
        RenderUtils.drawRoundedRect(trackX, trackY, trackX + SW, trackY + SH, SH / 2f, trackColor);

        // Glow when on
        if (percent > 0.5f) {
            RenderUtils.drawRoundedRect(trackX - 1, trackY - 1, trackX + SW + 1, trackY + SH + 1,
                    (SH + 2) / 2f, NullTheme.ACCENT_GLOW_SOFT);
        }

        // Knob
        int knobX = (int) (trackX + 1 + percent * (SW - KNOB - 2));
        int knobY = trackY + (SH - KNOB) / 2;
        RenderUtils.drawRoundedRect(knobX, knobY, knobX + KNOB, knobY + KNOB, KNOB / 2f, NullTheme.TOGGLE_KNOB);

        // Label
        FontUtil.poppinsRegular.drawSmoothString(setting.getName(), x + SW + 6, y, NullTheme.TEXT_LABEL);
    }

    @Override
    public int getHeight() {
        return Math.max((int) FontUtil.poppinsRegular.getHeight(), 12) + 2;
    }

    @Override
    public boolean mouseDown(int mouseX, int mouseY, int button) {
        if (mouseX >= x && mouseX <= x + SW + 6 + FontUtil.poppinsRegular.getStringWidth(setting.getName())
                && mouseY >= y && mouseY <= y + getHeight()) {
            anim.setCooldown(NullTheme.ANIM_TOGGLE);
            anim.start();
            setting.toggle();
            mod.guiButtonToggled(setting);
            return true;
        }
        return false;
    }
}
