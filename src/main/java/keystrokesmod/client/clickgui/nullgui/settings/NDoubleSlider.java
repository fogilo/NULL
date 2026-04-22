package keystrokesmod.client.clickgui.nullgui.settings;

import keystrokesmod.client.clickgui.nullgui.NullTheme;
import keystrokesmod.client.module.setting.impl.DoubleSliderSetting;
import keystrokesmod.client.utils.RenderUtils;
import keystrokesmod.client.utils.font.FontUtil;

/**
 * Range slider with two knobs for min/max value settings.
 * Matches redesigned slider style: uppercase label, value pills, thick track.
 */
public class NDoubleSlider extends NSettingComponent {

    private final DoubleSliderSetting setting;
    private boolean dragging;
    private boolean draggingMax; // true = max knob, false = min knob

    public NDoubleSlider(DoubleSliderSetting setting) {
        this.setting = setting;
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        int trackWidth = width - 80; // reserve space for value pills

        if (dragging) {
            float percent = (mouseX - x) / (float) trackWidth;
            percent = Math.max(0f, Math.min(1f, percent));
            float value = (float) (setting.getMin() + percent * (setting.getMax() - setting.getMin()));
            if (draggingMax)
                setting.setValueMax(value);
            else
                setting.setValueMin(value);
        }

        // ── Label: UPPERCASE name ──
        String label = setting.getName().toUpperCase();
        FontUtil.poppinsBold.drawSmoothString(label, x, y, NullTheme.TEXT_LABEL);

        // ── Value pill on right: "min - max" ──
        String valueStr = setting.getInputMin() + " - " + setting.getInputMax();
        float valueTextW = (float) FontUtil.poppinsRegular.getStringWidth(valueStr);
        int pillW = Math.max(50, (int) valueTextW + 14);
        int pillX = x + width - pillW;
        int pillY = y - 2;
        int pillH = (int) FontUtil.poppinsBold.getHeight() + 4;
        RenderUtils.drawRoundedRect(pillX, pillY, pillX + pillW, pillY + pillH, pillH / 2f, NullTheme.SLIDER_VALUE_PILL_BG);
        RenderUtils.drawRoundedOutline(pillX, pillY, pillX + pillW, pillY + pillH, pillH / 2f, 1, NullTheme.GHOST_BORDER);
        FontUtil.poppinsRegular.drawSmoothString(valueStr,
                pillX + (pillW - valueTextW) / 2f,
                pillY + (pillH - FontUtil.poppinsRegular.getHeight()) / 2f, NullTheme.SLIDER_VALUE_PILL_TEXT);

        // ── Track ──
        int trackY = y + (int) FontUtil.poppinsBold.getHeight() + 6;
        int trackH = 6;
        RenderUtils.drawRoundedRect(x, trackY, x + trackWidth, trackY + trackH, 3, NullTheme.SLIDER_TRACK);

        // ── Fill between min and max ──
        float pMin = (float) ((setting.getInputMin() - setting.getMin()) / (setting.getMax() - setting.getMin()));
        float pMax = (float) ((setting.getInputMax() - setting.getMin()) / (setting.getMax() - setting.getMin()));
        int fillX1 = x + (int) (trackWidth * pMin);
        int fillX2 = x + (int) (trackWidth * pMax);
        if (fillX2 > fillX1) {
            RenderUtils.drawRoundedRect(fillX1, trackY, fillX2, trackY + trackH, 3, NullTheme.SLIDER_FILL);
        }

        // ── Knobs ──
        int knobR = 5;
        int knobCY = trackY + trackH / 2;

        // Min knob
        RenderUtils.drawRoundedRect(fillX1 - knobR - 2, knobCY - knobR - 2, fillX1 + knobR + 2, knobCY + knobR + 2,
                knobR + 2, NullTheme.ACCENT_GLOW_SOFT);
        RenderUtils.drawRoundedRect(fillX1 - knobR, knobCY - knobR, fillX1 + knobR, knobCY + knobR, knobR, NullTheme.SLIDER_KNOB);

        // Max knob
        RenderUtils.drawRoundedRect(fillX2 - knobR - 2, knobCY - knobR - 2, fillX2 + knobR + 2, knobCY + knobR + 2,
                knobR + 2, NullTheme.ACCENT_GLOW_SOFT);
        RenderUtils.drawRoundedRect(fillX2 - knobR, knobCY - knobR, fillX2 + knobR, knobCY + knobR, knobR, NullTheme.SLIDER_KNOB);
    }

    @Override
    public int getHeight() {
        return (int) FontUtil.poppinsBold.getHeight() + 6 + 6 + 8;
    }

    @Override
    public boolean mouseDown(int mouseX, int mouseY, int button) {
        int trackY = y + (int) FontUtil.poppinsBold.getHeight() + 6;
        int trackWidth = width - 80;
        if (mouseX >= x && mouseX <= x + trackWidth && mouseY >= trackY - 6 && mouseY <= trackY + 12) {
            dragging = true;
            // Determine which knob is closer
            float pMin = (float) ((setting.getInputMin() - setting.getMin()) / (setting.getMax() - setting.getMin()));
            float pMax = (float) ((setting.getInputMax() - setting.getMin()) / (setting.getMax() - setting.getMin()));
            float clickPercent = (mouseX - x) / (float) trackWidth;
            draggingMax = Math.abs(clickPercent - pMax) < Math.abs(clickPercent - pMin);
            return true;
        }
        return false;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int button) {
        dragging = false;
    }
}
