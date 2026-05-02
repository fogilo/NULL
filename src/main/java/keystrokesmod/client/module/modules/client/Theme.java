package keystrokesmod.client.module.modules.client;

import keystrokesmod.client.clickgui.nullgui.NullTheme;
import keystrokesmod.client.module.Module;
import keystrokesmod.client.module.setting.impl.ComboSetting;
import keystrokesmod.client.main.Raven;

public class Theme extends Module {
    public enum ThemeMode {
        Amethyst, Ocean, Emerald, Amber
    }

    public static ComboSetting<ThemeMode> themeCombo;
    private static ThemeMode lastTheme = ThemeMode.Amethyst;

    public Theme() {
        super("Theme", ModuleCategory.client);
        this.withDescription("Switch the entire UI color scheme.");

        themeCombo = new ComboSetting<ThemeMode>("Preset", ThemeMode.Amethyst);
        this.registerSetting(themeCombo);
    }

    public static void tick() {
        if (themeCombo != null) {
            ThemeMode current = themeCombo.getMode();
            if (current != lastTheme) {
                lastTheme = current;
                
                if (current == ThemeMode.Amethyst) {
                    NullTheme.applyPreset(NullTheme.Preset.AMETHYST);
                } else if (current == ThemeMode.Ocean) {
                    NullTheme.applyPreset(NullTheme.Preset.OCEAN);
                } else if (current == ThemeMode.Emerald) {
                    NullTheme.applyPreset(NullTheme.Preset.EMERALD);
                } else if (current == ThemeMode.Amber) {
                    NullTheme.applyPreset(NullTheme.Preset.AMBER);
                }
                
                Raven.profileManager.saveProfile();
                if (Raven.clientConfig != null) Raven.clientConfig.saveConfig();
            }
        }
    }
}
