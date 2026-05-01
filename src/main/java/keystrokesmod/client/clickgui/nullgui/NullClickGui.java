package keystrokesmod.client.clickgui.nullgui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.lwjgl.input.Keyboard;
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
 * NULL Client ClickGUI — centered overlay window.
 * The game world remains visible around the edges.
 *
 * Key behavior:
 * - Bound key (right shift by default): opens GUI; closes only after release + re-press
 * - ESC and E: always close the GUI (never open)
 */
public class NullClickGui extends GuiScreen {

    // ── Virtual "favorites" tab name ──
    private static final String FAVORITES_TAB = "Favorites";

    private final List<ModuleCategory> categories = new ArrayList<ModuleCategory>();
    private String activeTab;
    private ModuleCategory activeCategory;
    private final List<NullModuleRow> rows = new ArrayList<NullModuleRow>();

    /** Module names that are favorited — persists across GUI open/close */
    public static final Set<String> favorites = new HashSet<String>();

    // ── Key debounce ──
    /** The keycode used to open this GUI (set by GuiModule) */
    public int boundKey = 54; // default: right shift
    /** Whether the bound key has been released since the GUI was opened */
    public boolean boundKeyReleased = false;

    // ── Scroll offsets ──
    private int moduleScrollOffset = 0;
    private int sidebarScrollOffset = 0;

    private final CoolDown openAnim = new CoolDown(NullTheme.ANIM_OPEN_GUI);

    // Computed window bounds (recalculated each frame)
    private int winX, winY, winW, winH;
    private float uiScale = 1.0f;

    // Category unicode icons
    private static final String[] CAT_ICONS = {
        "\u2694",  // ⚔ Combat
        "\u26A1",  // ⚡ Movement
        "\u2666",  // ♦ Player
        "\u25C9",  // ◉ Render
        "\u2022\u2022\u2022", // ••• Other
        "\u2699"   // ⚙ Client
    };

    public NullClickGui() {
        for (ModuleCategory cat : ModuleCategory.values()) {
            if (cat == ModuleCategory.category) continue;
            if (cat == ModuleCategory.config) continue;
            if (!Raven.moduleManager.getModulesInCategory(cat).isEmpty())
                categories.add(cat);
        }
        if (!categories.isEmpty()) {
            activeCategory = categories.get(0);
            activeTab = activeCategory.getName();
            rebuildRows();
        }
    }

    public void open() {
        openAnim.setCooldown(NullTheme.ANIM_OPEN_GUI);
        openAnim.start();
    }

    // ── TAB SWITCHING ────────────────────────────────────────────

    private void setActiveFavorites() {
        activeTab = FAVORITES_TAB;
        activeCategory = null;
        moduleScrollOffset = 0;
        rebuildRows();
    }

    private void setActiveCategory(ModuleCategory cat) {
        activeTab = cat.getName();
        activeCategory = cat;
        moduleScrollOffset = 0;
        rebuildRows();
    }

    private void rebuildRows() {
        rows.clear();
        if (FAVORITES_TAB.equals(activeTab)) {
            for (ModuleCategory cat : categories) {
                for (Module mod : Raven.moduleManager.getModulesInCategory(cat)) {
                    if (favorites.contains(mod.getName())) {
                        rows.add(new NullModuleRow(mod));
                    }
                }
            }
        } else if (activeCategory != null) {
            for (Module mod : Raven.moduleManager.getModulesInCategory(activeCategory)) {
                if (mod instanceof keystrokesmod.client.module.GuiModule
                        && ((keystrokesmod.client.module.GuiModule) mod).getGuiCategory() == ModuleCategory.config)
                    continue;
                rows.add(new NullModuleRow(mod));
            }
        }
    }

    /** Called by NullModuleRow when a star is toggled */
    public void onFavoriteChanged() {
        if (FAVORITES_TAB.equals(activeTab)) {
            rebuildRows();
        }
    }

    private int countEnabled(ModuleCategory cat) {
        int count = 0;
        for (Module m : Raven.moduleManager.getModulesInCategory(cat))
            if (m.isEnabled()) count++;
        return count;
    }

