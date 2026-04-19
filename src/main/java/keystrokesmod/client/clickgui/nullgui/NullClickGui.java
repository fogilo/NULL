package keystrokesmod.client.clickgui.nullgui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import keystrokesmod.client.main.Raven;
import keystrokesmod.client.module.Module;
import keystrokesmod.client.module.Module.ModuleCategory;
import keystrokesmod.client.module.modules.client.GuiModule;
import keystrokesmod.client.utils.CoolDown;
import keystrokesmod.client.utils.RenderUtils;
import keystrokesmod.client.utils.Utils;
import keystrokesmod.client.utils.font.FontUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

/**
 * NULL Client ClickGUI — "Obsidian Terminal" themed sidebar layout.
 *
 * Layout:
 * ┌─────────────────────────────────────────────┐
 * │ ┌──────────┬──────────────────────────────┐ │
 * │ │ NULL     │  [Category Title]            │ │
 * │ │ Client   │  [Description]               │ │
 * │ │          ├──────────────────────────────┤ │
 * │ │ Combat 5 │  ┌─ Module Card ────────────┐│ │
 * │ │ Movement │  │ Name      [toggle]  ▼    ││ │
 * │ │ Player   │  └──────────────────────────┘│ │
 * │ │ Render   │  ...                         │ │
 * │ │ Other    │                              │ │
 * │ │ Client   │                              │ │
 * │ └──────────┴──────────────────────────────┘ │
 * └─────────────────────────────────────────────┘
 */
public class NullClickGui extends GuiScreen {

    private final List<ModuleCategory> categories = new ArrayList<ModuleCategory>();
    private ModuleCategory activeCategory;
    private final List<NullModuleRow> rows = new ArrayList<NullModuleRow>();

    private int scrollOffset = 0;
    private final CoolDown openAnim = new CoolDown(NullTheme.ANIM_OPEN_GUI);

    // Category descriptions for the header area
    private static final String[] CAT_DESCRIPTIONS = {
        "Advanced heuristic modules for PvP combat advantage.",
        "Movement enhancement and traversal modifications.",
        "Player utility modules for automation and efficiency.",
        "Visual enhancements and rendering modifications.",
        "Miscellaneous utility modules.",
        "Client configuration and interface settings."
    };

    public NullClickGui() {
        for (ModuleCategory cat : ModuleCategory.values()) {
            if (cat == ModuleCategory.category) continue;
            if (cat == ModuleCategory.config) continue;
            if (!Raven.moduleManager.getModulesInCategory(cat).isEmpty())
                categories.add(cat);
        }
        if (!categories.isEmpty())
            setActiveCategory(categories.get(0));
    }

    public void open() {
        openAnim.setCooldown(NullTheme.ANIM_OPEN_GUI);
        openAnim.start();
    }

    private void setActiveCategory(ModuleCategory cat) {
        activeCategory = cat;
        scrollOffset = 0;
        rows.clear();
        for (Module mod : Raven.moduleManager.getModulesInCategory(cat)) {
            if (mod instanceof keystrokesmod.client.module.GuiModule
                    && ((keystrokesmod.client.module.GuiModule) mod).getGuiCategory() == ModuleCategory.config)
                continue;
            rows.add(new NullModuleRow(mod));
        }
    }

    private int winX() { return (width  - NullTheme.WIN_W) / 2; }
    private int winY() { return (height - NullTheme.WIN_H) / 2; }

    private int countEnabled(ModuleCategory cat) {
        int count = 0;
        for (Module m : Raven.moduleManager.getModulesInCategory(cat))
            if (m.isEnabled()) count++;
        return count;
    }

    private int getCategoryIndex(ModuleCategory cat) {
        return categories.indexOf(cat);
    }

    // ════════════════════════════════════════════════════════════
    //  RENDERING
    // ════════════════════════════════════════════════════════════

    @Override
    public void initGui() {
        super.initGui();
        open();
    }

    @Override
    public void drawScreen(int mx, int my, float partial) {
        super.drawScreen(mx, my, partial);

        // Full-screen overlay
        drawRect(0, 0, width, height, NullTheme.OVERLAY);

        float t = Utils.Client.smoothPercent(
            openAnim.getElapsedTime() / (float) openAnim.getCooldownTime()
        );

        int wx = winX(), wy = winY();

        // Scale-in animation
        GL11.glPushMatrix();
        GL11.glTranslatef(wx + NullTheme.WIN_W / 2f, wy + NullTheme.WIN_H / 2f, 0f);
        GL11.glScalef(t, t, 1f);
        GL11.glTranslatef(-(wx + NullTheme.WIN_W / 2f), -(wy + NullTheme.WIN_H / 2f), 0f);

        drawWindowFrame(wx, wy);
        drawSidebar(mx, my, wx, wy);
        drawModulePanel(mx, my, wx, wy);

        GL11.glPopMatrix();
    }

