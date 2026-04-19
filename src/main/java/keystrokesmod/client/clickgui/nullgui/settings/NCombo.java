package keystrokesmod.client.clickgui.nullgui.settings;

import keystrokesmod.client.clickgui.nullgui.NullTheme;
import keystrokesmod.client.module.Module;
import keystrokesmod.client.module.setting.impl.ComboSetting;
import keystrokesmod.client.utils.font.FontUtil;

/**
 * Mode selector — shows "Name: Mode" with click to cycle.
 * Left-click advances to next mode, right-click goes to previous.
 */
public class NCombo extends NSettingComponent {

    private final ComboSetting<?> setting;
    private final Module mod;

    public NCombo(ComboSetting<?> setting, Module mod) {
        this.setting = setting;
        this.mod = mod;
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        String name = setting.getName() + ": ";
        String mode = setting.getMode().name();

        FontUtil.poppinsRegular.drawSmoothString(name, x, y, NullTheme.TEXT_LABEL);
        float nameW = (float) FontUtil.poppinsRegular.getStringWidth(name);
        FontUtil.poppinsRegular.drawSmoothString(mode, x + nameW, y, NullTheme.TEXT_VALUE);

        // Draw left/right arrows
        float arrowX = x + nameW + (float) FontUtil.poppinsRegular.getStringWidth(mode) + 4;
        FontUtil.poppinsRegular.drawSmoothString("\u25C0 \u25B6", arrowX, y, NullTheme.TEXT_SECONDARY);
    }

    @Override
    public int getHeight() {
        return (int) FontUtil.poppinsRegular.getHeight() + 4;
    }

    @Override
    public boolean mouseDown(int mouseX, int mouseY, int button) {
        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + getHeight()) {
            if (button == 0)
                setting.nextMode();
            else if (button == 1)
                setting.prevMode();
            mod.guiButtonToggled(setting);
            return true;
        }
        return false;
    }
}
