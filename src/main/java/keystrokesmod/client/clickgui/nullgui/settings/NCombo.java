package keystrokesmod.client.clickgui.nullgui.settings;

import keystrokesmod.client.clickgui.nullgui.NullTheme;
import keystrokesmod.client.module.Module;
import keystrokesmod.client.module.setting.impl.ComboSetting;
import keystrokesmod.client.utils.RenderUtils;
import keystrokesmod.client.utils.font.FontUtil;

/**
 * Dropdown selector — clicking the pill expands a selectable list below it.
 * Only when a list item is clicked does the value change and the dropdown close.
 */
public class NCombo extends NSettingComponent {

    private final ComboSetting<?> setting;
    private final Module mod;
    private boolean dropdownOpen = false;

    private static final int PILL_H = 20;
    private static final int PILL_RADIUS = 5;
    private static final int DROPDOWN_RADIUS = 6;
    private static final int ITEM_H = 22;
    private static final int DROPDOWN_PAD = 4;

    public NCombo(ComboSetting<?> setting, Module mod) {
        this.setting = setting;
        this.mod = mod;
    }

    // ── Pill geometry (reused by draw + click) ──

    private int getPillX() {
        String label = setting.getName().toUpperCase();
        float labelW = (float) FontUtil.poppinsBold.getStringWidth(label);
        return (int) (x + labelW + 12);
    }

    private int getPillW() {
        String currentName = setting.getMode().toString();
        float textW = (float) FontUtil.poppinsRegular.getStringWidth(currentName);
        return (int) textW + 44; // text + padding + arrow area
    }

    // ── DRAWING ──

    @Override
    public void draw(int mouseX, int mouseY) {
        // ── Label ──
        String label = setting.getName().toUpperCase();
        FontUtil.poppinsBold.drawSmoothString(label, x, y, NullTheme.TEXT_LABEL);

        // ── Current value pill ──
        int pillX = getPillX();
        int pillW = getPillW();
        int pillY = y - 1;

        boolean hoverPill = mouseX >= pillX && mouseX <= pillX + pillW
                && mouseY >= pillY && mouseY <= pillY + PILL_H;

        int pillBg = (hoverPill || dropdownOpen) ? NullTheme.ACCENT : NullTheme.COMBO_ACTIVE_BG;
        RenderUtils.drawRoundedRect(pillX, pillY, pillX + pillW, pillY + PILL_H, PILL_RADIUS, pillBg);

        // Current mode text
        String currentName = setting.getMode().toString();
        FontUtil.poppinsRegular.drawSmoothString(currentName,
                pillX + 10, pillY + (PILL_H - FontUtil.poppinsRegular.getHeight()) / 2f,
                NullTheme.COMBO_ACTIVE_TEXT);

        // Arrow indicator
        String arrow = dropdownOpen ? "\u25B2" : "\u25BC";
        FontUtil.poppinsRegular.drawSmoothString(arrow,
                pillX + pillW - 16, pillY + (PILL_H - FontUtil.poppinsRegular.getHeight()) / 2f,
                NullTheme.COMBO_ACTIVE_TEXT);

        // ── Dropdown list ──
        if (dropdownOpen) {
            int count = setting.getOptionsCount();
            int dropX = pillX;
            int dropY = pillY + PILL_H + 3;
            int dropW = Math.max(pillW, 120);
            int dropH = count * ITEM_H + DROPDOWN_PAD * 2;

            // Shadow
            RenderUtils.drawRoundedRect(dropX + 2, dropY + 2,
                    dropX + dropW + 2, dropY + dropH + 2, DROPDOWN_RADIUS, 0x40000000);
            // Background
            RenderUtils.drawRoundedRect(dropX, dropY, dropX + dropW, dropY + dropH,
                    DROPDOWN_RADIUS, NullTheme.DROPDOWN_BG);
            RenderUtils.drawRoundedOutline(dropX, dropY, dropX + dropW, dropY + dropH,
                    DROPDOWN_RADIUS, 1f, NullTheme.GHOST_BORDER_STRONG);

            int itemY = dropY + DROPDOWN_PAD;
            int selectedIdx = setting.getCurrentIndex();

            for (int i = 0; i < count; i++) {
                boolean isSelected = (i == selectedIdx);
                boolean hoverItem = mouseX >= dropX + 3 && mouseX <= dropX + dropW - 3
                        && mouseY >= itemY && mouseY <= itemY + ITEM_H;

                // Item highlight
                if (isSelected) {
                    RenderUtils.drawRoundedRect(dropX + 3, itemY, dropX + dropW - 3,
                            itemY + ITEM_H, 4, NullTheme.DROPDOWN_SELECTED);
                } else if (hoverItem) {
                    RenderUtils.drawRoundedRect(dropX + 3, itemY, dropX + dropW - 3,
                            itemY + ITEM_H, 4, NullTheme.DROPDOWN_HOVER);
                }

                // Item text
                int textColor = isSelected ? NullTheme.ACCENT
                        : (hoverItem ? NullTheme.TEXT_PRIMARY : NullTheme.TEXT_SECONDARY);
                FontUtil.poppinsRegular.drawSmoothString(setting.getOptionName(i),
                        dropX + 12, itemY + (ITEM_H - FontUtil.poppinsRegular.getHeight()) / 2f,
                        textColor);

                // Checkmark for selected
                if (isSelected) {
                    FontUtil.poppinsRegular.drawSmoothString("\u2713",
                            dropX + dropW - 20,
                            itemY + (ITEM_H - FontUtil.poppinsRegular.getHeight()) / 2f,
                            NullTheme.ACCENT);
                }

                itemY += ITEM_H;
            }
        }
    }

    // ── HEIGHT ──

    @Override
    public int getHeight() {
        int base = Math.max((int) FontUtil.poppinsBold.getHeight(), PILL_H) + 6;
        if (dropdownOpen) {
            base += setting.getOptionsCount() * ITEM_H + DROPDOWN_PAD * 2 + 3;
        }
        return base;
    }

    // ── INPUT ──

    @Override
    public boolean mouseDown(int mouseX, int mouseY, int button) {
        if (button != 0) return false;

        int pillX = getPillX();
        int pillW = getPillW();
        int pillY = y - 1;

        // ── Click on dropdown items ──
        if (dropdownOpen) {
            int count = setting.getOptionsCount();
            int dropX = pillX;
            int dropY = pillY + PILL_H + 3;
            int dropW = Math.max(pillW, 120);
            int itemY = dropY + DROPDOWN_PAD;

            for (int i = 0; i < count; i++) {
                if (mouseX >= dropX + 3 && mouseX <= dropX + dropW - 3
                        && mouseY >= itemY && mouseY <= itemY + ITEM_H) {
                    setting.setModeByIndex(i);
                    mod.guiButtonToggled(setting);
                    dropdownOpen = false;
                    return true;
                }
                itemY += ITEM_H;
            }

            // Click outside the dropdown list → close it
            dropdownOpen = false;
            return true;
        }

        // ── Click on pill → open dropdown ──
        if (mouseX >= pillX && mouseX <= pillX + pillW
                && mouseY >= pillY && mouseY <= pillY + PILL_H) {
            dropdownOpen = true;
            return true;
        }

        return false;
    }

    /** Whether the dropdown overlay is currently showing */
    public boolean isDropdownOpen() {
        return dropdownOpen;
    }

    /** Force-close the dropdown (e.g. when the parent card collapses) */
    public void closeDropdown() {
        dropdownOpen = false;
    }
}
