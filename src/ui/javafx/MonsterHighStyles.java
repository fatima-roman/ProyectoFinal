package ui.javafx;

/**
 * This class holds all the CSS styles used in the app.
 * Instead of writing the same style in every file, we define them
 * here once and reuse them everywhere.
 *
 * Colors follow the Monster High brand:
 *   - Hot pink  : #E91E8C
 *   - Dark purple: #4A0E6E
 *   - Lime green : #8CC63F
 *   - Almost black background: #0D0D0D
 *
 * @author Fatima Roman
 * @version 1.0
 */
public final class MonsterHighStyles {

    // This class should never be instantiated
    private MonsterHighStyles() {}

    // ── Brand colors ─────────────────────────────────────────────────────────
    public static final String PINK   = "#E91E8C";
    public static final String PURPLE = "#4A0E6E";
    public static final String GREEN  = "#8CC63F";
    public static final String RED    = "#E53935";

    // ── Background ───────────────────────────────────────────────────────────
    /** Main window background (almost black). */
    public static final String SCENE_BG = "-fx-background-color: #0D0D0D;";

    // ── Text styles ──────────────────────────────────────────────────────────
    /** Big page title — pink and bold. */
    public static final String TITLE =
        "-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + PINK + ";";

    /** Section heading — white and bold. */
    public static final String HEADING =
        "-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #F0F0F0;";

    /** Normal body text. */
    public static final String BODY = "-fx-font-size: 14px; -fx-text-fill: #F0F0F0;";

    /** Small grey text (hints, version label, etc.). */
    public static final String MUTED = "-fx-font-size: 12px; -fx-text-fill: #AAAAAA;";

    // ── Buttons ───────────────────────────────────────────────────────────────
    /** Main action button — pink. */
    public static final String BTN_PRIMARY =
        "-fx-background-color: " + PINK + "; -fx-text-fill: white; " +
        "-fx-font-size: 13px; -fx-font-weight: bold; " +
        "-fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8 18;";

    /** Secondary button — purple. */
    public static final String BTN_SECONDARY =
        "-fx-background-color: " + PURPLE + "; -fx-text-fill: white; " +
        "-fx-font-size: 12px; -fx-background-radius: 8; " +
        "-fx-cursor: hand; -fx-padding: 6 14;";

    /** Delete / danger button — red. */
    public static final String BTN_DANGER =
        "-fx-background-color: " + RED + "; -fx-text-fill: white; " +
        "-fx-font-size: 12px; -fx-background-radius: 8; " +
        "-fx-cursor: hand; -fx-padding: 6 14;";

    /** Ghost button — transparent with pink border. */
    public static final String BTN_GHOST =
        "-fx-background-color: transparent; -fx-border-color: " + PINK + "; " +
        "-fx-border-radius: 8; -fx-border-width: 1.5; " +
        "-fx-text-fill: " + PINK + "; -fx-font-size: 12px; " +
        "-fx-cursor: hand; -fx-padding: 6 14;";

    // ── Sidebar navigation buttons ────────────────────────────────────────────
    /** Sidebar button — not selected. */
    public static final String NAV_BTN =
        "-fx-background-color: transparent; -fx-text-fill: #AAAAAA; " +
        "-fx-font-size: 13px; -fx-cursor: hand; " +
        "-fx-alignment: CENTER_LEFT; -fx-padding: 10 20; -fx-background-radius: 8;";

    /** Sidebar button — currently selected (active). */
    public static final String NAV_BTN_ACTIVE =
        "-fx-background-color: " + PURPLE + "; -fx-text-fill: " + PINK + "; " +
        "-fx-font-size: 13px; -fx-font-weight: bold; -fx-cursor: hand; " +
        "-fx-alignment: CENTER_LEFT; -fx-padding: 10 20; -fx-background-radius: 8;";

    // ── Form controls ─────────────────────────────────────────────────────────
    /** Dark text field. */
    public static final String TEXT_FIELD =
        "-fx-background-color: #2B2B2B; -fx-text-fill: #F0F0F0; " +
        "-fx-border-color: #444; -fx-border-radius: 6; " +
        "-fx-background-radius: 6; -fx-padding: 6 10; -fx-font-size: 13px;";

    /** Dark combo box / date picker. */
    public static final String COMBO =
        "-fx-background-color: #2B2B2B; -fx-text-fill: #F0F0F0; " +
        "-fx-border-color: #444; -fx-border-radius: 6; -fx-background-radius: 6;";

    // ── Cards ─────────────────────────────────────────────────────────────────
    /** Dark elevated card used in the dashboard. */
    public static final String CARD =
        "-fx-background-color: #1E1E2E; -fx-background-radius: 12;";

    // ── Table ─────────────────────────────────────────────────────────────────
    /** TableView dark theme. */
    public static final String TABLE =
        "-fx-background-color: #1E1E2E; -fx-text-fill: #F0F0F0; -fx-border-color: #333;";

    // ── Divider ───────────────────────────────────────────────────────────────
    /** Thin pink horizontal line used as a separator. */
    public static final String DIVIDER =
        "-fx-background-color: " + PINK + "; -fx-pref-height: 2; -fx-opacity: 0.6;";

    // ── Grade badges ──────────────────────────────────────────────────────────
    /** Green badge — grade >= 5 (passed). */
    public static final String BADGE_OK =
        "-fx-background-color: " + GREEN + "; -fx-text-fill: black; " +
        "-fx-background-radius: 12; -fx-padding: 2 8; -fx-font-size: 11px;";

    /** Red badge — grade < 5 (failed). */
    public static final String BADGE_FAIL =
        "-fx-background-color: " + RED + "; -fx-text-fill: white; " +
        "-fx-background-radius: 12; -fx-padding: 2 8; -fx-font-size: 11px;";
}
