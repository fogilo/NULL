package keystrokesmod.client.clickgui.nullgui.settings;

import keystrokesmod.client.clickgui.nullgui.NullTheme;
import keystrokesmod.client.module.Module;
import keystrokesmod.client.module.setting.impl.ProfileComboSetting;
import keystrokesmod.client.utils.RenderUtils;
import keystrokesmod.client.utils.font.FontUtil;
import keystrokesmod.client.main.Raven;

public class NProfileCombo extends NSettingComponent {

    private final ProfileComboSetting setting;
    private final Module mod;
    private boolean dropdownOpen = false;
    
    public static boolean isRenaming = false;
    private int renamingIndex = -1;
    private String newProfileName = "";

    private static final int PILL_H = 20;
    private static final int PILL_RADIUS = 5;
    private static final int DROPDOWN_RADIUS = 6;
    private static final int ITEM_H = 22;
    private static final int DROPDOWN_PAD = 4;

    public NProfileCombo(ProfileComboSetting setting, Module mod) {
        this.setting = setting;
        this.mod = mod;
    }

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

    @Override
    public void draw(int mouseX, int mouseY) {
        // Label
        String label = setting.getName().toUpperCase();
        FontUtil.poppinsBold.drawSmoothString(label, x, y, NullTheme.TEXT_LABEL);

        // Current value pill
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

        // Dropdown list
        if (dropdownOpen) {
            int count = setting.getOptionsCount() + 1; // +1 for the create new option
            int dropX = pillX;
            int dropY = pillY + PILL_H + 3;
            int dropW = Math.max(pillW, 160); // make it wider to fit edit icon
            int dropH = count * ITEM_H + DROPDOWN_PAD * 2;

            RenderUtils.drawRoundedRect(dropX + 2, dropY + 2,
                    dropX + dropW + 2, dropY + dropH + 2, DROPDOWN_RADIUS, 0x40000000);
            RenderUtils.drawRoundedRect(dropX, dropY, dropX + dropW, dropY + dropH,
                    DROPDOWN_RADIUS, NullTheme.DROPDOWN_BG);
            RenderUtils.drawRoundedOutline(dropX, dropY, dropX + dropW, dropY + dropH,
                    DROPDOWN_RADIUS, 1f, NullTheme.GHOST_BORDER_STRONG);

            int itemY = dropY + DROPDOWN_PAD;
            int selectedIdx = setting.getCurrentIndex();

            for (int i = 0; i < count; i++) {
                boolean isCreateNew = (i == setting.getOptionsCount());
                boolean isSelected = (!isCreateNew && i == selectedIdx);
                boolean hoverItem = mouseX >= dropX + 3 && mouseX <= dropX + dropW - 3
                        && mouseY >= itemY && mouseY <= itemY + ITEM_H;

                // Item highlight
                if (isSelected && !isRenaming) {
                    RenderUtils.drawRoundedRect(dropX + 3, itemY, dropX + dropW - 3,
                            itemY + ITEM_H, 4, NullTheme.DROPDOWN_SELECTED);
                } else if (hoverItem && (!isRenaming || renamingIndex == i)) {
                    RenderUtils.drawRoundedRect(dropX + 3, itemY, dropX + dropW - 3,
                            itemY + ITEM_H, 4, NullTheme.DROPDOWN_HOVER);
                }

                if (isCreateNew) {
                    int textColor = hoverItem ? NullTheme.ACCENT : NullTheme.TEXT_SECONDARY;
                    FontUtil.poppinsRegular.drawSmoothString("[+ Create New Profile]",
                            dropX + 12, itemY + (ITEM_H - FontUtil.poppinsRegular.getHeight()) / 2f,
                            textColor);
                } else {
                    if (isRenaming && renamingIndex == i) {
                        // Draw text field
                        String display = newProfileName + (System.currentTimeMillis() % 1000 < 500 ? "_" : "");
                        FontUtil.poppinsRegular.drawSmoothString(display,
                                dropX + 12, itemY + (ITEM_H - FontUtil.poppinsRegular.getHeight()) / 2f,
                                NullTheme.ACCENT);
                    } else {
                        int textColor = isSelected ? NullTheme.ACCENT
                                : (hoverItem ? NullTheme.TEXT_PRIMARY : NullTheme.TEXT_SECONDARY);
                        FontUtil.poppinsRegular.drawSmoothString(setting.getOptionName(i),
                                dropX + 12, itemY + (ITEM_H - FontUtil.poppinsRegular.getHeight()) / 2f,
                                textColor);
                        
                        boolean isDefault = setting.getOptionName(i).equalsIgnoreCase("Default");
                        
                        if (!isDefault) {
                            // Edit icon
                            boolean hoverEdit = mouseX >= dropX + dropW - 55 && mouseX <= dropX + dropW - 40
                                    && mouseY >= itemY && mouseY <= itemY + ITEM_H;
                            int editColor = hoverEdit ? NullTheme.ACCENT : NullTheme.TEXT_SECONDARY;
                            net.minecraft.client.Minecraft.getMinecraft().fontRendererObj.drawString("\u270E",
                                    dropX + dropW - 50, itemY + (ITEM_H - 8) / 2,
                                    editColor);

                            // Delete icon
                            boolean hoverDel = mouseX >= dropX + dropW - 35 && mouseX <= dropX + dropW - 20
                                    && mouseY >= itemY && mouseY <= itemY + ITEM_H;
                            int delColor = hoverDel ? 0xFFFF5555 : NullTheme.TEXT_SECONDARY;
                            net.minecraft.client.Minecraft.getMinecraft().fontRendererObj.drawString("\u2716",
                                    dropX + dropW - 30, itemY + (ITEM_H - 8) / 2,
                                    delColor);
                        }

                        // Checkmark for selected
                        if (isSelected) {
                            FontUtil.poppinsRegular.drawSmoothString("\u2713",
                                    dropX + dropW - 20,
                                    itemY + (ITEM_H - FontUtil.poppinsRegular.getHeight()) / 2f,
                                    NullTheme.ACCENT);
                        }
                    }
                }

                itemY += ITEM_H;
            }
        }
    }

