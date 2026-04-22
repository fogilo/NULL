package keystrokesmod.client.clickgui.nullgui;

/**
 * Central design-token class for the NULL Client GUI.
 * All rendering code references these constants — no hardcoded colors elsewhere.
 *
 * Palette strictly derived from the reference design images:
 *   Primary:   #A020F0   (vivid purple)
 *   Secondary: #985DC1   (muted lavender)
 *   Tertiary:  #9C5C00   (amber / gold)
 *   Neutral:   #050505   (near-black)
 */
public final class NullTheme {

    private NullTheme() {}

    // ══════════════════════════════════════════════════════════════
    //  WINDOW DIMENSIONS — centered overlay, NOT fullscreen
    // ══════════════════════════════════════════════════════════════
    /** Percentage of screen width the window occupies (0.0–1.0) */
    public static final float WINDOW_WIDTH_RATIO     = 0.72f;
    /** Percentage of screen height the window occupies (0.0–1.0) */
    public static final float WINDOW_HEIGHT_RATIO    = 0.75f;
    /** Corner radius for the outer window frame */
    public static final int WINDOW_RADIUS            = 12;

    // ══════════════════════════════════════════════════════════════
    //  BACKGROUND & SURFACE  — tonal depth layering
    // ══════════════════════════════════════════════════════════════
    /** Screen-dim behind the window overlay — 50% black */
    public static final int OVERLAY                  = 0x80000000;

    /** Deepest background — neutral (#050505) */
    public static final int SURFACE                  = 0xFF050505;
    /** Main canvas / primary content area (#0D0D0E) */
    public static final int SURFACE_DIM              = 0xFF0D0D0E;
    /** Floating panels (#131315) */
    public static final int SURFACE_CONTAINER_LOW    = 0xFF131315;
    /** Interactive elements / elevated cards (#1A1A1E) */
    public static final int SURFACE_CONTAINER        = 0xFF1A1A1E;
    /** Highest elevation surfaces (#222228) */
    public static final int SURFACE_CONTAINER_HIGH   = 0xFF222228;
    /** Bright surface for hovered interactive (#2C2C32) */
    public static final int SURFACE_BRIGHT           = 0xFF2C2C32;

    /** Ghost Border — 15% opacity outline_variant (#484849) */
    public static final int GHOST_BORDER             = 0x26484849;
    /** Stronger ghost border for expanded cards */
    public static final int GHOST_BORDER_STRONG      = 0x40484849;

    /** Sidebar background — deepest surface with slight glass */
    public static final int SIDEBAR_BG               = 0xF0080808;
    /** Main window body — slight transparency for glassmorphism */
    public static final int BG_MAIN                  = 0xF00D0D0E;

    /** Module card — default collapsed background */
    public static final int CARD_BG                  = 0x40101010; // Dark grey box
    /** Module card — hover */
    public static final int CARD_HOVER               = 0x30993399; // Pleasant purple-pink
    /** Module card — expanded */
    public static final int CARD_EXPANDED            = 0x40151515;
    /** Settings area inside expanded card */
    public static final int CARD_SETTINGS_BG         = 0x40101010;

    // ══════════════════════════════════════════════════════════════
    //  PRIMARY ACCENT — Vivid Purple (#A020F0)
    // ══════════════════════════════════════════════════════════════
    /** Primary vivid purple — active elements, toggles ON */
    public static final int ACCENT                   = 0xFFA020F0;
    /** Dimmer purple for gradient effects (#7B18B8) */
    public static final int ACCENT_DIM               = 0xFF7B18B8;
    /** Glow effect — 40% opacity primary */
    public static final int ACCENT_GLOW              = 0x66A020F0;
    /** Soft glow / aura — 10% opacity primary */
    public static final int ACCENT_GLOW_SOFT         = 0x1AA020F0;

    // ══════════════════════════════════════════════════════════════
    //  SECONDARY — Muted Lavender (#985DC1)
    // ══════════════════════════════════════════════════════════════
    public static final int SECONDARY                = 0xFF985DC1;
    public static final int SECONDARY_DIM            = 0xFF7A4A9E;

    // ══════════════════════════════════════════════════════════════
    //  TERTIARY — Amber/Gold (#9C5C00)
    // ══════════════════════════════════════════════════════════════
    /** Used for slider value pills, special badges */
    public static final int TERTIARY                 = 0xFF9C5C00;
    public static final int TERTIARY_BRIGHT          = 0xFFBF7400;

    // ══════════════════════════════════════════════════════════════
    //  FAVORITE STAR — Pink (#FF69B4 / #FF1493)
    // ══════════════════════════════════════════════════════════════
    /** Star filled when module is favorited — hot pink */
    public static final int STAR_ACTIVE              = 0xFFFF69B4;
    /** Star outline when not favorited — muted gray */
    public static final int STAR_INACTIVE            = 0xFF555558;
    /** Star hover state — brighter pink */
    public static final int STAR_HOVER               = 0xFFFF8AC5;

