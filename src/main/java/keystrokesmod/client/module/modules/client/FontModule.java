package keystrokesmod.client.module.modules.client;

import keystrokesmod.client.module.Module;
import keystrokesmod.client.module.setting.Setting;
import keystrokesmod.client.module.setting.impl.ComboSetting;
import keystrokesmod.client.utils.font.FontUtil;

/**
 * Font module — lets the user switch the NULL Client GUI font on the fly.
 * Lives in the Client category. Changing the combo immediately rebuilds
 * the GUI font renderers so the effect is instant.
 *
 * Persistence: Since this module is clientConfig=true and uses a ComboSetting,
 * the selected font is automatically saved/loaded by ClientConfig via the
 * existing JSON serialization in ComboSetting.getConfigAsJson/applyConfigFromJson.
 * postApplyConfig() ensures the font is rebuilt after loading from disk.
 */
public class FontModule extends Module {

    public static ComboSetting<GuiFont> fontSetting;
    private static GuiFont lastApplied = null;

    public FontModule() {
        super("Font", ModuleCategory.client);
        this.withDescription("Change the client's custom font.");
        this.registerSetting(fontSetting = new ComboSetting<>("Font", GuiFont.Poppins));
        this.clientConfig = true;
    }

    @Override
    public void guiButtonToggled(Setting setting) {
        if (setting == fontSetting) {
            applyFont();
        }
    }

    /**
     * Called after the config system restores this module's settings from disk.
     * Ensures the saved font is actually applied to the renderers on startup.
     */
    @Override
    public void postApplyConfig() {
        applyFont();
    }

    /** Called every GUI frame to detect combo changes without needing a toggle */
    public static void tick() {
        if (fontSetting == null) return;
        GuiFont current = fontSetting.getMode();
        if (current != lastApplied) {
            applyFont();
        }
    }

    private static void applyFont() {
        if (fontSetting == null) return;
        GuiFont selected = fontSetting.getMode();
        lastApplied = selected;
        FontUtil.rebuildGuiFonts(selected.fileName);
    }

    public static String getCurrentFontFile() {
        if (fontSetting == null) return "Poppins.ttf";
        return fontSetting.getMode().fileName;
    }

    public enum GuiFont {
        Inter("Inter.ttf"),
        Montserrat("Montserrat.ttf"),
        Roboto("Roboto.ttf"),
        Poppins("Poppins.ttf"),
        JetBrains_Mono("JetBrainsMono.ttf");

        public final String fileName;

        GuiFont(String fileName) {
            this.fileName = fileName;
        }

        @Override
        public String toString() {
            return name().replace('_', ' ');
        }
    }
}
