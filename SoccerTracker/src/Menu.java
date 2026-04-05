import java.util.ArrayList;
import java.util.Scanner;

/**
 * Handles all user interaction for the Soccer Tracker application.
 *
 * Visual design uses ANSI colors and box-drawing characters.
 * The menu is grouped into four categories: Teams, Players, Statistics, Reports.
 */
public class Menu {

    private static final Scanner scanner = new Scanner(System.in);

    // ─── Banner ──────────────────────────────────────────────────────────────
    //
    // Box is 50 terminal columns wide (48-col interior + 2 border chars).
    // ⚽ emoji = 2 terminal cols, box/line chars = 1 terminal col each.
    //
    private static final String BANNER =
        Colors.BRIGHT_GREEN + Colors.BOLD
        + "╔════════════════════════════════════════════════╗\n"
        + "║                                                ║\n"
        + "║    ⚽   S O C C E R   T R A C K E R   ⚽     ║\n"
        + "║       Track Teams · Players · Statistics       ║\n"
        + "║                                                ║\n"
        + "╚════════════════════════════════════════════════╝\n"
        + Colors.RESET;

    // ─── Main Loop ───────────────────────────────────────────────────────────

    /** Entry point — loads saved data then runs the menu until the user exits. */
    public static void menuLoop() {
        clearScreen();
        System.out.println(BANNER);

        System.out.print(Colors.DIM + "  Loading saved data..." + Colors.RESET);
        boolean loaded = Data.load();
        if (loaded) System.out.println(Colors.brightGreen("  done."));
        else        System.out.println(Colors.dim("  no save file found. Starting fresh."));

        int option = -1;
        while (option != 0) {
            System.out.println();
            printMenu();
            option = readInt(Colors.BRIGHT_WHITE + "  ► " + Colors.RESET);

            if (option == 0) break;

            if (option < 1 || option > 12) {
                System.out.println(Colors.brightRed(
                        "  Invalid option '" + option + "'. Choose 0–12."));
                pause();
                continue;
            }

            clearScreen();
            System.out.println(BANNER);
            printSectionTitle(optionLabel(option));

            switch (option) {
                case 1  -> menuAddClubTeam();
                case 2  -> menuAddNationalTeam();
                case 3  -> menuAddPlayerToTeam();
                case 4  -> menuUpdatePlayerInfo();
                case 5  -> menuRecordAssists();
                case 6  -> menuRecordGoals();
                case 7  -> menuRecordRedCards();
                case 8  -> menuRecordYellowCards();
                case 9  -> menuRecordPenaltyKicks();
                case 10 -> menuDisplayAllPlayers();
                case 11 -> menuTop3GoalScorers();
                case 12 -> menuRankByPerformance();
            }
            pause();
            clearScreen();
            System.out.println(BANNER);
        }

        System.out.println();
        System.out.println(Colors.brightGreen("  Thanks for using Soccer Tracker! Goodbye! ⚽"));
        System.out.println();
    }

    // ─── Menu Display ────────────────────────────────────────────────────────

    private static void printMenu() {
        // Box width = 50 terminal cols, interior = 48
        // ┌─ LABEL ──────────────────────────────────────┐
        // │   [N]  option text                           │
        // └──────────────────────────────────────────────┘

        menuCategory("TEAMS", new int[]{1, 2}, new String[]{
            "Add a Club Team",
            "Add a National Team"
        });
        menuCategory("PLAYERS", new int[]{3, 4}, new String[]{
            "Add a Player to a Team",
            "Update Player Information"
        });
        menuCategory("STATISTICS", new int[]{5, 6, 7, 8, 9}, new String[]{
            "Record Assists           " + Colors.dim("(+2 pts each)"),
            "Record Goals             " + Colors.dim("(+3 pts each)"),
            "Record Red Cards         " + Colors.dim("(\u001B[31m-5 pts\u001B[0m\u001B[2m each\u001B[0m)"),
            "Record Yellow Cards      " + Colors.dim("(\u001B[33m-2 pts\u001B[0m\u001B[2m each\u001B[0m)"),
            "Record Penalty Kicks     " + Colors.dim("(+1 pt  each)")
        });
        menuCategory("REPORTS", new int[]{10, 11, 12}, new String[]{
            "Display All Players & Jersey Numbers",
            "Display Top 3 Goal Scorers",
            "Rank Players by Performance Score"
        });

        System.out.println("  " + Colors.dim("──────────────────────────────────────────────────"));
        System.out.println("     " + Colors.bold("[0]") + "  Exit Program");
        System.out.println();
    }