    @Override
    public int getHeight() {
        int base = Math.max((int) FontUtil.poppinsBold.getHeight(), PILL_H) + 6;
        if (dropdownOpen) {
            base += (setting.getOptionsCount() + 1) * ITEM_H + DROPDOWN_PAD * 2 + 3;
        }
        return base;
    }

    @Override
    public boolean mouseDown(int mouseX, int mouseY, int button) {
        if (button != 0) return false;

        if (isRenaming) return true; // block other clicks while renaming

        int pillX = getPillX();
        int pillW = getPillW();
        int pillY = y - 1;

        if (dropdownOpen) {
            int count = setting.getOptionsCount() + 1;
            int dropX = pillX;
            int dropY = pillY + PILL_H + 3;
            int dropW = Math.max(pillW, 160);
            int itemY = dropY + DROPDOWN_PAD;

            for (int i = 0; i < count; i++) {
                if (mouseX >= dropX + 3 && mouseX <= dropX + dropW - 3
                        && mouseY >= itemY && mouseY <= itemY + ITEM_H) {
                    
                    if (i == setting.getOptionsCount()) {
                        // Create New Profile
                        Raven.profileManager.createAutoProfile();
                        setting.updateOptions(Raven.profileManager.getProfiles(), Raven.profileManager.getActiveProfile());
                        dropdownOpen = false;
                        return true;
                    } else {
                        boolean isDefault = setting.getOptionName(i).equalsIgnoreCase("Default");
                        
                        if (!isDefault && mouseX >= dropX + dropW - 55 && mouseX <= dropX + dropW - 40) {
                            isRenaming = true;
                            renamingIndex = i;
                            newProfileName = setting.getOptionName(i);
                            return true;
                        } 
                        else if (!isDefault && mouseX >= dropX + dropW - 35 && mouseX <= dropX + dropW - 20) {
                            String name = setting.getOptionName(i);
                            Raven.profileManager.deleteProfile(name);
                            setting.updateOptions(Raven.profileManager.getProfiles(), Raven.profileManager.getActiveProfile());
                            return true;
                        } else {
                            setting.setModeByIndex(i);
                            mod.guiButtonToggled(setting);
                            dropdownOpen = false;
                            return true;
                        }
                    }
                }
                itemY += ITEM_H;
            }

            dropdownOpen = false;
            return true;
        }

        if (mouseX >= pillX && mouseX <= pillX + pillW
                && mouseY >= pillY && mouseY <= pillY + PILL_H) {
            dropdownOpen = true;
            return true;
        }

        return false;
    }

    @Override
    public void keyTyped(char c, int key) {
        if (isRenaming && renamingIndex != -1) {
            if (key == org.lwjgl.input.Keyboard.KEY_RETURN) {
                if (!newProfileName.isEmpty()) {
                    String oldName = setting.getOptionName(renamingIndex);
                    Raven.profileManager.renameProfile(oldName, newProfileName);
                    setting.updateOptions(Raven.profileManager.getProfiles(), Raven.profileManager.getActiveProfile());
                }
                isRenaming = false;
                renamingIndex = -1;
            } else if (key == org.lwjgl.input.Keyboard.KEY_ESCAPE) {
                isRenaming = false;
                renamingIndex = -1;
            } else if (key == org.lwjgl.input.Keyboard.KEY_BACK && newProfileName.length() > 0) {
                newProfileName = newProfileName.substring(0, newProfileName.length() - 1);
            } else if (FontUtil.poppinsRegular.getStringWidth(newProfileName + c) < 120 && net.minecraft.util.ChatAllowedCharacters.isAllowedCharacter(c)) {
                newProfileName += c;
            }
        }
    }

    public boolean isDropdownOpen() {
        return dropdownOpen;
    }

    public void closeDropdown() {
        if (!isRenaming) {
            dropdownOpen = false;
        }
    }
}
