package keystrokesmod.client.clickgui.nullgui;

/**
 * Central design-token class for the NULL Client GUI.
 * All rendering code references these constants — no hardcoded colors elsewhere.
 *
 * Palette derived from the Stitch "Obsidian Terminal" design system
 * with user-specified deep-purple (#120520) background override.
 */
public final class NullTheme {

    private NullTheme() {}

    // ══════════════════════════════════════════════════════════════
    //  BACKGROUND & SURFACE
    // ══════════════════════════════════════════════════════════════
    /** Full-screen dim overlay (semi-transparent purple-black) */
    public static final int OVERLAY          = 0xC0080210;

    /** Main window gradient — top */
    public static final int BG_TOP           = 0xFF120520;
    /** Main window gradient — bottom */
    public static final int BG_BOTTOM        = 0xFF0A0112;

    /** Sidebar panel background */
    public static final int SIDEBAR_BG       = 0xFF0D0318;
    /** Sidebar divider line */
    public static final int SIDEBAR_DIVIDER  = 0xFF1E1430;

    /** Module card — default collapsed background */
    public static final int CARD_BG          = 0xFF1A1428;
    /** Module card — hover */
    public static final int CARD_HOVER       = 0xFF241A38;
    /** Module card — expanded header */
    public static final int CARD_EXPANDED    = 0xFF1E1630;
    /** Settings area inside expanded card */
    public static final int CARD_SETTINGS_BG = 0xFF151020;

    // ══════════════════════════════════════════════════════════════
    //  ACCENT / PRIMARY
    // ══════════════════════════════════════════════════════════════
    /** Neon violet — active elements, toggles ON, slider fills */
    public static final int ACCENT           = 0xFFCC97FF;
    /** Darker violet — gradient end for signature texture */
    public static final int ACCENT_DIM       = 0xFF9C48EA;
    /** Glow effect — 25% opacity accent */
    public static final int ACCENT_GLOW      = 0x40CC97FF;
    /** Subtle glow — 15% opacity accent */
    public static final int ACCENT_GLOW_SOFT = 0x26CC97FF;

    /** Active category sidebar indicator (4px left bar) */
    public static final int ACTIVE_INDICATOR = ACCENT;
    /** Category hover highlight */
    public static final int CAT_HOVER        = 0x18CC97FF;
    /** Active category background */
    public static final int CAT_ACTIVE_BG    = 0x22CC97FF;

    // ══════════════════════════════════════════════════════════════
    //  TEXT
    // ══════════════════════════════════════════════════════════════
    /** Headlines, branding — pure white (reserve for display-level text) */
    public static final int TEXT_PRIMARY     = 0xFFFFFFFF;
    /** Module names when enabled */
    public static final int TEXT_ENABLED     = 0xFFE8D8FF;
    /** Module names when disabled */
    public static final int TEXT_DISABLED    = 0xFF888090;
    /** Descriptions, secondary info */
    public static final int TEXT_SECONDARY   = 0xFFADAAAA;
    /** Settings labels */
    public static final int TEXT_LABEL       = 0xFFCCC4D8;
    /** Settings values */
    public static final int TEXT_VALUE       = ACCENT;
    /** Category header above module panel */
    public static final int TEXT_CATEGORY_HEADER = 0xFFCC97FF;

    // ══════════════════════════════════════════════════════════════
    //  TOGGLE SWITCH
    // ══════════════════════════════════════════════════════════════
    public static final int TOGGLE_ON_TRACK  = ACCENT;
    public static final int TOGGLE_OFF_TRACK = 0xFF2A2030;
    public static final int TOGGLE_KNOB      = 0xFFFFFFFF;
    public static final int TOGGLE_GLOW      = ACCENT_GLOW;

    // ══════════════════════════════════════════════════════════════
    //  SLIDER
    // ══════════════════════════════════════════════════════════════
    public static final int SLIDER_TRACK     = 0xFF2A2030;
    public static final int SLIDER_FILL      = ACCENT;
    public static final int SLIDER_KNOB      = 0xFFFFFFFF;

    // ══════════════════════════════════════════════════════════════
    //  BADGE (enabled-count on sidebar)
    // ══════════════════════════════════════════════════════════════
    public static final int BADGE_BG         = 0xFF9C48EA;
    public static final int BADGE_TEXT       = 0xFFFFFFFF;

    // ══════════════════════════════════════════════════════════════
    //  WINDOW DIMENSIONS  (at guiScale = 3)
    // ══════════════════════════════════════════════════════════════
    public static final int WIN_W            = 420;
    public static final int WIN_H            = 280;
    public static final int SIDEBAR_W        = 90;

    // ══════════════════════════════════════════════════════════════
    //  MODULE CARD DIMENSIONS
    // ══════════════════════════════════════════════════════════════
    public static final int CARD_HEIGHT      = 28;
    public static final int CARD_RADIUS      = 8;
    public static final int CARD_GAP         = 3;

    // ══════════════════════════════════════════════════════════════
    //  TOGGLE SWITCH DIMENSIONS
    // ══════════════════════════════════════════════════════════════
    public static final int TOGGLE_W         = 26;
    public static final int TOGGLE_H         = 12;
    public static final int TOGGLE_KNOB_SIZE = 10;
    public static final int TOGGLE_INSET     = 1;

    // ══════════════════════════════════════════════════════════════
    //  ANIMATION DURATIONS (ms)
    // ══════════════════════════════════════════════════════════════
    public static final int ANIM_OPEN_GUI    = 200;
    public static final int ANIM_EXPAND      = 200;
    public static final int ANIM_TOGGLE      = 250;

    // ══════════════════════════════════════════════════════════════
    //  UTILITY: lerp between two ARGB colors
    // ══════════════════════════════════════════════════════════════
    public static int lerpColor(int from, int to, float t) {
        if (t <= 0f) return from;
        if (t >= 1f) return to;
        int a = lerp((from >> 24) & 0xFF, (to >> 24) & 0xFF, t);
        int r = lerp((from >> 16) & 0xFF, (to >> 16) & 0xFF, t);
        int g = lerp((from >>  8) & 0xFF, (to >>  8) & 0xFF, t);
        int b = lerp( from        & 0xFF,  to        & 0xFF, t);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static int lerp(int a, int b, float t) {
        return (int) (a + (b - a) * t);
    }
}
