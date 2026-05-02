import os
import re

descriptions = {
    "NoFall": "Prevents fall damage.",
    "AutoTool": "Automatically switches to the best tool.",
    "BedAura": "Automatically destroys beds through walls.",
    "AutoPlace": "Automatically places blocks.",
    "Parkour": "Automatically jumps at the edge of blocks.",
    "AutoArmour": "Automatically equips the best armor.",
    "Freecam": "Allows you to fly out of your body.",
    "RightClicker": "Automatically clicks your right mouse button.",
    "Clutch": "Helps you clutch by automatically placing blocks.",
    "Blink": "Simulates lag by withholding packets.",
    "SafeWalk": "Prevents you from walking off edges.",
    "ChestStealer": "Automatically loots chests quickly.",
    "BridgeAssist": "Helps you aim while bridging.",
    "FastPlace": "Removes place delay for blocks.",
    "ProfilesModule": "Manage and switch configuration profiles.",
    "FontModule": "Change the client's custom font.",
    "Targets": "Configure target priorities and filters.",
    "GuiModule": "Settings for the ClickGUI.",
    "SelfDestruct": "Unloads the client from the game completely.",
    "JumpReset": "Resets your sprint by jumping when hit.",
    "LeftClicker": "Automatically clicks your left mouse button.",
    "WTap": "Automatically W-Taps to increase knockback.",
    "DelayRemover": "Removes the 1.8 hit delay.",
    "LegitAura2": "A highly customizable combat assist.",
    "AimAssist": "Smoothly aims at targets for you.",
    "BlockHit": "Automatically blocks your sword when attacking.",
    "KillAura": "Automatically attacks entities around you.",
    "Reach": "Extends your attack reach distance.",
    "KnockbackDelay": "Delays knockback for smoother combat.",
    "HitBox": "Expands the hitboxes of entities.",
    "AutoBlock": "Automatically blocks attacks for you.",
    "Chams": "Renders entities through walls.",
    "PlayerESP": "Draws boxes and outlines around players.",
    "TargetHUD": "Displays information about your current target.",
    "Nametags": "Renders larger nametags with health and armor.",
    "CursorTrail": "Draws a trail behind your cursor in GUI.",
    "Tracers": "Draw lines to players.",
    "Projectiles": "Shows the predicted path of projectiles.",
    "Radar": "Displays a minimap with nearby entities.",
    "ChestESP": "Renders chests through walls.",
    "Fullbright": "Brightens up the world.",
    "Timer": "Changes the speed of the game.",
    "KeepSprint": "Prevents sprint from stopping when hitting entities.",
    "Fly": "Allows you to fly in survival mode.",
    "Sprint": "Automatically sprints for you.",
    "SpeedTest": "A movement testing module.",
    "WaterBucket": "Automatically places a water bucket to prevent fall damage.",
    "NameHider": "Hides your name and other players' names.",
    "MiddleClick": "Throws pearls or performs actions on middle click.",
    "AntiBot": "Ignores non-player entities in combat modules.",
    "HUD": "Heads up display settings."
}

directory = "/home/nico/Dokumente/NULL-Client/NULL-Client/src/main/java/keystrokesmod/client/module/modules"

count = 0

for root, _, files in os.walk(directory):
    for file in files:
        if file.endswith(".java"):
            class_name = file[:-5]
            if class_name in descriptions:
                desc = descriptions[class_name]
                path = os.path.join(root, file)
                
                with open(path, "r") as f:
                    content = f.read()
                
                if "this.withDescription(" in content or "this.withDescription (" in content:
                    # Already has description, skip or replace? Replace.
                    content = re.sub(r'this\.withDescription\([^)]+\);', f'this.withDescription("{desc}");', content)
                else:
                    # Find super(...) and append this.withDescription("...");
                    # Sometimes the super call spans multiple lines or has different spacing.
                    # We look for `super(` followed by `);`
                    pattern = r'(super\([^)]+\);)'
                    
                    if re.search(pattern, content):
                        content = re.sub(pattern, r'\1\n        this.withDescription("' + desc + r'");', content, count=1)
                    else:
                        print(f"Warning: Could not find super() in {class_name}")

                with open(path, "w") as f:
                    f.write(content)
                count += 1

print(f"Updated {count} files.")
