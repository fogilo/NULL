package keystrokesmod.client.clickgui.nullgui.settings;

import keystrokesmod.client.clickgui.nullgui.NullTheme;
import keystrokesmod.client.module.setting.impl.SliderSetting;
import keystrokesmod.client.utils.RenderUtils;
import keystrokesmod.client.utils.font.FontUtil;

/**
 * Redesigned slider with uppercase label, value pill, and thick rounded track.
 * Matches the reference design: "CPS TARGET" style labels with purple fill.
 */
public class NSlider extends NSettingComponent {

    private final SliderSetting setting;
    private boolean dragging;

    public NSlider(SliderSetting setting) {
        this.setting = setting;
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        if (dragging) {
            float percent = (mouseX - x) / (float) (width - 60); // account for value pill width
            percent = Math.max(0f, Math.min(1f, percent));
            float value = (float) (setting.getMin() + percent * (setting.getMax() - setting.getMin()));
            setting.setValue(value);
        }

        int trackWidth = width - 60; // reserve space for value pill

        // ── Label: UPPERCASE name ──
        String label = setting.getName().toUpperCase();
        FontUtil.poppinsBold.drawSmoothString(label, x, y, NullTheme.TEXT_LABEL);

        // ── Value pill on the right ──
        String valueStr = String.valueOf(setting.getInput());
        float valueTextW = (float) FontUtil.poppinsBold.getStringWidth(valueStr);
        int pillW = Math.max(36, (int) valueTextW + 14);
        int pillX = x + width - pillW;
        int pillY = y - 2;
        int pillH = (int) FontUtil.poppinsBold.getHeight() + 4;
        RenderUtils.drawRoundedRect(pillX, pillY, pillX + pillW, pillY + pillH, pillH / 2f, NullTheme.SLIDER_VALUE_PILL_BG);
        RenderUtils.drawRoundedOutline(pillX, pillY, pillX + pillW, pillY + pillH, pillH / 2f, 1, NullTheme.GHOST_BORDER);
        FontUtil.poppinsBold.drawSmoothString(valueStr,
                pillX + (pillW - valueTextW) / 2f,
                pillY + (pillH - FontUtil.poppinsBold.getHeight()) / 2f, NullTheme.SLIDER_VALUE_PILL_TEXT);

        // ── Track ──
        int trackY = y + (int) FontUtil.poppinsBold.getHeight() + 6;
        int trackH = 6;
        RenderUtils.drawRoundedRect(x, trackY, x + trackWidth, trackY + trackH, 3, NullTheme.SLIDER_TRACK);

        // ── Fill ──
        float percent = (float) ((setting.getInput() - setting.getMin()) / (setting.getMax() - setting.getMin()));
        int fillW = (int) (trackWidth * percent);
        if (fillW > 0) {
            RenderUtils.drawRoundedRect(x, trackY, x + fillW, trackY + trackH, 3, NullTheme.SLIDER_FILL);
        }

        // ── Knob ──
        int knobR = 5;
        int knobX = x + fillW;
        int knobCY = trackY + trackH / 2;
        // Glow
        RenderUtils.drawRoundedRect(knobX - knobR - 3, knobCY - knobR - 3, knobX + knobR + 3, knobCY + knobR + 3,
                knobR + 3, NullTheme.ACCENT_GLOW_SOFT);
        // Solid knob
        RenderUtils.drawRoundedRect(knobX - knobR, knobCY - knobR, knobX + knobR, knobCY + knobR, knobR, NullTheme.SLIDER_KNOB);
    }

    @Override
    public int getHeight() {
        return (int) FontUtil.poppinsBold.getHeight() + 6 + 6 + 8;
    }

    @Override
    public boolean mouseDown(int mouseX, int mouseY, int button) {
        int trackY = y + (int) FontUtil.poppinsBold.getHeight() + 6;
        int trackWidth = width - 60;
        if (mouseX >= x && mouseX <= x + trackWidth && mouseY >= trackY - 6 && mouseY <= trackY + 12) {
            dragging = true;
            return true;
        }
        return false;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int button) {
        dragging = false;
    }
}
