package keystrokesmod.client.clickgui.nullgui.settings;

import keystrokesmod.client.clickgui.nullgui.NullTheme;
import keystrokesmod.client.module.setting.impl.RGBSetting;
import keystrokesmod.client.utils.RenderUtils;
import keystrokesmod.client.utils.font.FontUtil;

/**
 * RGB color picker with three draggable channel knobs.
 * Redesigned to match the updated slider aesthetic with uppercase labels and color preview.
 */
public class NRgb extends NSettingComponent {

    private final RGBSetting setting;
    private boolean dragging;
    private int activeChannel = -1; // 0=R, 1=G, 2=B

    public NRgb(RGBSetting setting) {
        this.setting = setting;
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        if (dragging && activeChannel >= 0) {
            float percent = (mouseX - x) / (float) (width - 20);
            percent = Math.max(0f, Math.min(1f, percent));
            setting.setColor(activeChannel, (int) (percent * 255f));
        }

        // ── Label: UPPERCASE ──
        String label = setting.getName().toUpperCase();
        FontUtil.poppinsBold.drawSmoothString(label, x, y, NullTheme.TEXT_LABEL);

        // ── Color preview swatch ──
        int previewColor = 0xFF000000 | (setting.getRed() << 16) | (setting.getGreen() << 8) | setting.getBlue();
        int swatchSize = (int) FontUtil.poppinsBold.getHeight();
        int swatchX = x + (int) FontUtil.poppinsBold.getStringWidth(label) + 8;
        RenderUtils.drawRoundedRect(swatchX, y, swatchX + swatchSize, y + swatchSize, 3, previewColor);
        RenderUtils.drawRoundedOutline(swatchX, y, swatchX + swatchSize, y + swatchSize, 3, 1, NullTheme.GHOST_BORDER);

        // ── RGB values display ──
        String rgbStr = setting.getRed() + ", " + setting.getGreen() + ", " + setting.getBlue();
        float rgbTextW = (float) FontUtil.poppinsRegular.getStringWidth(rgbStr);
        int pillW = (int) rgbTextW + 14;
        int pillX = x + width - pillW;
        int pillY = y - 2;
        int pillH = (int) FontUtil.poppinsBold.getHeight() + 4;
        RenderUtils.drawRoundedRect(pillX, pillY, pillX + pillW, pillY + pillH, pillH / 2f, NullTheme.SLIDER_VALUE_PILL_BG);
        RenderUtils.drawRoundedOutline(pillX, pillY, pillX + pillW, pillY + pillH, pillH / 2f, 1, NullTheme.GHOST_BORDER);
        FontUtil.poppinsRegular.drawSmoothString(rgbStr,
                pillX + (pillW - rgbTextW) / 2f,
                pillY + (pillH - FontUtil.poppinsRegular.getHeight()) / 2f, NullTheme.SLIDER_VALUE_PILL_TEXT);

        // ── Track ──
        int trackY = y + (int) FontUtil.poppinsBold.getHeight() + 6;
        int trackH = 6;
        int trackW = width - 20;
        RenderUtils.drawRoundedRect(x, trackY, x + trackW, trackY + trackH, 3, NullTheme.SLIDER_TRACK);

        // ── Channel knobs ──
        int[] colors = {0xFFFF4444, 0xFF44FF44, 0xFF4488FF};
        int knobR = 5;
        int knobCY = trackY + trackH / 2;
        for (int i = 0; i < 3; i++) {
            float channelPercent = setting.getColor(i) / 255f;
            int knobX = x + (int) (trackW * channelPercent);
            // Glow
            RenderUtils.drawRoundedRect(knobX - knobR - 2, knobCY - knobR - 2, knobX + knobR + 2, knobCY + knobR + 2,
                    knobR + 2, (0x33000000 | (colors[i] & 0x00FFFFFF)));
            // Solid knob
            RenderUtils.drawRoundedRect(knobX - knobR, knobCY - knobR, knobX + knobR, knobCY + knobR, knobR, colors[i]);
        }
    }

    @Override
    public int getHeight() {
        return (int) FontUtil.poppinsBold.getHeight() + 6 + 6 + 8;
    }

    @Override
    public boolean mouseDown(int mouseX, int mouseY, int button) {
        int trackY = y + (int) FontUtil.poppinsBold.getHeight() + 6;
        int trackW = width - 20;
        if (mouseX >= x && mouseX <= x + trackW && mouseY >= trackY - 6 && mouseY <= trackY + 12) {
            dragging = true;
            // Find closest channel knob
            float clickPercent = (mouseX - x) / (float) trackW;
            float closest = Float.MAX_VALUE;
            activeChannel = 0;
            for (int i = 0; i < 3; i++) {
                float channelPercent = setting.getColor(i) / 255f;
                float dist = Math.abs(clickPercent - channelPercent);
                if (dist < closest) {
                    closest = dist;
                    activeChannel = i;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int button) {
        dragging = false;
        activeChannel = -1;
    }
}
