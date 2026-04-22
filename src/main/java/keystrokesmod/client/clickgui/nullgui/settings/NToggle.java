package keystrokesmod.client.clickgui.nullgui.settings;

import keystrokesmod.client.clickgui.nullgui.NullTheme;
import keystrokesmod.client.module.Module;
import keystrokesmod.client.module.setting.impl.TickSetting;
import keystrokesmod.client.utils.CoolDown;
import keystrokesmod.client.utils.RenderUtils;
import keystrokesmod.client.utils.Utils;
import keystrokesmod.client.utils.font.FontUtil;

/**
 * Checkbox-style toggle for boolean (tick) settings.
 * Matches reference design: 14×14 rounded square, filled purple when checked.
 */
public class NToggle extends NSettingComponent {

    private final TickSetting setting;
    private final Module mod;
    private final CoolDown anim = new CoolDown(NullTheme.ANIM_TOGGLE);

    private static final int BOX_SIZE = 14;
    private static final int BOX_RADIUS = 3;

    public NToggle(TickSetting setting, Module mod) {
        this.setting = setting;
        this.mod = mod;
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        float h = FontUtil.poppinsRegular.getHeight();
        int centerY = y + (int) (h / 2);
        int boxY = centerY - BOX_SIZE / 2;

        float percent = Utils.Client.smoothPercent(
            (setting.isToggled() ? anim.getElapsedTime() : anim.getTimeLeft()) / (float) anim.getCooldownTime()
        );

        // ── Checkbox background ──
        int bgColor = NullTheme.lerpColor(NullTheme.CHECKBOX_UNCHECKED_BG, NullTheme.CHECKBOX_CHECKED_BG, percent);
        RenderUtils.drawRoundedRect(x, boxY, x + BOX_SIZE, boxY + BOX_SIZE, BOX_RADIUS, bgColor);

        // ── Border when unchecked ──
        if (percent < 0.5f) {
            RenderUtils.drawRoundedOutline(x, boxY, x + BOX_SIZE, boxY + BOX_SIZE, BOX_RADIUS, 1, NullTheme.CHECKBOX_BORDER);
        }

        // ── Glow when checked ──
        if (percent > 0.3f) {
            int glowAlpha = (int) (percent * 0x1A);
            int glowColor = (glowAlpha << 24) | (NullTheme.ACCENT & 0x00FFFFFF);
            RenderUtils.drawRoundedRect(x - 2, boxY - 2, x + BOX_SIZE + 2, boxY + BOX_SIZE + 2,
                    BOX_RADIUS + 1, glowColor);
        }

        // ── Checkmark when checked (simple cross/tick using GL11) ──
        if (percent > 0.5f) {
            int checkAlpha = (int) (Math.min(1f, (percent - 0.5f) * 2f) * 255);
            int checkColor = (checkAlpha << 24) | (NullTheme.CHECKBOX_CHECK_COLOR & 0x00FFFFFF);
            
            org.lwjgl.opengl.GL11.glPushMatrix();
            org.lwjgl.opengl.GL11.glDisable(org.lwjgl.opengl.GL11.GL_TEXTURE_2D);
            org.lwjgl.opengl.GL11.glEnable(org.lwjgl.opengl.GL11.GL_BLEND);
            org.lwjgl.opengl.GL11.glEnable(org.lwjgl.opengl.GL11.GL_LINE_SMOOTH);
            org.lwjgl.opengl.GL11.glLineWidth(2.5f);
            
            float f3 = (float)(checkColor >> 24 & 255) / 255.0F;
            float f = (float)(checkColor >> 16 & 255) / 255.0F;
            float f1 = (float)(checkColor >> 8 & 255) / 255.0F;
            float f2 = (float)(checkColor & 255) / 255.0F;
            org.lwjgl.opengl.GL11.glColor4f(f, f1, f2, f3);
            
            org.lwjgl.opengl.GL11.glBegin(org.lwjgl.opengl.GL11.GL_LINE_STRIP);
            org.lwjgl.opengl.GL11.glVertex2d(x + 3, boxY + 7);
            org.lwjgl.opengl.GL11.glVertex2d(x + 6, boxY + 10);
            org.lwjgl.opengl.GL11.glVertex2d(x + 11, boxY + 4);
            org.lwjgl.opengl.GL11.glEnd();
            
            org.lwjgl.opengl.GL11.glEnable(org.lwjgl.opengl.GL11.GL_TEXTURE_2D);
            org.lwjgl.opengl.GL11.glPopMatrix();
        }

        // ── Label ──
        FontUtil.poppinsRegular.drawSmoothString(setting.getName(), x + BOX_SIZE + 8, y, NullTheme.TEXT_LABEL);
    }

    @Override
    public int getHeight() {
        return Math.max((int) FontUtil.poppinsRegular.getHeight(), BOX_SIZE) + 4;
    }

    @Override
    public boolean mouseDown(int mouseX, int mouseY, int button) {
        if (mouseX >= x && mouseX <= x + BOX_SIZE + 8 + FontUtil.poppinsRegular.getStringWidth(setting.getName())
                && mouseY >= y && mouseY <= y + getHeight()) {
            anim.setCooldown(NullTheme.ANIM_TOGGLE);
            anim.start();
            setting.toggle();
            mod.guiButtonToggled(setting);
            return true;
        }
        return false;
    }
}