    // ══════════════════════════════════════════════════════════════
    //  CATEGORY SIDEBAR
    // ══════════════════════════════════════════════════════════════
    /** Active category sidebar indicator bar */
    public static final int ACTIVE_INDICATOR         = ACCENT;
    /** Category hover highlight — 10% primary */
    public static final int CAT_HOVER                = 0x1AA020F0;
    /** Active category background — 15% primary */
    public static final int CAT_ACTIVE_BG            = 0x26A020F0;

    // ══════════════════════════════════════════════════════════════
    //  TEXT COLORS
    // ══════════════════════════════════════════════════════════════
    /** Headlines, branding — pure white */
    public static final int TEXT_PRIMARY              = 0xFFFFFFFF;
    /** Module names when enabled — bright purple */
    public static final int TEXT_ENABLED             = 0xFFA020F0;
    /** Module names when disabled — light gray */
    public static final int TEXT_DISABLED            = 0xFFB0ADB0;
    /** Descriptions, secondary info */
    public static final int TEXT_SECONDARY           = 0xFF787878;
    /** Settings labels — uppercase labels */
    public static final int TEXT_LABEL               = 0xFFB0ADB0;
    /** Settings values — primary accent */
    public static final int TEXT_VALUE               = ACCENT;
    /** Category header/breadcrumb — muted secondary */
    public static final int TEXT_CATEGORY_HEADER     = SECONDARY;
    /** Module description inline text */
    public static final int TEXT_DESCRIPTION         = 0xFF606060;

    // ══════════════════════════════════════════════════════════════
    //  TOGGLE SWITCH
    // ══════════════════════════════════════════════════════════════
    public static final int TOGGLE_ON_TRACK          = ACCENT;
    public static final int TOGGLE_OFF_TRACK         = 0xFF333338;
    public static final int TOGGLE_KNOB              = 0xFFFFFFFF;
    public static final int TOGGLE_GLOW              = ACCENT_GLOW;

    // ══════════════════════════════════════════════════════════════
    //  CHECKBOX (settings boolean toggle)
    // ══════════════════════════════════════════════════════════════
    public static final int CHECKBOX_CHECKED_BG      = ACCENT;
    public static final int CHECKBOX_UNCHECKED_BG    = 0xFF333338;
    public static final int CHECKBOX_BORDER          = 0xFF555558;
    public static final int CHECKBOX_CHECK_COLOR     = 0xFFFFFFFF;

    // ══════════════════════════════════════════════════════════════
    //  SLIDER
    // ══════════════════════════════════════════════════════════════
    public static final int SLIDER_TRACK             = 0xFF333338;
    public static final int SLIDER_FILL              = ACCENT;
    public static final int SLIDER_KNOB              = 0xFFFFFFFF;
    /** Value pill background — uses tertiary accent */
    public static final int SLIDER_VALUE_PILL_BG     = 0xFF2A1F3A;
    public static final int SLIDER_VALUE_PILL_TEXT    = ACCENT;

    // ══════════════════════════════════════════════════════════════
    //  COMBO SETTING (pill buttons)
    // ══════════════════════════════════════════════════════════════
    public static final int COMBO_ACTIVE_BG          = ACCENT;
    public static final int COMBO_ACTIVE_TEXT         = 0xFFFFFFFF;
    public static final int COMBO_INACTIVE_BG        = 0xFF2A2A30;
    public static final int COMBO_INACTIVE_TEXT       = 0xFF888890;

    // ══════════════════════════════════════════════════════════════
    //  BADGE (enabled-count on sidebar)
    // ══════════════════════════════════════════════════════════════
    public static final int BADGE_BG                 = ACCENT;
    public static final int BADGE_TEXT               = 0xFFFFFFFF;

    // ══════════════════════════════════════════════════════════════
    //  SIDEBAR DIMENSIONS
    // ══════════════════════════════════════════════════════════════
    public static final int SIDEBAR_W                = 180;

    // ══════════════════════════════════════════════════════════════
    //  MODULE CARD DIMENSIONS
    // ══════════════════════════════════════════════════════════════
    public static final int CARD_HEIGHT              = 42;
    public static final int CARD_RADIUS              = 8;
    public static final int CARD_GAP                 = 5;

    // ══════════════════════════════════════════════════════════════
    //  TOGGLE SWITCH DIMENSIONS (module-level)
    // ══════════════════════════════════════════════════════════════
    public static final int TOGGLE_W                 = 30;
    public static final int TOGGLE_H                 = 14;
    public static final int TOGGLE_KNOB_SIZE         = 10;
    public static final int TOGGLE_INSET             = 2;

    // ══════════════════════════════════════════════════════════════
    //  HOVER GLOW — subtle weight increase on module hover
    // ══════════════════════════════════════════════════════════════
    /** Soft purple glow behind card on hover — 8% primary */
    public static final int HOVER_GLOW               = 0x14A020F0;

    // ══════════════════════════════════════════════════════════════
    //  ANIMATION DURATIONS (ms)
    // ══════════════════════════════════════════════════════════════
    public static final int ANIM_OPEN_GUI            = 220;
    public static final int ANIM_EXPAND              = 180;
    public static final int ANIM_TOGGLE              = 200;

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
