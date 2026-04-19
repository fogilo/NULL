package keystrokesmod.client.clickgui.nullgui.settings;

import org.lwjgl.input.Keyboard;

import keystrokesmod.client.clickgui.nullgui.NullTheme;
import keystrokesmod.client.module.Module;
import keystrokesmod.client.module.modules.client.GuiModule;
import keystrokesmod.client.utils.font.FontUtil;

/**
 * Keybind setting component.
 * Click to enter binding mode, then press a key to bind.
 */
public class NBind extends NSettingComponent {

    private final Module mod;
    private boolean binding;

    public NBind(Module mod) {
        this.mod = mod;
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        String text;
        if (binding) {
            text = "Bind: [Press a key...]";
        } else {
            text = "Bind: [" + mod.getBindAsString() + "]";
        }

        int color = binding ? NullTheme.ACCENT : NullTheme.TEXT_LABEL;
        FontUtil.poppinsRegular.drawSmoothString(text, x, y, color);
    }

    @Override
    public int getHeight() {
        return (int) FontUtil.poppinsRegular.getHeight() + 4;
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