    private static void menuCategory(String label, int[] nums, String[] labels) {
        // Top border with label
        String header = "─ " + label + " ";
        int dashes = 48 - header.length();
        System.out.println("  " + Colors.CYAN + Colors.BOLD
                + "┌" + header + "─".repeat(dashes) + "┐" + Colors.RESET);

        for (int i = 0; i < nums.length; i++) {
            String numTag = Colors.BOLD + Colors.BRIGHT_WHITE
                    + String.format("[%2d]", nums[i]) + Colors.RESET;
            System.out.println("  " + Colors.CYAN + "│" + Colors.RESET
                    + "   " + numTag + "  " + labels[i]);
        }

        System.out.println("  " + Colors.CYAN + Colors.BOLD
                + "└" + "─".repeat(48) + "┘" + Colors.RESET);
    }

    private static void printSectionTitle(String title) {
        System.out.println("  " + Colors.BOLD + Colors.BRIGHT_CYAN
                + "┌─ " + title + " " + "─".repeat(Math.max(0, 44 - title.length()))
                + "┐" + Colors.RESET);
        System.out.println();
    }

    private static String optionLabel(int n) {
        return switch (n) {
            case 1  -> "ADD CLUB TEAM";
            case 2  -> "ADD NATIONAL TEAM";
            case 3  -> "ADD PLAYER";
            case 4  -> "UPDATE PLAYER";
            case 5  -> "RECORD ASSISTS";
            case 6  -> "RECORD GOALS";
            case 7  -> "RECORD RED CARDS";
            case 8  -> "RECORD YELLOW CARDS";
            case 9  -> "RECORD PENALTY KICKS";
            case 10 -> "ALL PLAYERS & JERSEY NUMBERS";
            case 11 -> "TOP 3 GOAL SCORERS";
            case 12 -> "PLAYER PERFORMANCE RANKINGS";
            default -> "";
        };
    }

    // ─── Input Helpers ───────────────────────────────────────────────────────

