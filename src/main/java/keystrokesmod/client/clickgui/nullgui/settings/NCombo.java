package keystrokesmod.client.clickgui.nullgui.settings;

import keystrokesmod.client.clickgui.nullgui.NullTheme;
import keystrokesmod.client.module.Module;
import keystrokesmod.client.module.setting.impl.ComboSetting;
import keystrokesmod.client.utils.RenderUtils;
import keystrokesmod.client.utils.font.FontUtil;

/**
 * Mode selector — shows label with current mode in a pill, click to cycle.
 * Left-click advances to next mode, right-click goes to previous.
 * Styled as a highlighted pill button matching the reference design.
 */
public class NCombo extends NSettingComponent {

    private final ComboSetting<?> setting;
    private final Module mod;

    private static final int PILL_H = 18;
    private static final int PILL_RADIUS = 4;
    private static final int PILL_PAD_X = 12;

    public NCombo(ComboSetting<?> setting, Module mod) {
        this.setting = setting;
        this.mod = mod;
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        // ── Label: UPPERCASE setting name ──
        String label = setting.getName().toUpperCase();
        FontUtil.poppinsBold.drawSmoothString(label, x, y, NullTheme.TEXT_LABEL);

        // ── Current mode pill ──
        float labelW = (float) FontUtil.poppinsBold.getStringWidth(label);
        String modeName = setting.getMode().name();
        float textW = (float) FontUtil.poppinsRegular.getStringWidth(modeName);
        int pillW = (int) textW + PILL_PAD_X * 2;
        int pillX = (int) (x + labelW + 12);
        int pillY = y - 1;

        // Active pill with primary color
        RenderUtils.drawRoundedRect(pillX, pillY, pillX + pillW, pillY + PILL_H, PILL_RADIUS, NullTheme.COMBO_ACTIVE_BG);
        FontUtil.poppinsRegular.drawSmoothString(modeName,
                pillX + (pillW - textW) / 2f,
                pillY + (PILL_H - FontUtil.poppinsRegular.getHeight()) / 2f, NullTheme.COMBO_ACTIVE_TEXT);

        // ── Navigation arrows ──
        int arrowX = pillX + pillW + 8;
        boolean hoverLeft = mouseX >= arrowX && mouseX <= arrowX + 12
                && mouseY >= pillY && mouseY <= pillY + PILL_H;
        FontUtil.poppinsRegular.drawSmoothString("\u25C0", arrowX, pillY + 3,
                hoverLeft ? NullTheme.ACCENT : NullTheme.TEXT_SECONDARY);

        int arrowRightX = arrowX + 16;
        boolean hoverRight = mouseX >= arrowRightX && mouseX <= arrowRightX + 12
                && mouseY >= pillY && mouseY <= pillY + PILL_H;
        FontUtil.poppinsRegular.drawSmoothString("\u25B6", arrowRightX, pillY + 3,
                hoverRight ? NullTheme.ACCENT : NullTheme.TEXT_SECONDARY);
    }

    @Override
    public int getHeight() {
        return Math.max((int) FontUtil.poppinsBold.getHeight(), PILL_H) + 6;
    }

    @Override
    public boolean mouseDown(int mouseX, int mouseY, int button) {
        if (mouseX >= x && mouseX <= x + width && mouseY >= y - 1 && mouseY <= y + getHeight()) {
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
