package keystrokesmod.client.module.setting.impl;

import com.google.gson.JsonObject;
import keystrokesmod.client.clickgui.kv.KvComponent;
import keystrokesmod.client.clickgui.kv.components.KvComboComponent;
import keystrokesmod.client.clickgui.raven.Component;
import keystrokesmod.client.clickgui.raven.components.ComboComponent;
import keystrokesmod.client.clickgui.raven.components.ModuleComponent;
import keystrokesmod.client.clickgui.raven.components.SettingComponent;
import keystrokesmod.client.module.setting.Setting;
import java.util.List;
import java.util.ArrayList;

public class ProfileComboSetting extends Setting {
    private List<String> options = new ArrayList<>();
    private String currentOption = "";

    public ProfileComboSetting(String settingName) {
        super(settingName);
    }
    
    public void updateOptions(List<String> newOptions, String newCurrent) {
        this.options = newOptions;
        this.currentOption = newCurrent;
    }

    @Override
    public void resetToDefaults() { }

    @Override
    public JsonObject getConfigAsJson() {
        JsonObject data = new JsonObject();
        data.addProperty("type", getSettingType());
        data.addProperty("value", currentOption);
        return data;
    }

    @Override
    public String getSettingType() {
        return "profilecombo";
    }

    @Override
    public void applyConfigFromJson(JsonObject data) {
        if (!data.has("type") || !data.get("type").getAsString().equals(getSettingType()))
            return;
        if (!data.has("value")) return;
        String val = data.get("value").getAsString();
        if (options.contains(val)) currentOption = val;
    }

    @Override
    public Component createComponent(ModuleComponent moduleComponent) { return null; }

    public String getMode() { return this.currentOption; }
    public void setMode(String value) { this.currentOption = value; }
    
    public void nextMode() {
        if (options.isEmpty()) return;
        int idx = options.indexOf(currentOption);
        currentOption = options.get((idx + 1) % options.size());
    }
    
    public void prevMode() {
        if (options.isEmpty()) return;
        int idx = options.indexOf(currentOption);
        currentOption = options.get(idx <= 0 ? options.size() - 1 : idx - 1);
    }

    public int getOptionsCount() { return options.size(); }
    public String getOptionName(int index) { return options.get(index); }
    public int getCurrentIndex() { return options.indexOf(currentOption); }
    public void setModeByIndex(int index) {
        if (index >= 0 && index < options.size()) {
            currentOption = options.get(index);
        }
    }

    @Override
    public Class<? extends KvComponent> getComponentType() { return KvComboComponent.class; }

    @Override
    public Class<? extends SettingComponent> getRavenComponentType() { return ComboComponent.class; }
}