    // ── WINDOW GEOMETRY ──────────────────────────────────────────

    private void computeWindowBounds() {
        winW = (int) (width * NullTheme.WINDOW_WIDTH_RATIO);
        winH = (int) (height * NullTheme.WINDOW_HEIGHT_RATIO);
        winX = (width - winW) / 2;
        winY = (height - winH) / 2;
    }

    // ── ANIMATION & PARTICLES ────────────────────────────────────
    public static float smoothMx = -1, smoothMy = -1;
    private long lastFrameTime;
    
    private static class Particle {
        float x, y, speed, size;
        Particle(int w, int h) {
            x = (float)(Math.random() * w);
            y = (float)(Math.random() * h);
            speed = 0.2f + (float)Math.random() * 0.5f;
            size = 1.0f + (float)Math.random() * 2.0f;
        }
        void update(int h, float delta) {
            y -= speed * delta;
            if (y < 0) {
                y = h;
                x = (float)(Math.random() * Raven.mc.displayWidth); // rough bounds
            }
        }
    }
    private List<Particle> particles = new ArrayList<>();

    @Override
    public void initGui() {
        super.initGui();
        lastFrameTime = System.currentTimeMillis();
        smoothMx = -1;
        smoothMy = -1;
        particles.clear();
        for (int i = 0; i < 80; i++) { // Increased particle count
            particles.add(new Particle(width, height));
        }
        open();
    }

    // ── RENDERING ────────────────────────────────────────────────

    @Override
    public void drawScreen(int mx, int my, float partial) {
        super.drawScreen(mx, my, partial);
        computeWindowBounds();

        // Delta Time calculation
        long now = System.currentTimeMillis();
        float delta = (now - lastFrameTime) / 16.0f; // normalize to ~60 FPS
        lastFrameTime = now;

        // Smooth Mouse Tracking (200ms ease-out approximation via LERP)
        if (smoothMx == -1) {
            smoothMx = mx;
            smoothMy = my;
        } else {
            smoothMx += (mx - smoothMx) * Math.min(1.0f, 0.15f * delta);
            smoothMy += (my - smoothMy) * Math.min(1.0f, 0.15f * delta);
        }

        float t = Utils.Client.smoothPercent(
            openAnim.getElapsedTime() / (float) openAnim.getCooldownTime()
        );

        // Dim overlay behind the window
        Gui.drawRect(0, 0, width, height, NullTheme.OVERLAY);

        // Draw Particles behind the main UI elements
        for (Particle p : particles) {
            p.update(height, delta);
            float dist = (float) Math.hypot(p.x - smoothMx, p.y - smoothMy);
            float pAlpha = 0.35f; // stronger base opacity
            float pSize = p.size;
            // React to flashlight
            if (dist < NullTheme.FLASHLIGHT_RADIUS) {
                float intensity = 1.0f - (dist / NullTheme.FLASHLIGHT_RADIUS);
                pAlpha += intensity * 1.0f; // stronger reaction
                pSize += intensity * 2.5f; // larger growth
            }
            int color = (Math.min(255, (int)(pAlpha * 255)) << 24) | (NullTheme.PARTICLE_BASE & 0x00FFFFFF);
            RenderUtils.drawRoundedRect(p.x - pSize/2, p.y - pSize/2, p.x + pSize/2, p.y + pSize/2, pSize/2, color);
        }

        GL11.glPushMatrix();
        float cx = winX + winW / 2f;
        float cy = winY + winH / 2f;
        GL11.glTranslatef(cx, cy, 0f);
        GL11.glScalef(t, t, 1f);
        GL11.glTranslatef(-cx, -cy, 0f);

        drawWindowFrame();
        
        // Draw Flashlight ON TOP of the window background, but behind the text/modules
        RenderUtils.drawRadialGradient(smoothMx, smoothMy, NullTheme.FLASHLIGHT_RADIUS, NullTheme.FLASHLIGHT_CENTER, NullTheme.FLASHLIGHT_EDGE);

        drawSidebar(mx, my);
        drawModulePanel(mx, my);

        GL11.glPopMatrix();
    }