    private void drawWindowFrame(int wx, int wy) {
        // Main window — gradient background
        // Draw bottom layer first (gradient bottom)
        RenderUtils.drawRoundedRect(wx, wy, wx + NullTheme.WIN_W, wy + NullTheme.WIN_H,
                12, NullTheme.BG_BOTTOM);
        // Top half overlay for gradient effect
        RenderUtils.drawRoundedRect(wx, wy, wx + NullTheme.WIN_W, wy + NullTheme.WIN_H / 2,
                12, NullTheme.BG_TOP, new boolean[]{true, false, false, true});

        // Sidebar panel — darker
        RenderUtils.drawRoundedRect(wx, wy, wx + NullTheme.SIDEBAR_W, wy + NullTheme.WIN_H,
                12, NullTheme.SIDEBAR_BG, new boolean[]{true, true, false, false});

        // Sidebar divider
        Gui.drawRect(wx + NullTheme.SIDEBAR_W, wy + 8,
                wx + NullTheme.SIDEBAR_W + 1, wy + NullTheme.WIN_H - 8,
                NullTheme.SIDEBAR_DIVIDER);

        // Subtle outer glow
        RenderUtils.drawRoundedOutline(wx - 1, wy - 1, wx + NullTheme.WIN_W + 1, wy + NullTheme.WIN_H + 1,
                13, 1, NullTheme.ACCENT_GLOW_SOFT);
    }

    private void drawSidebar(int mx, int my, int wx, int wy) {
        // ── Branding: "NULL Client" ──
        float brandY = wy + 12;
        FontUtil.poppinsBold.drawSmoothString("NULL", wx + 12, brandY, NullTheme.TEXT_PRIMARY);
        float nullW = (float) FontUtil.poppinsBold.getStringWidth("NULL");
        FontUtil.poppinsRegular.drawSmoothString("Client", wx + 12 + nullW + 4, brandY + 1, NullTheme.ACCENT);

        // Branding glow effect (subtle)
        RenderUtils.drawRoundedRect(wx + 8, brandY - 2, wx + NullTheme.SIDEBAR_W - 8,
                brandY + FontUtil.poppinsBold.getHeight() + 4, 6, NullTheme.ACCENT_GLOW_SOFT);

        // ── Category buttons ──
        int spacing = 24;
        int sy = wy + 38;

        for (int i = 0; i < categories.size(); i++) {
            ModuleCategory cat = categories.get(i);
            boolean active = cat == activeCategory;

            int itemLeft = wx + 4;
            int itemRight = wx + NullTheme.SIDEBAR_W - 4;
            int itemTop = sy - 2;
            int itemBottom = sy + 18;

            boolean hover = !active && mx >= itemLeft && mx <= itemRight
                    && my >= itemTop && my <= itemBottom;

            // Background
            if (active) {
                RenderUtils.drawRoundedRect(itemLeft, itemTop, itemRight, itemBottom, 6, NullTheme.CAT_ACTIVE_BG);
                // Active indicator bar (left edge)
                RenderUtils.drawRoundedRect(itemLeft, itemTop + 2, itemLeft + 3, itemBottom - 2, 2, NullTheme.ACTIVE_INDICATOR);
            } else if (hover) {
                RenderUtils.drawRoundedRect(itemLeft, itemTop, itemRight, itemBottom, 6, NullTheme.CAT_HOVER);
            }

            // Category name
            int textColor = active ? NullTheme.TEXT_PRIMARY : NullTheme.TEXT_SECONDARY;
            FontUtil.poppinsRegular.drawSmoothString(cat.getName(), wx + 12,
                    sy + (16 - FontUtil.poppinsRegular.getHeight()) / 2f, textColor);

            // Enabled count badge
            int enabledCount = countEnabled(cat);
            if (enabledCount > 0) {
                String countStr = String.valueOf(enabledCount);
                float badgeTextW = (float) FontUtil.poppinsRegular.getStringWidth(countStr);
                int badgeW = Math.max(14, (int) badgeTextW + 8);
                int badgeX = itemRight - badgeW - 2;
                int badgeY = sy;
                int badgeBg = active ? NullTheme.ACCENT : NullTheme.BADGE_BG;
                RenderUtils.drawRoundedRect(badgeX, badgeY, badgeX + badgeW, badgeY + 14, 7, badgeBg);
                FontUtil.poppinsRegular.drawSmoothString(countStr,
                        badgeX + (badgeW - badgeTextW) / 2f,
                        badgeY + (14 - FontUtil.poppinsRegular.getHeight()) / 2f, NullTheme.BADGE_TEXT);
            }

            sy += spacing;
        }
    }

