/**
 * Represents a soccer player with statistics tracking.
 *
 * Performance Score Formula:
 *   Goals x3 + Assists x2 + Penalty Kicks x1 - Yellow Cards x2 - Red Cards x5
 */
public class Player {
    private String name;
    private int jerseyNumber;
    private String position;
    private int goals;
    private int assists;
    private int redCards;
    private int yellowCards;
    private int penaltyKicks;

    /**
     * Creates a new player. All statistics start at zero.
     */
    public Player(String name, int jerseyNumber, String position) {
        this.name = name;
        this.jerseyNumber = jerseyNumber;
        this.position = position;
    }

    // ─── Getters & Setters ───────────────────────────────────────────────────

    public String getName()              { return name; }
    public void setName(String name)     { this.name = name; }

    public int getJerseyNumber()                   { return jerseyNumber; }
    public void setJerseyNumber(int jerseyNumber)  { this.jerseyNumber = jerseyNumber; }

    public String getPosition()              { return position; }
    public void setPosition(String position) { this.position = position; }

    public int getGoals()             { return goals; }
    public void addGoals(int amount)  { this.goals += amount; }

    public int getAssists()             { return assists; }
    public void addAssists(int amount)  { this.assists += amount; }

    public int getRedCards()             { return redCards; }
    public void addRedCards(int amount)  { this.redCards += amount; }

    public int getYellowCards()             { return yellowCards; }
    public void addYellowCards(int amount)  { this.yellowCards += amount; }

    public int getPenaltyKicks()             { return penaltyKicks; }
    public void addPenaltyKicks(int amount)  { this.penaltyKicks += amount; }

    // ─── Computed ────────────────────────────────────────────────────────────

    /**
     * Returns the weighted performance score for this player.
     */
    public int getPerformanceScore() {
        return (goals * 3) + (assists * 2) + penaltyKicks
                - (yellowCards * 2) - (redCards * 5);
    }

    @Override
    public String toString() {
        return String.format("#%-3d %-22s (%s)", jerseyNumber, name, position);
    }

    // ─── Persistence ─────────────────────────────────────────────────────────

    /**
     * Serializes this player to a pipe-delimited line for file storage.
     */
    public String toFileString() {
        return String.join("|", "PLAYER", name, String.valueOf(jerseyNumber), position,
                String.valueOf(goals), String.valueOf(assists),
                String.valueOf(redCards), String.valueOf(yellowCards),
                String.valueOf(penaltyKicks));
    }

    /**
     * Deserializes a player from a pipe-delimited file line produced by toFileString().
     */
    public static Player fromFileString(String line) {
        String[] p = line.split("\\|");
        // p[0]="PLAYER", p[1]=name, p[2]=jersey, p[3]=position,
        // p[4]=goals, p[5]=assists, p[6]=redCards, p[7]=yellowCards, p[8]=penaltyKicks
        Player player = new Player(p[1], Integer.parseInt(p[2]), p[3]);
        player.goals        = Integer.parseInt(p[4]);
        player.assists      = Integer.parseInt(p[5]);
        player.redCards     = Integer.parseInt(p[6]);
        player.yellowCards  = Integer.parseInt(p[7]);
        player.penaltyKicks = Integer.parseInt(p[8]);
        return player;
    }
}