    private void drawWindowFrame() {
        // Outer window
        RenderUtils.drawRoundedRect(winX, winY, winX + winW, winY + winH,
                NullTheme.WINDOW_RADIUS, NullTheme.BG_MAIN);
        RenderUtils.drawRoundedOutline(winX, winY, winX + winW, winY + winH,
                NullTheme.WINDOW_RADIUS, 1, NullTheme.GHOST_BORDER);
        // Sidebar panel
        RenderUtils.drawRoundedRect(winX, winY, winX + NullTheme.SIDEBAR_W, winY + winH,
                NullTheme.WINDOW_RADIUS, NullTheme.SIDEBAR_BG);
        // Sidebar divider
        Gui.drawRect(winX + NullTheme.SIDEBAR_W, winY + 12,
                     winX + NullTheme.SIDEBAR_W + 1, winY + winH - 12, NullTheme.GHOST_BORDER);
    }

    /** Total height of all sidebar content below the brand header */
    private int sidebarContentHeight;
    /** Y position where scrollable sidebar content starts (below branding) */
    private int sidebarContentStartY;

    private void applyScissor(float x, float y, float w, float h) {
        ScaledResolution sr = new ScaledResolution(Raven.mc);
        int scale = sr.getScaleFactor();
        
        int scissorX = (int) (x * scale);
        int scissorY = (int) (Raven.mc.displayHeight - (y + h) * scale);
        int scissorW = (int) (w * scale);
        int scissorH = (int) (h * scale);
        
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(scissorX, scissorY, scissorW, scissorH);
    }

