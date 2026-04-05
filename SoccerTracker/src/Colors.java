/**
 * ANSI escape code constants for terminal color output.
 *
 * Usage example:
 *   System.out.println(Colors.GREEN + "Hello!" + Colors.RESET);
 *   System.out.println(Colors.green("Hello!"));   // same, via helper
 */
public class Colors {

    // ─── Reset ───────────────────────────────────────────────────────────────
    public static final String RESET  = "\u001B[0m";

    // ─── Text Styles ─────────────────────────────────────────────────────────
    public static final String BOLD   = "\u001B[1m";
    public static final String DIM    = "\u001B[2m";

    // ─── Foreground Colors ───────────────────────────────────────────────────
    public static final String RED    = "\u001B[31m";
    public static final String GREEN  = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE   = "\u001B[34m";
    public static final String CYAN   = "\u001B[36m";
    public static final String WHITE  = "\u001B[37m";

    // ─── Bright Foreground Colors ─────────────────────────────────────────────
    public static final String BRIGHT_RED    = "\u001B[91m";
    public static final String BRIGHT_GREEN  = "\u001B[92m";
    public static final String BRIGHT_YELLOW = "\u001B[93m";
    public static final String BRIGHT_BLUE   = "\u001B[94m";
    public static final String BRIGHT_CYAN   = "\u001B[96m";
    public static final String BRIGHT_WHITE  = "\u001B[97m";

    // ─── Convenience Wrappers ────────────────────────────────────────────────

    public static String bold(String s)        { return BOLD   + s + RESET; }
    public static String dim(String s)         { return DIM    + s + RESET; }
    public static String red(String s)         { return RED    + s + RESET; }
    public static String green(String s)       { return GREEN  + s + RESET; }
    public static String yellow(String s)      { return YELLOW + s + RESET; }
    public static String cyan(String s)        { return CYAN   + s + RESET; }
    public static String brightRed(String s)   { return BRIGHT_RED    + s + RESET; }
    public static String brightGreen(String s) { return BRIGHT_GREEN  + s + RESET; }
    public static String brightCyan(String s)  { return BRIGHT_CYAN   + s + RESET; }
    public static String brightWhite(String s) { return BRIGHT_WHITE  + s + RESET; }

    /**
     * Returns a score string colored green (positive), red (negative), or white (zero).
     */
    public static String coloredScore(int score) {
        if (score > 0) return BRIGHT_GREEN + score + RESET;
        if (score < 0) return BRIGHT_RED   + score + RESET;
        return WHITE + "0" + RESET;
    }
}
