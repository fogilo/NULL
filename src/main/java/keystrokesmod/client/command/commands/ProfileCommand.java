package keystrokesmod.client.command.commands;

import keystrokesmod.client.clickgui.raven.Terminal;
import keystrokesmod.client.command.Command;
import keystrokesmod.client.main.Raven;

public class ProfileCommand extends Command {
    public ProfileCommand() {
        super("profile", "Manages profiles", 1, 3, new String[] { "create,rename,list,delete", "profile's name" },
                new String[] { "profiles", "cfg" });
    }

    @Override
    public void onCall(String[] args) {
        if (Raven.profileManager == null) {
            Terminal.print("Profile manager not loaded.");
            return;
        }

        if (args.length == 0) {
            Terminal.print("Current profile: " + Raven.profileManager.getActiveProfile());
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                this.listProfiles();
            } else {
                this.incorrectArgs();
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("create")) {
                if (Raven.profileManager.getProfiles().contains(args[1])) {
                    Terminal.print("Profile " + args[1] + " already exists!");
                } else {
                    Raven.profileManager.createProfile(args[1]);
                    Terminal.print("Created and switched to profile: " + args[1]);
                }
            } else if (args[0].equalsIgnoreCase("rename")) {
                String oldName = Raven.profileManager.getActiveProfile();
                Raven.profileManager.renameProfile(args[1]);
                Terminal.print("Renamed profile '" + oldName + "' to '" + args[1] + "'");
            } else if (args[0].equalsIgnoreCase("delete")) {
                // To keep it safe, don't let them delete Default
                if (args[1].equalsIgnoreCase("Default")) {
                    Terminal.print("You cannot delete the Default profile.");
                    return;
                }
                // Implement delete later or now
                Terminal.print("Deleting profiles is not fully supported yet in command.");
            } else {
                this.incorrectArgs();
            }
        }
    }

    public void listProfiles() {
        Terminal.print("Available profiles: ");
        for (String profile : Raven.profileManager.getProfiles()) {
            if (Raven.profileManager.getActiveProfile().equals(profile))
                Terminal.print("Current: " + profile);
            else
                Terminal.print(profile);
        }
    }
}