    private void drawSidebar(int mx, int my) {
        int sbX = winX;
        int sbRight = winX + NullTheme.SIDEBAR_W;

        // ── Branding (fixed — does NOT scroll) ──
        float brandY = winY + 20;
        
        float nullW = (float) FontUtil.nullTitle.getStringWidth("NULL");
        float clientW = (float) FontUtil.nullTitle.getStringWidth("CLIENT");
        int totalBrandW = (int)(nullW + 4 + clientW);
        int brandH = FontUtil.nullTitle.getHeight();
        int padX = 5;
        int padY = 3;
        
        // Glow effect for NULL Client (perfectly symmetrical)
        RenderUtils.drawRoundedRect(sbX + 16 - padX, (int)brandY - padY, sbX + 16 + totalBrandW + padX, (int)brandY + brandH + padY - 1, 6, NullTheme.ACCENT_GLOW_SOFT);
        
        FontUtil.nullTitle.drawSmoothString("NULL", sbX + 16, brandY, NullTheme.TEXT_PRIMARY);
        FontUtil.nullTitle.drawSmoothString("CLIENT", sbX + 16 + nullW + 4, brandY, NullTheme.ACCENT);
        FontUtil.poppinsRegular.drawSmoothString("v1.0.0", sbX + 16, brandY + brandH + 8, NullTheme.TEXT_SECONDARY);

        // ★ Favorites — Sticky, fixed below branding
        int favSy = (int)(brandY + 42);
        drawSidebarItem(sbX, sbRight, favSy, FAVORITES_TAB.equals(activeTab),
                "Favorites",
                FAVORITES_TAB.equals(activeTab) ? NullTheme.STAR_ACTIVE : NullTheme.TEXT_SECONDARY,
                favorites.isEmpty() ? -1 : favorites.size(), mx, my);
        
        // Thin separator after favorites
        Gui.drawRect(sbX + 16, favSy + 28, sbRight - 16, favSy + 29, NullTheme.GHOST_BORDER);

        sidebarContentStartY = favSy + 35;
        int sidebarBottomY = winY + winH - 8;

        // ── Scissor for scrollable sidebar categories ──
        applyScissor(sbX, sidebarContentStartY, NullTheme.SIDEBAR_W, sidebarBottomY - sidebarContentStartY);

        int spacing = 34;
        int sy = sidebarContentStartY + sidebarScrollOffset + 8;

        // Real categories
        for (int i = 0; i < categories.size(); i++) {
            ModuleCategory cat = categories.get(i);
            boolean active = activeCategory == cat;

            int textColor = active ? NullTheme.ACCENT : NullTheme.TEXT_SECONDARY;
            int badge = countEnabled(cat);

            drawSidebarItem(sbX, sbRight, sy, active, cat.getName(),
                    textColor, badge, mx, my);
            sy += spacing;
        }

        sidebarContentHeight = sy - sidebarContentStartY - sidebarScrollOffset;

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    private void drawSidebarItem(int sbX, int sbRight, int sy, boolean active,
                                  String name, int textColor,
                                  int badge, int mx, int my) {
        int itemLeft = sbX + 6;
        int itemRight = sbRight - 6;
        int itemTop = sy - 3;
        int itemBottom = sy + 24;

        boolean hover = !active && mx >= itemLeft && mx <= itemRight
                && my >= itemTop && my <= itemBottom;

        if (active) {
            RenderUtils.drawRoundedRect(itemLeft, itemTop, itemRight, itemBottom, 6, NullTheme.CAT_ACTIVE_BG);
            RenderUtils.drawRoundedRect(itemLeft, itemTop + 5, itemLeft + 3, itemBottom - 5, 2, NullTheme.ACTIVE_INDICATOR);
        } else if (hover) {
            RenderUtils.drawRoundedRect(itemLeft, itemTop, itemRight, itemBottom, 6, NullTheme.CAT_HOVER);
        }

        FontUtil.nullCategory.drawSmoothString(name, sbX + 16,
                sy + (21 - FontUtil.nullCategory.getHeight()) / 2f, textColor);

        if (badge > 0) {
            String countStr = String.valueOf(badge);
            float badgeTextW = (float) FontUtil.poppinsRegular.getStringWidth(countStr);
            int badgeW = Math.max(16, (int) badgeTextW + 8);
            int badgeX = itemRight - badgeW - 6;
            int badgeY = sy + 1;
            int badgeBg = active ? NullTheme.BADGE_BG : NullTheme.SECONDARY_DIM;
            RenderUtils.drawRoundedRect(badgeX, badgeY, badgeX + badgeW, badgeY + 16, 8, badgeBg);
            FontUtil.poppinsRegular.drawSmoothString(countStr,
                    badgeX + (badgeW - badgeTextW) / 2f,
                    badgeY + (16 - FontUtil.poppinsRegular.getHeight()) / 2f, NullTheme.BADGE_TEXT);
        }
    }

    // ── MODULE PANEL ─────────────────────────────────────────────

    private void drawModulePanel(int mx, int my) {
        int panelX = winX + NullTheme.SIDEBAR_W + 1;
        int panelW = winW - NullTheme.SIDEBAR_W - 1;

        // Category title — clean, minimal
        int headerY = winY + 22;
        String title = FAVORITES_TAB.equals(activeTab) ? "Favorites" :
                (activeCategory != null ? activeCategory.getName() : "");
        FontUtil.nullTitle.drawSmoothString(title, panelX + 20, headerY, NullTheme.TEXT_PRIMARY);

        String countLabel = rows.size() + " modules";
        FontUtil.poppinsRegular.drawSmoothString(countLabel,
                panelX + 20 + (float) FontUtil.nullTitle.getStringWidth(title) + 10,
                headerY + 4, NullTheme.TEXT_SECONDARY);

        int contentStartY = headerY + 28;

        // Scissor for scrollable module list
        applyScissor(panelX, contentStartY, panelW, (winY + winH - 8) - contentStartY);

        int rowY = contentStartY + moduleScrollOffset + 8;
        int rowPadding = 16;
        int maxRowW = Math.min(panelW - rowPadding * 2, 700);
        int rowX = panelX + rowPadding;

        for (NullModuleRow row : rows) {
            row.setPosition(rowX, rowY, maxRowW);
            row.draw(mx, my);
            rowY += row.getTotalHeight();
        }

        // Empty favorites message
        if (rows.isEmpty() && FAVORITES_TAB.equals(activeTab)) {
            FontUtil.poppinsRegular.drawSmoothString("Click the star on any module to add it here.",
                    rowX, contentStartY + 16, NullTheme.TEXT_SECONDARY);
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    // ── INPUT: KEYBOARD ──────────────────────────────────────────

    @Override
    public void handleKeyboardInput() throws IOException {
        int key = Keyboard.getEventKey();
        boolean down = Keyboard.getEventKeyState();

        // ── Bound key debounce: close only after release + re-press ──
        if (key == boundKey) {
            if (!down) {
                boundKeyReleased = true;
            } else if (boundKeyReleased) {
                closeGui();
                return;
            }
        }

        // Let parent handle the rest (calls keyTyped for key-down events)
        super.handleKeyboardInput();
    }

    @Override
    public void keyTyped(char c, int key) throws IOException {
        // Forward to setting components (e.g. keybind setting)
        rows.forEach(r -> r.keyTyped(c, key));

        // ESC and E always close
        if (key == Keyboard.KEY_ESCAPE || key == Keyboard.KEY_E) {
            closeGui();
        }
    }

    private void closeGui() {
        Raven.mc.displayGuiScreen(null);
        Raven.configManager.save();
        if (Raven.clientConfig != null) Raven.clientConfig.saveConfig();
    }

    // ── INPUT: MOUSE ─────────────────────────────────────────────

    @Override
    public void mouseClicked(int mx, int my, int button) throws IOException {
        computeWindowBounds();

        // Click outside window — ignore (don't close, user might misclick)
        if (mx < winX || mx > winX + winW || my < winY || my > winY + winH) {
            return;
        }

        int sbX = winX;
        int sbRight = winX + NullTheme.SIDEBAR_W;

        // ── Sidebar clicks ──
        if (mx >= sbX && mx <= sbRight) {
            float brandY = winY + 20;
            int favSy = (int)(brandY + 36);

            // Favorites tab
            if (mx >= sbX + 6 && mx <= sbRight - 6 && my >= favSy - 3 && my <= favSy + 24) {
                setActiveFavorites();
                return;
            }

            int spacing = 34;
            int sy = sidebarContentStartY + sidebarScrollOffset;

            // Category tabs
            for (ModuleCategory cat : categories) {
                if (mx >= sbX + 6 && mx <= sbRight - 6 && my >= sy - 3 && my <= sy + 24) {
                    setActiveCategory(cat);
                    return;
                }
                sy += spacing;
            }
            return;
        }

        // ── Module rows ──
        for (NullModuleRow row : rows) {
            if (row.mouseDown(mx, my, button)) {
                onFavoriteChanged();
                return;
            }
        }
    }

    @Override
    public void mouseReleased(int mx, int my, int button) {
        rows.forEach(r -> r.mouseReleased(mx, my, button));
        if (Raven.clientConfig != null) Raven.clientConfig.saveConfig();
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int scroll = Mouse.getEventDWheel();
        if (scroll == 0) return;

        computeWindowBounds();
        int mx = Mouse.getEventX() * width / Raven.mc.displayWidth;
        int my = height - Mouse.getEventY() * height / Raven.mc.displayHeight - 1;

        int sbRight = winX + NullTheme.SIDEBAR_W;

        // ── Sidebar scroll ──
        if (mx >= winX && mx <= sbRight && my >= winY && my <= winY + winH) {
            sidebarScrollOffset += scroll > 0 ? 20 : -20;
            int sidebarVisibleH = (winY + winH - 8) - sidebarContentStartY;
            int minScroll = Math.min(0, sidebarVisibleH - sidebarContentHeight);
            sidebarScrollOffset = Math.max(minScroll, Math.min(0, sidebarScrollOffset));
            return;
        }

        // ── Module panel scroll ──
        int panelX = winX + NullTheme.SIDEBAR_W;
        if (mx >= panelX && mx <= winX + winW && my >= winY && my <= winY + winH) {
            moduleScrollOffset += scroll > 0 ? 25 : -25;

            int totalH = 0;
            for (NullModuleRow r : rows) totalH += r.getTotalHeight();
            totalH += 16;

            int headerY = winY + 22;
            int contentStartY = headerY + 28;
            int visibleH = (winY + winH) - contentStartY;
            int minScroll = Math.min(0, visibleH - totalH);
            moduleScrollOffset = Math.max(minScroll, Math.min(0, moduleScrollOffset));
        }
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