    private void drawModulePanel(int mx, int my, int wx, int wy) {
        int panelX = wx + NullTheme.SIDEBAR_W + 1;
        int panelY = wy;
        int panelW = NullTheme.WIN_W - NullTheme.SIDEBAR_W - 1;
        int panelH = NullTheme.WIN_H;

        // ── Category header ──
        if (activeCategory != null) {
            // Category title
            FontUtil.poppinsBold.drawSmoothString(activeCategory.getName(),
                    panelX + 14, panelY + 12, NullTheme.TEXT_CATEGORY_HEADER);

            // Description
            int catIdx = getCategoryIndex(activeCategory);
            if (catIdx >= 0 && catIdx < CAT_DESCRIPTIONS.length) {
                FontUtil.poppinsRegular.drawSmoothString(CAT_DESCRIPTIONS[catIdx],
                        panelX + 14, panelY + 12 + FontUtil.poppinsBold.getHeight() + 2,
                        NullTheme.TEXT_SECONDARY);
            }
        }

        int headerH = 36; // space for category title + description

        // ── Scissor for scrollable module list ──
        ScaledResolution sr = new ScaledResolution(Raven.mc);
        int scale = sr.getScaleFactor();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(
            panelX * scale,
            Raven.mc.displayHeight - (panelY + panelH) * scale,
            panelW * scale,
            (panelH - headerH) * scale
        );

        // ── Module rows ──
        int rowY = panelY + headerH + 4 + scrollOffset;
        for (NullModuleRow row : rows) {
            row.setPosition(panelX + 8, rowY, panelW - 16);
            row.draw(mx, my);
            rowY += row.getTotalHeight();
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    // ════════════════════════════════════════════════════════════
    //  INPUT HANDLING
    // ════════════════════════════════════════════════════════════

    @Override
    public void mouseClicked(int mx, int my, int button) throws IOException {
        int wx = winX(), wy = winY();

        // ── Sidebar category selection ──
        int sy = wy + 38;
        int spacing = 24;
        for (ModuleCategory cat : categories) {
            int itemLeft = wx + 4;
            int itemRight = wx + NullTheme.SIDEBAR_W - 4;
            if (mx >= itemLeft && mx <= itemRight && my >= sy - 2 && my <= sy + 18) {
                setActiveCategory(cat);
                return;
            }
            sy += spacing;
        }

        // ── Module rows ──
        for (NullModuleRow row : rows)
            if (row.mouseDown(mx, my, button)) return;
    }

    @Override
    public void mouseReleased(int mx, int my, int button) {
        rows.forEach(r -> r.mouseReleased(mx, my, button));
        if (Raven.clientConfig != null) Raven.clientConfig.saveConfig();
    }

    @Override
    public void keyTyped(char c, int key) throws IOException {
        rows.forEach(r -> r.keyTyped(c, key));
        if (key == 1) { // ESC
            Raven.mc.displayGuiScreen(null);
            Raven.configManager.save();
            if (Raven.clientConfig != null) Raven.clientConfig.saveConfig();
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int scroll = Mouse.getEventDWheel();
        if (scroll == 0) return;

        int wx = winX(), wy = winY();
        int panelX = wx + NullTheme.SIDEBAR_W + 1;
        int mx = Mouse.getEventX() * width / Raven.mc.displayWidth;
        int my = height - Mouse.getEventY() * height / Raven.mc.displayHeight - 1;

        if (mx < panelX || mx > wx + NullTheme.WIN_W || my < wy || my > wy + NullTheme.WIN_H)
            return;

        scrollOffset += scroll > 0 ? 15 : -15;

        // Clamp scroll
        int totalH = 0;
        for (NullModuleRow r : rows) totalH += r.getTotalHeight();
        totalH += 20;
        int visibleH = NullTheme.WIN_H - 40;
        int minScroll = Math.min(0, visibleH - totalH);
        scrollOffset = Math.max(minScroll, Math.min(0, scrollOffset));
    }

    @Override
    public void onGuiClosed() {
        Raven.mc.gameSettings.guiScale = GuiModule.guiScale;
        Raven.configManager.save();
        if (Raven.clientConfig != null) Raven.clientConfig.saveConfig();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