    /** Reads an integer, re-prompting on invalid input so the app never crashes. */
    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println(Colors.red("  Please enter a valid number."));
            }
        }
    }

    /** Reads a non-empty string, re-prompting if the user enters nothing. */
    private static String readString(String prompt) {
        while (true) {
            System.out.print("  " + Colors.BRIGHT_WHITE + prompt + Colors.RESET + " ");
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.out.println(Colors.red("  Input cannot be empty. Try again."));
        }
    }

    /** Prints a success message in bright green. */
    private static void success(String msg) {
        System.out.println("  " + Colors.BRIGHT_GREEN + "✔  " + msg + Colors.RESET);
    }

    /** Prints an error/warning message in bright red. */
    private static void error(String msg) {
        System.out.println("  " + Colors.BRIGHT_RED + "✘  " + msg + Colors.RESET);
    }

    /** Waits for the user to press Enter before continuing. */
    private static void pause() {
        System.out.println();
        System.out.print(Colors.dim("  Press Enter to return to menu..."));
        scanner.nextLine();
    }

    /** Clears the terminal screen. Works on macOS/Linux. */
    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /**
     * Shows a numbered list of teams and returns the user's selection.
     *
     * @return the selected Team, or null if no teams exist or selection is invalid.
     */
    private static Team selectTeam(String prompt) {
        ArrayList<Team> teams = Data.getTeams();
        if (teams.isEmpty()) {
            error("No teams registered yet. Add a team first.");
            return null;
        }
        System.out.println("  " + Colors.bold(prompt));
        for (int i = 0; i < teams.size(); i++) {
            System.out.printf("  " + Colors.BRIGHT_WHITE + "  [%d]" + Colors.RESET + "  %s%n",
                    i + 1, teams.get(i));
        }
        int choice = readInt("  Team number: ");
        if (choice < 1 || choice > teams.size()) {
            error("Invalid selection.");
            return null;
        }
        return teams.get(choice - 1);
    }

    /**
     * Shows a numbered list of the team's players and returns the user's selection.
     *
     * @return the selected Player, or null if no players exist or selection is invalid.
     */
    private static Player selectPlayer(Team team, String prompt) {
        ArrayList<Player> players = team.getPlayers();
        if (players.isEmpty()) {
            error("'" + team.getName() + "' has no players yet.");
            return null;
        }
        System.out.println("  " + Colors.bold(prompt));
        for (int i = 0; i < players.size(); i++) {
            System.out.printf("  " + Colors.BRIGHT_WHITE + "  [%d]" + Colors.RESET + "  %s%n",
                    i + 1, players.get(i));
        }
        int choice = readInt("  Player number: ");
        if (choice < 1 || choice > players.size()) {
            error("Invalid selection.");
            return null;
        }
        return players.get(choice - 1);
    }

    // ─── Menu Actions ────────────────────────────────────────────────────────

    private static void menuAddClubTeam() {
        String name = readString("Club team name:");
        if (Data.addClubTeam(name)) success("Club team '" + name + "' added!");
        else error("A team named '" + name + "' already exists.");
    }

    private static void menuAddNationalTeam() {
        String name    = readString("National team name:");
        String country = readString("Country:");
        if (Data.addNationalTeam(name, country))
            success("National team '" + name + "' (" + country + ") added!");
        else error("A team named '" + name + "' already exists.");
    }

    private static void menuAddPlayerToTeam() {
        Team team = selectTeam("Select a team:");
        if (team == null) return;

        System.out.println();
        System.out.println("  Adding player to: " + Colors.bold(team.getName()));
        String name     = readString("Player name:");
        int    jersey   = readInt("  Jersey number: ");
        String position = readString("Position (e.g. Forward, Midfielder, Defender, Goalkeeper):");

        Player player = new Player(name, jersey, position);
        if (Data.addPlayerToTeam(team, player))
            success("'" + name + "' (#" + jersey + ") added to " + team.getName() + "!");
        else
            error("Jersey #" + jersey + " is already taken on '" + team.getName() + "'.");
    }

    private static void menuUpdatePlayerInfo() {
        Team team = selectTeam("Select the team:");
        if (team == null) return;
        Player player = selectPlayer(team, "Select the player to update:");
        if (player == null) return;

        System.out.println();
        System.out.println("  Updating: " + Colors.bold(player.toString())
                + Colors.dim("  (leave blank to keep current value)"));
        System.out.println();

        System.out.print("  Name [" + Colors.CYAN + player.getName() + Colors.RESET + "]: ");
        String name = scanner.nextLine().trim();
        if (!name.isEmpty()) player.setName(name);

        System.out.print("  Jersey # [" + Colors.CYAN + player.getJerseyNumber() + Colors.RESET + "]: ");
        String jerseyStr = scanner.nextLine().trim();
        if (!jerseyStr.isEmpty()) {
            try {
                int j = Integer.parseInt(jerseyStr);
                boolean taken = team.getPlayers().stream()
                        .anyMatch(p -> p != player && p.getJerseyNumber() == j);
                if (taken) error("Jersey #" + j + " is already taken — keeping current.");
                else player.setJerseyNumber(j);
            } catch (NumberFormatException e) {
                error("Invalid number — keeping current.");
            }
        }

        System.out.print("  Position [" + Colors.CYAN + player.getPosition() + Colors.RESET + "]: ");
        String position = scanner.nextLine().trim();
        if (!position.isEmpty()) player.setPosition(position);

        Data.save();
        success("'" + player.getName() + "' updated successfully.");
    }

    private static void menuRecordAssists() {
        Team team = selectTeam("Select team:");
        if (team == null) return;
        Player player = selectPlayer(team, "Select player:");
        if (player == null) return;
        int amount = readInt("  Assists to add (current: "
                + Colors.BRIGHT_CYAN + player.getAssists() + Colors.RESET + "): ");
        if (amount < 0) { error("Cannot record a negative amount."); return; }
        player.addAssists(amount);
        Data.save();
        success(player.getName() + " now has " + Colors.brightCyan(player.getAssists() + " assist(s)."));
    }

    private static void menuRecordGoals() {
        Team team = selectTeam("Select team:");
        if (team == null) return;
        Player player = selectPlayer(team, "Select player:");
        if (player == null) return;
        int amount = readInt("  Goals to add (current: "
                + Colors.BRIGHT_CYAN + player.getGoals() + Colors.RESET + "): ");
        if (amount < 0) { error("Cannot record a negative amount."); return; }
        player.addGoals(amount);
        Data.save();
        success(player.getName() + " now has " + Colors.brightCyan(player.getGoals() + " goal(s). ⚽"));
    }

    private static void menuRecordRedCards() {
        Team team = selectTeam("Select team:");
        if (team == null) return;
        Player player = selectPlayer(team, "Select player:");
        if (player == null) return;
        int amount = readInt("  Red cards to add (current: "
                + Colors.RED + player.getRedCards() + Colors.RESET + "): ");
        if (amount < 0) { error("Cannot record a negative amount."); return; }
        player.addRedCards(amount);
        Data.save();
        success(player.getName() + " now has " + Colors.red(player.getRedCards() + " red card(s). 🟥"));
    }

    private static void menuRecordYellowCards() {
        Team team = selectTeam("Select team:");
        if (team == null) return;
        Player player = selectPlayer(team, "Select player:");
        if (player == null) return;
        int amount = readInt("  Yellow cards to add (current: "
                + Colors.YELLOW + player.getYellowCards() + Colors.RESET + "): ");
        if (amount < 0) { error("Cannot record a negative amount."); return; }
        player.addYellowCards(amount);
        Data.save();
        success(player.getName() + " now has " + Colors.yellow(player.getYellowCards() + " yellow card(s). 🟨"));
    }

    private static void menuRecordPenaltyKicks() {
        Team team = selectTeam("Select team:");
        if (team == null) return;
        Player player = selectPlayer(team, "Select player:");
        if (player == null) return;
        int amount = readInt("  Penalty kicks to add (current: "
                + Colors.BRIGHT_CYAN + player.getPenaltyKicks() + Colors.RESET + "): ");
        if (amount < 0) { error("Cannot record a negative amount."); return; }
        player.addPenaltyKicks(amount);
        Data.save();
        success(player.getName() + " now has " + Colors.brightCyan(player.getPenaltyKicks() + " penalty kick(s)."));
    }

    private static void menuDisplayAllPlayers() {
        ArrayList<Team> teams = Data.getTeams();
        if (teams.isEmpty()) {
            error("No teams registered yet.");
            return;
        }

        for (Team team : teams) {
            System.out.println();
            // Team header box
            String teamLabel = "  " + team.getName() + "  ";
            System.out.println(Colors.BOLD + Colors.BRIGHT_CYAN
                    + "  ╔═" + teamLabel + "═".repeat(Math.max(0, 62 - teamLabel.length())) + "╗"
                    + Colors.RESET);
            System.out.println(Colors.BOLD + Colors.BRIGHT_CYAN
                    + "  ║  " + Colors.RESET
                    + Colors.dim(team.getTypeLabel())
                    + (team.isNationalTeam() ? Colors.dim(" — " + team.getCountry()) : "")
                    + Colors.dim("  ·  " + team.getPlayers().size() + " player(s)")
                    + Colors.BOLD + Colors.BRIGHT_CYAN
                    + "  ".repeat(3) + "║"
                    + Colors.RESET);

            ArrayList<Player> players = team.getPlayers();
            if (players.isEmpty()) {
                System.out.println(Colors.BOLD + Colors.BRIGHT_CYAN
                        + "  ║  " + Colors.RESET
                        + Colors.dim("  (no players yet)")
                        + Colors.BOLD + Colors.BRIGHT_CYAN + "  ║" + Colors.RESET);
            } else {
                // Sort by jersey number
                players.sort((a, b) -> Integer.compare(a.getJerseyNumber(), b.getJerseyNumber()));

                // Column header
                System.out.println(Colors.BRIGHT_CYAN + "  ╠" + "═".repeat(64) + "╣" + Colors.RESET);
                System.out.printf(Colors.BRIGHT_CYAN + "  ║" + Colors.RESET
                        + Colors.dim("  %-22s  %-4s  %-13s  %5s  %5s  %4s  %4s  %4s  Score")
                        + Colors.BRIGHT_CYAN + "  ║%n" + Colors.RESET,
                        "Name", "#", "Position", "Goals", "Assts", "PK", "YC", "RC");
                System.out.println(Colors.BRIGHT_CYAN + "  ╠" + "═".repeat(64) + "╣" + Colors.RESET);

                for (Player p : players) {
                    System.out.printf(Colors.BRIGHT_CYAN + "  ║" + Colors.RESET
                            + "  %-22s  " + Colors.BOLD + "#%-3d" + Colors.RESET
                            + "  %-13s  "
                            + Colors.BRIGHT_CYAN + "%5d" + Colors.RESET + "  "
                            + Colors.CYAN + "%5d" + Colors.RESET + "  "
                            + Colors.DIM + "%4d" + Colors.RESET + "  "
                            + Colors.YELLOW + "%4d" + Colors.RESET + "  "
                            + Colors.RED + "%4d" + Colors.RESET + "  %s"
                            + Colors.BRIGHT_CYAN + "  ║%n" + Colors.RESET,
                            p.getName(), p.getJerseyNumber(), p.getPosition(),
                            p.getGoals(), p.getAssists(), p.getPenaltyKicks(),
                            p.getYellowCards(), p.getRedCards(),
                            Colors.coloredScore(p.getPerformanceScore()));
                }
            }
            System.out.println(Colors.BOLD + Colors.BRIGHT_CYAN
                    + "  ╚" + "═".repeat(64) + "╝" + Colors.RESET);
        }
    }

    private static void menuTop3GoalScorers() {
        ArrayList<Player> sorted = Data.getAllPlayersSortedByGoals();
        if (sorted.isEmpty()) { error("No players registered yet."); return; }

        String[] medals = {"🥇", "🥈", "🥉"};
        int limit = Math.min(3, sorted.size());

        System.out.println();
        System.out.println("  " + Colors.BOLD + Colors.BRIGHT_YELLOW
                + "🏆  Top " + limit + " Goal Scorer" + (limit > 1 ? "s" : "")
                + Colors.RESET);
        System.out.println("  " + Colors.DIM + "─".repeat(44) + Colors.RESET);
        System.out.println();

        for (int i = 0; i < limit; i++) {
            Player p = sorted.get(i);
            System.out.printf("     %s  %-24s  "
                    + Colors.BRIGHT_CYAN + "%d" + Colors.RESET
                    + " goal%s%n",
                    medals[i], Colors.bold(p.getName()),
                    p.getGoals(), p.getGoals() == 1 ? "" : "s");
        }
    }

    private static void menuRankByPerformance() {
        ArrayList<Player> sorted = Data.getAllPlayersSortedByPerformance();
        if (sorted.isEmpty()) { error("No players registered yet."); return; }

        System.out.println();
        System.out.println("  " + Colors.bold("Performance Score")
                + Colors.dim(" = Goals×3 + Assists×2 + PK×1 − YellowCards×2 − RedCards×5"));
        System.out.println("  " + Colors.DIM + "─".repeat(72) + Colors.RESET);
        System.out.printf(Colors.DIM + "  %-4s  %-24s  %5s  %5s  %4s  %4s  %4s  Score%n" + Colors.RESET,
                "Rank", "Name", "Goals", "Assts", "PK", "YC", "RC");
        System.out.println("  " + Colors.DIM + "─".repeat(72) + Colors.RESET);

        for (int i = 0; i < sorted.size(); i++) {
            Player p = sorted.get(i);
            // Highlight rank 1–3 with bold, rest normal
            String rankStr = i < 3
                    ? Colors.BOLD + Colors.BRIGHT_YELLOW + String.format("#%-3d", i + 1) + Colors.RESET
                    : Colors.DIM + String.format("#%-3d", i + 1) + Colors.RESET;

            System.out.printf("  %s  %-24s  "
                    + Colors.BRIGHT_CYAN + "%5d" + Colors.RESET + "  "
                    + Colors.CYAN       + "%5d" + Colors.RESET + "  "
                    + Colors.DIM        + "%4d" + Colors.RESET + "  "
                    + Colors.YELLOW     + "%4d" + Colors.RESET + "  "
                    + Colors.RED        + "%4d" + Colors.RESET + "  %s%n",
                    rankStr, p.getName(),
                    p.getGoals(), p.getAssists(), p.getPenaltyKicks(),
                    p.getYellowCards(), p.getRedCards(),
                    Colors.coloredScore(p.getPerformanceScore()));
        }
    }
}
