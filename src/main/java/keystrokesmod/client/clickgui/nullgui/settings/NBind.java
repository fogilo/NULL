package keystrokesmod.client.clickgui.nullgui.settings;

import org.lwjgl.input.Keyboard;

import keystrokesmod.client.clickgui.nullgui.NullTheme;
import keystrokesmod.client.module.Module;
import keystrokesmod.client.module.modules.client.GuiModule;
import keystrokesmod.client.utils.RenderUtils;
import keystrokesmod.client.utils.font.FontUtil;

/**
 * Keybind setting component.
 * Click to enter binding mode, then press a key to bind.
 * Redesigned with pill-style display matching the new aesthetic.
 */
public class NBind extends NSettingComponent {

    private final Module mod;
    private boolean binding;

    private static final int PILL_H = 18;
    private static final int PILL_RADIUS = 4;

    public NBind(Module mod) {
        this.mod = mod;
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        // ── Label ──
        String label = "BIND";
        FontUtil.poppinsBold.drawSmoothString(label, x, y, NullTheme.TEXT_LABEL);

        // ── Key pill ──
        String keyText;
        if (binding) {
            keyText = "Press a key...";
        } else {
            keyText = "[" + mod.getBindAsString() + "]";
        }

        float labelW = (float) FontUtil.poppinsBold.getStringWidth(label);
        float keyTextW = (float) FontUtil.poppinsRegular.getStringWidth(keyText);
        int pillW = (int) keyTextW + 16;
        int pillX = (int) (x + labelW + 10);
        int pillY = y - 1;

        int pillBg = binding ? NullTheme.ACCENT : NullTheme.SLIDER_VALUE_PILL_BG;
        int pillTextColor = binding ? NullTheme.TEXT_PRIMARY : NullTheme.ACCENT;

        RenderUtils.drawRoundedRect(pillX, pillY, pillX + pillW, pillY + PILL_H, PILL_RADIUS, pillBg);
        if (!binding) {
            RenderUtils.drawRoundedOutline(pillX, pillY, pillX + pillW, pillY + PILL_H,
                    PILL_RADIUS, 1, NullTheme.GHOST_BORDER);
        }
        FontUtil.poppinsRegular.drawSmoothString(keyText,
                pillX + (pillW - keyTextW) / 2f,
                pillY + (PILL_H - FontUtil.poppinsRegular.getHeight()) / 2f, pillTextColor);
    }

    @Override
    public int getHeight() {
        return Math.max((int) FontUtil.poppinsBold.getHeight(), PILL_H) + 4;
    }

    @Override
    public boolean mouseDown(int mouseX, int mouseY, int button) {
        if (button == 0 && mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + getHeight()) {
            binding = true;
            return true;
        }
        return false;
    }

    @Override
    public void keyTyped(char c, int key) {
        if (binding) {
            if (key == Keyboard.KEY_ESCAPE || key == Keyboard.KEY_0) {
                if (mod instanceof GuiModule)
                    mod.setbind(54);
                else
                    mod.setbind(0);
            } else {
                mod.setbind(key);
            }
            binding = false;
        }
    }
}
