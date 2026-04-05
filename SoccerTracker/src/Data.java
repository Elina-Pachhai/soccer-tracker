import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Central data layer: manages teams, players, and the betting balance.
 *
 * Persistence format (soccer_data.txt):
 *   BALANCE|1000
 *   TEAM|Manchester City|false|
 *   PLAYER|Erling Haaland|9|Forward|27|5|0|2|6
 *   ...
 *
 * On first launch (no save file) the database is seeded with real club and
 * national team rosters so the app is immediately usable and demoable.
 */
public class Data {

    private static final String DATA_FILE = "soccer_data.txt";
    private static final ArrayList<Team> teams = new ArrayList<>();
    private static int balance = 1000; // virtual betting coins

    // ─── Balance ─────────────────────────────────────────────────────────────

    public static int  getBalance()            { return balance; }
    public static void addToBalance(int delta) { balance += delta; save(); }

    // ─── Team Operations ─────────────────────────────────────────────────────

    public static ArrayList<Team> getTeams() { return teams; }

    /** Adds a new club team. Returns false if the name is already taken. */
    public static boolean addClubTeam(String name) {
        if (findTeam(name) != null) return false;
        teams.add(new Team(name));
        save();
        return true;
    }

    /** Adds a new national team. Returns false if the name is already taken. */
    public static boolean addNationalTeam(String name, String country) {
        if (findTeam(name) != null) return false;
        teams.add(new Team(name, country));
        save();
        return true;
    }

    /** Case-insensitive team lookup. */
    public static Team findTeam(String name) {
        for (Team t : teams)
            if (t.getName().equalsIgnoreCase(name)) return t;
        return null;
    }

    // ─── Player Operations ───────────────────────────────────────────────────

    /** Adds a player to a team. Returns false if the jersey number is already taken. */
    public static boolean addPlayerToTeam(Team team, Player player) {
        boolean added = team.addPlayer(player);
        if (added) save();
        return added;
    }

    /** All players across all teams sorted by goals descending. */
    public static ArrayList<Player> getAllPlayersSortedByGoals() {
        ArrayList<Player> all = allPlayers();
        all.sort(Comparator.comparingInt(Player::getGoals).reversed());
        return all;
    }

    /** All players across all teams sorted by performance score descending. */
    public static ArrayList<Player> getAllPlayersSortedByPerformance() {
        ArrayList<Player> all = allPlayers();
        all.sort(Comparator.comparingInt(Player::getPerformanceScore).reversed());
        return all;
    }

    private static ArrayList<Player> allPlayers() {
        ArrayList<Player> all = new ArrayList<>();
        for (Team t : teams) all.addAll(t.getPlayers());
        return all;
    }

    // ─── Persistence ─────────────────────────────────────────────────────────

    /** Saves all data to disk. */
    public static void save() {
        try (PrintWriter w = new PrintWriter(new FileWriter(DATA_FILE))) {
            w.println("BALANCE|" + balance);
            for (Team team : teams) {
                w.println(team.toFileString());
                for (Player p : team.getPlayers()) w.println(p.toFileString());
            }
        } catch (IOException e) {
            System.out.println("Warning: could not save — " + e.getMessage());
        }
    }

