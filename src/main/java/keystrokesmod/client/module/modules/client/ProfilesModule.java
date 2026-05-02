package keystrokesmod.client.module.modules.client;

import keystrokesmod.client.main.Raven;
import keystrokesmod.client.module.Module;
import keystrokesmod.client.module.setting.Setting;
import keystrokesmod.client.module.setting.impl.ProfileComboSetting;
import keystrokesmod.client.module.setting.impl.DescriptionSetting;

import java.util.List;

public class ProfilesModule extends Module {

    public static ProfileComboSetting profileSetting;

    public ProfilesModule() {
        super("Profiles", ModuleCategory.client);
        this.withDescription("Manage and switch configuration profiles.");
        this.registerSetting(new DescriptionSetting("Manage your configurations."));
        profileSetting = new ProfileComboSetting("Profile");
        
        // Load initial profiles
        if (Raven.profileManager != null) {
            List<String> profiles = Raven.profileManager.getProfiles();
            String active = Raven.profileManager.getActiveProfile();
            profileSetting.updateOptions(profiles, active);
        }
        
        this.registerSetting(profileSetting);
        this.clientConfig = true;
    }

    @Override
    public void onEnable() {
        // Nothing special needed on enable, functionality is in the settings
        this.disable();
    }

    @Override
    public void guiButtonToggled(Setting setting) {
        if (setting == profileSetting) {
            applyProfile();
        }
    }

    public static void tick() {
        if (profileSetting == null || Raven.profileManager == null) return;
        
        // Periodically refresh the list of profiles in case they changed
        List<String> profiles = Raven.profileManager.getProfiles();
        String active = Raven.profileManager.getActiveProfile();
        
        if (profiles.size() != profileSetting.getOptionsCount()) {
            profileSetting.updateOptions(profiles, active);
        }

        String current = profileSetting.getMode();
        if (!current.equals(Raven.profileManager.getActiveProfile())) {
            applyProfile();
        }
    }

    private static void applyProfile() {
        if (profileSetting == null || Raven.profileManager == null) return;
        String selected = profileSetting.getMode();
        if (!selected.equals(Raven.profileManager.getActiveProfile())) {
            Raven.profileManager.setActiveProfile(selected);
        }
    }
}
