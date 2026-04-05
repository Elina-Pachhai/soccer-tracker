import java.util.ArrayList;

/**
 * Represents a soccer team (club or national) that manages a roster of players.
 */
public class Team {
    private String name;
    private boolean isNationalTeam;
    private String country;
    private ArrayList<Player> players;

    /**
     * Creates a new club team.
     */
    public Team(String name) {
        this(name, false, "");
    }

    /**
     * Creates a new national team with the given country.
     */
    public Team(String name, String country) {
        this(name, true, country);
    }

    // Internal constructor used by fromFileString
    private Team(String name, boolean isNationalTeam, String country) {
        this.name = name;
        this.isNationalTeam = isNationalTeam;
        this.country = country;
        this.players = new ArrayList<>();
    }

    // ─── Getters ─────────────────────────────────────────────────────────────

    public String getName()                 { return name; }
    public boolean isNationalTeam()         { return isNationalTeam; }
    public String getCountry()              { return country; }
    public ArrayList<Player> getPlayers()   { return players; }

    public String getTypeLabel() {
        return isNationalTeam ? "National Team" : "Club Team";
    }

    // ─── Player Management ───────────────────────────────────────────────────

    /**
     * Adds a player to this team.
     *
     * @return false if the jersey number is already taken on this team.
     */
    public boolean addPlayer(Player player) {
        for (Player p : players) {
            if (p.getJerseyNumber() == player.getJerseyNumber()) {
                return false;
            }
        }
        players.add(player);
        return true;
    }

    /**
     * Finds a player by name (case-insensitive).
     *
     * @return the matching Player, or null if not found.
     */
    public Player findPlayerByName(String name) {
        for (Player p : players) {
            if (p.getName().equalsIgnoreCase(name)) return p;
        }
        return null;
    }

    @Override
    public String toString() {
        if (isNationalTeam) {
            return String.format("%s [National — %s] (%d player%s)",
                    name, country, players.size(), players.size() == 1 ? "" : "s");
        }
        return String.format("%s [Club] (%d player%s)",
                name, players.size(), players.size() == 1 ? "" : "s");
    }

    // ─── Persistence ─────────────────────────────────────────────────────────

    /**
     * Serializes the team header to a pipe-delimited line for file storage.
     * Players are written separately by Data.save().
     */
    public String toFileString() {
        return String.join("|", "TEAM", name, String.valueOf(isNationalTeam), country);
    }

    /**
     * Deserializes a team from a pipe-delimited file line produced by toFileString().
     * The returned team has no players; load them separately.
     */
    public static Team fromFileString(String line) {
        String[] p = line.split("\\|");
        // p[0]="TEAM", p[1]=name, p[2]=isNational, p[3]=country (may be empty)
        boolean national = Boolean.parseBoolean(p[2]);
        String country   = p.length > 3 ? p[3] : "";
        return new Team(p[1], national, country);
    }
}