    /**
     * Loads data from disk. If no save file exists, seeds the database with
     * real team and player data so the app is immediately usable.
     *
     * @return true if an existing file was loaded.
     */
    public static boolean load() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            seedSampleData();
            return false;
        }
        try (BufferedReader r = new BufferedReader(new FileReader(file))) {
            teams.clear();
            String line;
            Team current = null;
            while ((line = r.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("BALANCE|")) {
                    try { balance = Integer.parseInt(line.split("\\|")[1]); }
                    catch (NumberFormatException ignored) {}
                } else if (line.startsWith("TEAM|")) {
                    current = Team.fromFileString(line);
                    teams.add(current);
                } else if (line.startsWith("PLAYER|") && current != null) {
                    current.addPlayer(Player.fromFileString(line));
                }
            }
            return true;
        } catch (IOException | NumberFormatException e) {
            System.out.println("Warning: could not load — " + e.getMessage());
            return false;
        }
    }

    // ─── Sample Data Seed ─────────────────────────────────────────────────────

    /**
     * Populates the database with real club and national team rosters.
     * Called automatically on first launch when no save file exists.
     *
     * Stats are based on the 2023-24 season (Premier League / Champions League /
     * international competitions). Players that appear for both a club and a
     * national team are stored as separate objects (different team contexts).
     */
    private static void seedSampleData() {

        // ── Manchester City ───────────────────────────────────────────────────
        Team mancity = new Team("Manchester City");
        addP(mancity, "Erling Haaland",     9,  "Forward",    27,  5, 0, 2, 6);
        addP(mancity, "Phil Foden",         47, "Forward",    19,  8, 0, 2, 0);
        addP(mancity, "Kevin De Bruyne",    17, "Midfielder",  2, 10, 0, 3, 0);
        addP(mancity, "Bernardo Silva",     20, "Midfielder",  8,  8, 0, 4, 0);
        addP(mancity, "Rodri",             16, "Midfielder",  8, 11, 0, 8, 1);
        teams.add(mancity);

        // ── Arsenal ───────────────────────────────────────────────────────────
        Team arsenal = new Team("Arsenal");
        addP(arsenal, "Bukayo Saka",        7,  "Forward",   16,  9, 0, 2, 0);
        addP(arsenal, "Leandro Trossard",  19, "Forward",   14,  7, 0, 2, 0);
        addP(arsenal, "Kai Havertz",       29, "Forward",   13,  8, 0, 3, 0);
        addP(arsenal, "Gabriel Martinelli",11, "Forward",   10,  9, 0, 3, 0);
        addP(arsenal, "Martin Odegaard",    8, "Midfielder",  8, 10, 0, 4, 0);
        teams.add(arsenal);

        // ── Liverpool ─────────────────────────────────────────────────────────
        Team liverpool = new Team("Liverpool");
        addP(liverpool, "Mohamed Salah",   11, "Forward",   18, 10, 0, 1, 0);
        addP(liverpool, "Luis Diaz",        7, "Forward",   13,  5, 0, 2, 0);
        addP(liverpool, "Darwin Nunez",     9, "Forward",   11,  5, 0, 4, 1);
        addP(liverpool, "Dominik Szoboszlai",8,"Midfielder",8,   6, 0, 5, 0);
        addP(liverpool, "Trent Alexander-Arnold",66,"Defender",4,9,0, 3, 0);
        teams.add(liverpool);

        // ── Real Madrid ───────────────────────────────────────────────────────
        Team realmadrid = new Team("Real Madrid");
        addP(realmadrid, "Vinicius Jr",    7,  "Forward",   24,  9, 0, 5, 1);
        addP(realmadrid, "Kylian Mbappe",  9,  "Forward",   33,  8, 0, 3, 0);
        addP(realmadrid, "Jude Bellingham",5,  "Midfielder",23, 13, 0, 6, 0);
        addP(realmadrid, "Rodrygo",       11,  "Forward",   14, 11, 0, 2, 0);
        addP(realmadrid, "Luka Modric",   10,  "Midfielder", 5,  8, 0, 4, 0);
        teams.add(realmadrid);

        // ── FC Barcelona ──────────────────────────────────────────────────────
        Team barca = new Team("FC Barcelona");
        addP(barca, "Robert Lewandowski",  9, "Forward",   26,  5, 0, 3, 0);
        addP(barca, "Raphinha",           11, "Forward",   27, 12, 0, 4, 0);
        addP(barca, "Pedri",               8, "Midfielder", 5, 10, 0, 6, 0);
        addP(barca, "Gavi",                6, "Midfielder", 3,  7, 0,10, 1);
        addP(barca, "Ferran Torres",       7, "Forward",   10,  6, 0, 2, 0);
        teams.add(barca);

        // ── Bayern Munich ─────────────────────────────────────────────────────
        Team bayern = new Team("Bayern Munich");
        addP(bayern, "Harry Kane",         9, "Forward",   36,  8, 0, 2, 0);
        addP(bayern, "Leroy Sane",        10, "Forward",   13, 10, 0, 3, 0);
        addP(bayern, "Jamal Musiala",     42, "Midfielder",12, 12, 0, 2, 0);
        addP(bayern, "Thomas Muller",     25, "Midfielder", 5, 11, 0, 3, 0);
        addP(bayern, "Serge Gnabry",      22, "Forward",    8,  7, 0, 2, 0);
        teams.add(bayern);

        // ── England (National) ────────────────────────────────────────────────
        Team england = new Team("England", "England");
        addP(england, "Harry Kane",        9, "Forward",   21, 14, 0, 2, 0);
        addP(england, "Bukayo Saka",       7, "Forward",   14, 12, 0, 1, 0);
        addP(england, "Jude Bellingham",  10, "Midfielder",13,  9, 0, 3, 0);
        addP(england, "Phil Foden",       11, "Forward",   10,  7, 0, 1, 0);
        addP(england, "Declan Rice",       4, "Midfielder", 4,  5, 0, 6, 0);
        teams.add(england);

        // ── Brazil (National) ─────────────────────────────────────────────────
        Team brazil = new Team("Brazil", "Brazil");
        addP(brazil, "Vinicius Jr",        7, "Forward",   14, 10, 0, 3, 0);
        addP(brazil, "Neymar",            10, "Forward",   18,  9, 0, 4, 0);
        addP(brazil, "Rodrygo",           11, "Forward",    7,  5, 0, 1, 0);
        addP(brazil, "Richarlison",        9, "Forward",   19,  5, 0, 3, 0);
        addP(brazil, "Bruno Guimaraes",   14, "Midfielder", 4,  6, 0, 4, 0);
        teams.add(brazil);

        // ── France (National) ─────────────────────────────────────────────────
        Team france = new Team("France", "France");
        addP(france, "Kylian Mbappe",     10, "Forward",   24, 15, 0, 2, 0);
        addP(france, "Antoine Griezmann",  7, "Forward",   12, 10, 0, 3, 0);
        addP(france, "Ousmane Dembele",   11, "Forward",    8,  9, 0, 4, 1);
        addP(france, "Olivier Giroud",     9, "Forward",    7,  3, 0, 2, 0);
        addP(france, "Aurelien Tchouameni",8,"Midfielder",  3,  4, 0, 5, 0);
        teams.add(france);

        // ── Argentina (National) ──────────────────────────────────────────────
        Team argentina = new Team("Argentina", "Argentina");
        addP(argentina, "Lionel Messi",   10, "Forward",   18, 16, 0, 2, 0);
        addP(argentina, "Julian Alvarez",  9, "Forward",   12,  8, 0, 2, 0);
        addP(argentina, "Angel Di Maria",  11,"Forward",    8,  9, 0, 3, 0);
        addP(argentina, "Rodrigo De Paul",  5,"Midfielder", 5,  8, 0, 6, 0);
        addP(argentina, "Enzo Fernandez",   8,"Midfielder", 4,  7, 0, 4, 0);
        teams.add(argentina);

        balance = 1000; // starting betting coins
        save();
    }

    /** Convenience: build a Player and add directly to a team (no save mid-seed). */
    private static void addP(Team team, String name, int jersey, String pos,
                              int goals, int assists, int pk, int yc, int rc) {
        Player p = new Player(name, jersey, pos);
        p.addGoals(goals);
        p.addAssists(assists);
        p.addPenaltyKicks(pk);
        p.addYellowCards(yc);
        p.addRedCards(rc);
        team.addPlayer(p);
    }
}
