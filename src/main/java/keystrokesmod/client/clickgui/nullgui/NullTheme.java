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
    public static final int SIDEBAR_BG               = 0xFF000000;
    /** Main window body — slight transparency for glassmorphism */
    public static final int BG_MAIN                  = 0xFF000000;

    /** Module card — default collapsed background */
    public static final int CARD_BG                  = 0x40101010; // Dark grey box
    public static int CARD_HOVER               = 0x30993399; // Pleasant purple-pink
    /** Module card — expanded */
    public static final int CARD_EXPANDED            = 0x40151515;
    /** Settings area inside expanded card */
    public static final int CARD_SETTINGS_BG         = 0x40101010;

    // ══════════════════════════════════════════════════════════════
    //  PRIMARY ACCENT
    // ══════════════════════════════════════════════════════════════
    public static int ACCENT                   = 0xFFA020F0;
    public static int ACCENT_DIM               = 0xFF7B18B8;
    public static int ACCENT_GLOW              = 0x66A020F0;
    public static int ACCENT_GLOW_SOFT         = 0x1AA020F0;

    // ══════════════════════════════════════════════════════════════
    //  SECONDARY
    // ══════════════════════════════════════════════════════════════
    public static int SECONDARY                = 0xFF985DC1;
    public static int SECONDARY_DIM            = 0xFF7A4A9E;

    // ══════════════════════════════════════════════════════════════
    //  TERTIARY
    // ══════════════════════════════════════════════════════════════
    public static final int TERTIARY                 = 0xFF9C5C00;
    public static final int TERTIARY_BRIGHT          = 0xFFBF7400;

    // ══════════════════════════════════════════════════════════════
    //  FAVORITE STAR
    // ══════════════════════════════════════════════════════════════
    public static int STAR_ACTIVE              = 0xFFFF69B4;
    public static final int STAR_INACTIVE            = 0xFF555558;
    public static int STAR_HOVER               = 0xFFFF8AC5;

    // ══════════════════════════════════════════════════════════════
    //  CATEGORY SIDEBAR
    // ══════════════════════════════════════════════════════════════
    public static int ACTIVE_INDICATOR         = ACCENT;
    public static int CAT_HOVER                = 0x1AA020F0;
    public static int CAT_ACTIVE_BG            = 0x26A020F0;

    // ══════════════════════════════════════════════════════════════
    //  TEXT COLORS
    // ══════════════════════════════════════════════════════════════
    public static final int TEXT_PRIMARY              = 0xFFFFFFFF;
    public static int TEXT_ENABLED             = ACCENT;
    public static final int TEXT_DISABLED            = 0xFFB0ADB0;
    public static final int TEXT_SECONDARY           = 0xFF787878;
    public static final int TEXT_LABEL               = 0xFFB0ADB0;
    public static int TEXT_VALUE               = ACCENT;
    public static int TEXT_CATEGORY_HEADER     = SECONDARY;
    public static final int TEXT_DESCRIPTION         = 0xFF888899;

    // ── FLASHLIGHT & PARTICLES ─────────────────────────────────
    public static int FLASHLIGHT_CENTER        = 0x22A020F0;
    public static int FLASHLIGHT_EDGE          = 0x00A020F0;
    public static int PARTICLE_BASE            = 0x33A020F0;
    public static final float FLASHLIGHT_RADIUS      = 250f;

    // ══════════════════════════════════════════════════════════════
    //  TOGGLE SWITCH
    // ══════════════════════════════════════════════════════════════
    public static int TOGGLE_ON_TRACK          = ACCENT;
    public static final int TOGGLE_OFF_TRACK         = 0xFF333338;
    public static final int TOGGLE_KNOB              = 0xFFFFFFFF;
    public static int TOGGLE_GLOW              = ACCENT_GLOW;

    // ══════════════════════════════════════════════════════════════
    //  CHECKBOX (settings boolean toggle)
    // ══════════════════════════════════════════════════════════════
    public static int CHECKBOX_CHECKED_BG      = ACCENT;
    public static final int CHECKBOX_UNCHECKED_BG    = 0xFF333338;
    public static final int CHECKBOX_BORDER          = 0xFF555558;
    public static final int CHECKBOX_CHECK_COLOR     = 0xFFFFFFFF;

    // ══════════════════════════════════════════════════════════════
    //  SLIDER
    // ══════════════════════════════════════════════════════════════
    public static final int SLIDER_TRACK             = 0xFF333338;
    public static int SLIDER_FILL              = ACCENT;
    public static final int SLIDER_KNOB              = 0xFFFFFFFF;
    public static final int SLIDER_VALUE_PILL_BG     = 0xFF2A1F3A;
    public static int SLIDER_VALUE_PILL_TEXT    = ACCENT;

    // ══════════════════════════════════════════════════════════════
    //  COMBO SETTING (pill buttons)
    // ══════════════════════════════════════════════════════════════
    public static int COMBO_ACTIVE_BG          = ACCENT;
    public static final int COMBO_ACTIVE_TEXT         = 0xFFFFFFFF;
    public static final int COMBO_INACTIVE_BG        = 0xFF2A2A30;
    public static final int COMBO_INACTIVE_TEXT       = 0xFF888890;

    // ══════════════════════════════════════════════════════════════
    //  DROPDOWN MENU (expandable combo)
    // ══════════════════════════════════════════════════════════════
    public static final int DROPDOWN_BG              = 0xF0131316;
    public static int DROPDOWN_HOVER           = 0x30A020F0;
    public static int DROPDOWN_SELECTED        = 0x40A020F0;

    // ══════════════════════════════════════════════════════════════
    //  MODULE HOVER OUTLINE
    // ══════════════════════════════════════════════════════════════
    public static int HOVER_OUTLINE            = 0xC0A020F0;

    // ══════════════════════════════════════════════════════════════
    //  BADGE
    // ══════════════════════════════════════════════════════════════
    public static int BADGE_BG                 = ACCENT;
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
    //  HOVER GLOW
    // ══════════════════════════════════════════════════════════════
    public static int HOVER_GLOW               = 0x14A020F0;

    // ══════════════════════════════════════════════════════════════
    //  ANIMATION DURATIONS (ms)
    // ══════════════════════════════════════════════════════════════
    public static final int ANIM_OPEN_GUI            = 220;
    public static final int ANIM_EXPAND              = 180;
    public static final int ANIM_TOGGLE              = 200;

    // ══════════════════════════════════════════════════════════════
    //  PRESETS
    // ══════════════════════════════════════════════════════════════
    public enum Preset {
        AMETHYST(0xFFA020F0, 0xFF7B18B8, 0xFF985DC1, 0xFF7A4A9E, 0xFFFF69B4, 0xFFFF8AC5, 0x30993399, 0xC0A020F0),
        OCEAN(0xFF1447D1, 0xFF0F36A0, 0xFF5C85D6, 0xFF4369B3, 0xFF00E5FF, 0xFF4DEDFF, 0x301447D1, 0xC01447D1),
        EMERALD(0xFF0E5921, 0xFF0A4017, 0xFF539C65, 0xFF3D7A4D, 0xFF39FF14, 0xFF6DFF52, 0x300E5921, 0xC00E5921),
        AMBER(0xFF6A4B3A, 0xFF4A3428, 0xFF8B6B58, 0xFF6C5344, 0xFFFFB300, 0xFFFFC54D, 0x305C4033, 0xC06A4B3A);

        public final int accent, accentDim, secondary, secondaryDim, starActive, starHover, cardHover, hoverOutline;

        Preset(int accent, int accentDim, int secondary, int secondaryDim, int starActive, int starHover, int cardHover, int hoverOutline) {
            this.accent = accent;
            this.accentDim = accentDim;
            this.secondary = secondary;
            this.secondaryDim = secondaryDim;
            this.starActive = starActive;
            this.starHover = starHover;
            this.cardHover = cardHover;
            this.hoverOutline = hoverOutline;
        }
    }

    public static void applyPreset(Preset preset) {
        ACCENT = preset.accent;
        ACCENT_DIM = preset.accentDim;
        
        // Compute alphas based on preset accent
        int baseR = (preset.accent >> 16) & 0xFF;
        int baseG = (preset.accent >> 8) & 0xFF;
        int baseB = preset.accent & 0xFF;
        
        ACCENT_GLOW = (0x66 << 24) | (baseR << 16) | (baseG << 8) | baseB;
        ACCENT_GLOW_SOFT = (0x1A << 24) | (baseR << 16) | (baseG << 8) | baseB;
        CAT_HOVER = (0x1A << 24) | (baseR << 16) | (baseG << 8) | baseB;
        CAT_ACTIVE_BG = (0x26 << 24) | (baseR << 16) | (baseG << 8) | baseB;
        FLASHLIGHT_CENTER = (0x22 << 24) | (baseR << 16) | (baseG << 8) | baseB;
        FLASHLIGHT_EDGE = (0x00 << 24) | (baseR << 16) | (baseG << 8) | baseB;
        PARTICLE_BASE = (0x33 << 24) | (baseR << 16) | (baseG << 8) | baseB;
        DROPDOWN_HOVER = (0x30 << 24) | (baseR << 16) | (baseG << 8) | baseB;
        DROPDOWN_SELECTED = (0x40 << 24) | (baseR << 16) | (baseG << 8) | baseB;
        HOVER_GLOW = (0x14 << 24) | (baseR << 16) | (baseG << 8) | baseB;

        SECONDARY = preset.secondary;
        SECONDARY_DIM = preset.secondaryDim;

        STAR_ACTIVE = preset.starActive;
        STAR_HOVER = preset.starHover;
        CARD_HOVER = preset.cardHover;
        HOVER_OUTLINE = preset.hoverOutline;

        ACTIVE_INDICATOR = ACCENT;
        TEXT_ENABLED = ACCENT;
        TEXT_VALUE = ACCENT;
        TEXT_CATEGORY_HEADER = SECONDARY;

        TOGGLE_ON_TRACK = ACCENT;
        TOGGLE_GLOW = ACCENT_GLOW;
        CHECKBOX_CHECKED_BG = ACCENT;
        SLIDER_FILL = ACCENT;
        SLIDER_VALUE_PILL_TEXT = ACCENT;
        COMBO_ACTIVE_BG = ACCENT;
        BADGE_BG = ACCENT;
    }

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
