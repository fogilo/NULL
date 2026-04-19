package keystrokesmod.client.clickgui.nullgui.settings;

import keystrokesmod.client.clickgui.nullgui.NullTheme;
import keystrokesmod.client.module.setting.impl.DoubleSliderSetting;
import keystrokesmod.client.utils.RenderUtils;
import keystrokesmod.client.utils.font.FontUtil;

/**
 * Range slider with two violet knobs for min/max value settings.
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
        if (dragging) {
            float percent = (mouseX - x) / (float) width;
            percent = Math.max(0f, Math.min(1f, percent));
            float value = (float) (setting.getMin() + percent * (setting.getMax() - setting.getMin()));
            if (draggingMax)
                setting.setValueMax(value);
            else
                setting.setValueMin(value);
        }

        // Label: "Name: min - max"
        String label = setting.getName() + ": " + setting.getInputMin() + " - " + setting.getInputMax();
        FontUtil.poppinsRegular.drawSmoothString(label, x, y, NullTheme.TEXT_LABEL);

        // Track
        int trackY = y + (int) FontUtil.poppinsRegular.getHeight() + 2;
        int trackH = 6;
        RenderUtils.drawRoundedRect(x, trackY, x + width, trackY + trackH, 3, NullTheme.SLIDER_TRACK);

        // Fill between min and max
        float pMin = (float) ((setting.getInputMin() - setting.getMin()) / (setting.getMax() - setting.getMin()));
        float pMax = (float) ((setting.getInputMax() - setting.getMin()) / (setting.getMax() - setting.getMin()));
        int fillX1 = x + (int) (width * pMin);
        int fillX2 = x + (int) (width * pMax);
        if (fillX2 > fillX1) {
            RenderUtils.drawRoundedRect(fillX1, trackY, fillX2, trackY + trackH, 3, NullTheme.SLIDER_FILL);
        }

        // Knobs
        int knobR = 4;
        int knobCY = trackY + trackH / 2;
        RenderUtils.drawRoundedRect(fillX1 - knobR, knobCY - knobR, fillX1 + knobR, knobCY + knobR, knobR, NullTheme.SLIDER_KNOB);
        RenderUtils.drawRoundedRect(fillX2 - knobR, knobCY - knobR, fillX2 + knobR, knobCY + knobR, knobR, NullTheme.SLIDER_KNOB);
    }

    @Override
    public int getHeight() {
        return (int) FontUtil.poppinsRegular.getHeight() + 2 + 6 + 4;
    }

    @Override
    public boolean mouseDown(int mouseX, int mouseY, int button) {
        int trackY = y + (int) FontUtil.poppinsRegular.getHeight() + 2;
        if (mouseX >= x && mouseX <= x + width && mouseY >= trackY - 4 && mouseY <= trackY + 10) {
            dragging = true;
            // Determine which knob is closer
            float pMin = (float) ((setting.getInputMin() - setting.getMin()) / (setting.getMax() - setting.getMin()));
            float pMax = (float) ((setting.getInputMax() - setting.getMin()) / (setting.getMax() - setting.getMin()));
            float clickPercent = (mouseX - x) / (float) width;
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
