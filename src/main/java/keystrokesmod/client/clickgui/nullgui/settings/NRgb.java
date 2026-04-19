package keystrokesmod.client.clickgui.nullgui.settings;

import keystrokesmod.client.clickgui.nullgui.NullTheme;
import keystrokesmod.client.module.setting.impl.RGBSetting;
import keystrokesmod.client.utils.RenderUtils;
import keystrokesmod.client.utils.font.FontUtil;
import net.minecraft.util.EnumChatFormatting;

/**
 * RGB color picker with three draggable channel knobs on a single track.
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
            float percent = (mouseX - x) / (float) width;
            percent = Math.max(0f, Math.min(1f, percent));
            setting.setColor(activeChannel, (int) (percent * 255f));
        }

        // Label
        String label = setting.getName() + ": "
                + EnumChatFormatting.RED + setting.getRed()
                + ", " + EnumChatFormatting.GREEN + setting.getGreen()
                + ", " + EnumChatFormatting.BLUE + setting.getBlue();
        FontUtil.poppinsRegular.drawSmoothString(label, x, y, NullTheme.TEXT_LABEL);

        // Track
        int trackY = y + (int) FontUtil.poppinsRegular.getHeight() + 2;
        int trackH = 6;
        RenderUtils.drawRoundedRect(x, trackY, x + width, trackY + trackH, 3, NullTheme.SLIDER_TRACK);

        // Preview color swatch
        int previewColor = 0xFF000000 | (setting.getRed() << 16) | (setting.getGreen() << 8) | setting.getBlue();
        RenderUtils.drawRoundedRect(x + width - 12, y, x + width, y + (int) FontUtil.poppinsRegular.getHeight(), 3, previewColor);

        // Channel knobs
        int[] colors = {0xFFFF4444, 0xFF44FF44, 0xFF4488FF};
        int knobR = 4;
        int knobCY = trackY + trackH / 2;
        for (int i = 0; i < 3; i++) {
            float channelPercent = setting.getColor(i) / 255f;
            int knobX = x + (int) (width * channelPercent);
            RenderUtils.drawRoundedRect(knobX - knobR, knobCY - knobR, knobX + knobR, knobCY + knobR, knobR, colors[i]);
        }
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
            // Find closest channel knob
            float clickPercent = (mouseX - x) / (float) width;
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
