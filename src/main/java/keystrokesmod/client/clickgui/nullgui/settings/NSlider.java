package keystrokesmod.client.clickgui.nullgui.settings;

import keystrokesmod.client.clickgui.nullgui.NullTheme;
import keystrokesmod.client.module.setting.impl.SliderSetting;
import keystrokesmod.client.utils.RenderUtils;
import keystrokesmod.client.utils.font.FontUtil;

/**
 * Neon-violet slider for numeric settings.
 * Shows "Name: value" label above a rounded track with violet fill.
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
            float percent = (mouseX - x) / (float) width;
            percent = Math.max(0f, Math.min(1f, percent));
            float value = (float) (setting.getMin() + percent * (setting.getMax() - setting.getMin()));
            setting.setValue(value);
        }

        // Label: "Name: value"
        String label = setting.getName() + ": " + setting.getInput();
        FontUtil.poppinsRegular.drawSmoothString(label, x, y, NullTheme.TEXT_LABEL);

        // Track
        int trackY = y + (int) FontUtil.poppinsRegular.getHeight() + 2;
        int trackH = 6;
        RenderUtils.drawRoundedRect(x, trackY, x + width, trackY + trackH, 3, NullTheme.SLIDER_TRACK);

        // Fill
        float percent = (float) ((setting.getInput() - setting.getMin()) / (setting.getMax() - setting.getMin()));
        int fillW = (int) (width * percent);
        if (fillW > 0) {
            RenderUtils.drawRoundedRect(x, trackY, x + fillW, trackY + trackH, 3, NullTheme.SLIDER_FILL);
        }

        // Knob
        int knobR = 4;
        int knobX = x + fillW;
        int knobY = trackY + trackH / 2;
        RenderUtils.drawRoundedRect(knobX - knobR, knobY - knobR, knobX + knobR, knobY + knobR, knobR, NullTheme.SLIDER_KNOB);
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
            return true;
        }
        return false;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int button) {
        dragging = false;
    }
}
