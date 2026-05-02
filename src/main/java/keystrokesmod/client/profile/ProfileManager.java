package keystrokesmod.client.profile;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import keystrokesmod.client.main.Raven;
import keystrokesmod.client.module.Module;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ProfileManager {

    private final File profilesDir = new File(Minecraft.getMinecraft().mcDataDir, "keystrokes" + File.separator + "profiles");
    private String activeProfileName = "Default";
    private final List<String> availableProfiles = new ArrayList<>();
    public static boolean applyingConfig;

    public ProfileManager() {
        if (!profilesDir.exists()) profilesDir.mkdirs();
        scanProfiles();
        if (availableProfiles.isEmpty()) {
            createProfile("Default");
        } else if (!availableProfiles.contains("Default")) {
            activeProfileName = availableProfiles.get(0);
        }
    }

    public void scanProfiles() {
        availableProfiles.clear();
        File[] files = profilesDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".json")) {
                    availableProfiles.add(file.getName().replace(".json", ""));
                }
            }
        }
        if (availableProfiles.isEmpty()) {
            availableProfiles.add("Default");
        }
    }

    public List<String> getProfiles() {
        scanProfiles();
        return availableProfiles;
    }

    public String getActiveProfile() {
        return activeProfileName;
    }

    public void setActiveProfile(String name) {
        if (!availableProfiles.contains(name)) return;
        saveProfile(); // Save current before switching
        this.activeProfileName = name;
        loadProfile(); // Load the new one
        if (Raven.clientConfig != null) {
            Raven.clientConfig.saveConfig();
        }
    }

    public void loadProfileByName(String name) {
        if (availableProfiles.contains(name)) {
            this.activeProfileName = name;
            loadProfile();
        }
    }

    public void createProfile(String name) {
        if (availableProfiles.contains(name)) return;
        availableProfiles.add(name);
        // Switch to the new profile and save current settings as its defaults
        saveProfile(); 
        this.activeProfileName = name;
        saveProfile(); // Save to new file
        if (Raven.clientConfig != null) {
            Raven.clientConfig.saveConfig();
        }
    }

    public void createAutoProfile() {
        String base = "Default";
        String name = base;
        int i = 2;
        while (availableProfiles.contains(name)) {
            name = base + " " + i;
            i++;
        }
        createProfile(name);
    }

    public void renameProfile(String oldName, String newName) {
        if (oldName.equalsIgnoreCase("Default")) return; // Never rename Default
        if (availableProfiles.contains(newName)) return;
        if (!availableProfiles.contains(oldName)) return;

        File oldFile = new File(profilesDir, oldName + ".json");
        File newFile = new File(profilesDir, newName + ".json");
        
        if (oldFile.exists()) {
            oldFile.renameTo(newFile);
        }
        
        availableProfiles.remove(oldName);
        availableProfiles.add(newName);
        
        if (activeProfileName.equals(oldName)) {
            activeProfileName = newName;
            if (Raven.clientConfig != null) {
                Raven.clientConfig.saveConfig();
            }
        }
    }

    public void renameProfile(String newName) {
        renameProfile(activeProfileName, newName);
    }

    public void deleteProfile(String name) {
        if (name.equalsIgnoreCase("Default")) return; // Never delete Default
        if (!availableProfiles.contains(name)) return;
        if (availableProfiles.size() <= 1) return; // always keep at least one

        File file = new File(profilesDir, name + ".json");
        if (file.exists()) {
            file.delete();
        }

        availableProfiles.remove(name);
        
        if (activeProfileName.equals(name)) {
            activeProfileName = availableProfiles.get(0);
            loadProfile();
            if (Raven.clientConfig != null) {
                Raven.clientConfig.saveConfig();
            }
        }
    }

    public void saveProfile() {
        if (applyingConfig) return;
        JsonObject data = new JsonObject();
        data.addProperty("version", Raven.versionManager.getClientVersion().getVersion());
        data.addProperty("author", "Unknown");
        data.addProperty("notes", "");
        data.addProperty("intendedServer", "");
        data.addProperty("usedFor", 0);
        data.addProperty("lastEditTime", System.currentTimeMillis());

        JsonObject modules = new JsonObject();
        for (Module module : Raven.moduleManager.getConfigModules()) {
            if (!module.isClientConfig()) {
                modules.add(module.getName(), module.getConfigAsJson());
            }
        }
        data.add("modules", modules);

        File file = new File(profilesDir, activeProfileName + ".json");
        try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
            out.write(data.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadProfile() {
        File file = new File(profilesDir, activeProfileName + ".json");
        if (!file.exists()) return;

        applyingConfig = true;
        JsonParser jsonParser = new JsonParser();
        try (FileReader reader = new FileReader(file)) {
            JsonObject data = jsonParser.parse(reader).getAsJsonObject();
            if (data.has("modules")) {
                JsonObject modules = data.get("modules").getAsJsonObject();
                for (Module module : Raven.moduleManager.getConfigModules()) {
                    if (!module.isClientConfig()) {
                        if (modules.has(module.getName())) {
                            module.applyConfigFromJson(modules.get(module.getName()).getAsJsonObject());
                        } else {
                            module.resetToDefaults();
                        }
                    }
                }
            }
        } catch (JsonSyntaxException | ClassCastException | IOException e) {
            e.printStackTrace();
        }
        applyingConfig = false;
    }
}
