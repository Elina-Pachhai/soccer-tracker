import java.util.ArrayList;

/**
 * Calculates match probabilities and simulates results based on player statistics.
 *
 * Team strength is derived from each player's weighted performance score.
 * Win/draw/loss probabilities are used to set betting odds and simulate scorelines.
 */
public class MatchPredictor {

    private static final double DRAW_PROB = 0.20; // 20% base draw probability

    // ─── Strength & Probability ──────────────────────────────────────────────

    /**
     * Returns a team's aggregate strength score (minimum 5.0 so odds are always finite).
     *
     * Goals heavily weighted because they directly win matches.
     */
    public static double teamStrength(Team team) {
        if (team.getPlayers().isEmpty()) return 5.0;
        double total = 0;
        for (Player p : team.getPlayers()) {
            total += p.getGoals()       * 4.0
                   + p.getAssists()     * 2.0
                   + p.getPenaltyKicks()
                   - p.getYellowCards() * 1.5
                   - p.getRedCards()    * 4.0;
        }
        return Math.max(total, 5.0);
    }

    /**
     * Returns a {p1_win, draw, p2_win} array where all three sum to 1.0.
     * The raw head-to-head probability is clamped to 10–90% before applying draw weight.
     */
    public static double[] probabilities(Team t1, Team t2) {
        double s1 = teamStrength(t1), s2 = teamStrength(t2);
        double raw = Math.min(0.90, Math.max(0.10, s1 / (s1 + s2)));
        double p1  = raw * (1.0 - DRAW_PROB);
        double p2  = (1.0 - raw) * (1.0 - DRAW_PROB);
        return new double[]{ p1, DRAW_PROB, p2 };
    }

    /** Decimal odds (1 / probability), rounded to 2 decimal places. */
    public static double odds(double probability) {
        return Math.round((1.0 / Math.max(0.01, probability)) * 100.0) / 100.0;
    }

    // ─── Match Simulation ────────────────────────────────────────────────────

    /**
     * Simulates a scoreline consistent with the teams' relative strength.
     *
     * @return int[]{goals_t1, goals_t2}
     */
    public static int[] simulateScore(Team t1, Team t2) {
        double[] p = probabilities(t1, t2);
        double rand = Math.random();

        // Determine outcome: 1 = t1 wins, 0 = draw, -1 = t2 wins
        int outcome = rand < p[0] ? 1 : rand < p[0] + p[1] ? 0 : -1;

        // Raw head-to-head probability (excluding draw)
        double winProb = p[0] / (p[0] + p[2]);

        // Generate Poisson-distributed goals based on relative strength
        int g1 = poisson(1.5 * winProb + 0.25);
        int g2 = poisson(1.5 * (1.0 - winProb) + 0.25);

        // Adjust raw goals to match the determined outcome
        if      (outcome == 1  && g1 <= g2) g1 = g2 + 1;
        else if (outcome == -1 && g2 <= g1) g2 = g1 + 1;
        else if (outcome == 0)              g2 = g1;

        return new int[]{ g1, g2 };
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    /** Returns the top n players from a team sorted by performance score (best first). */
    public static ArrayList<Player> topPlayers(Team team, int n) {
        ArrayList<Player> list = new ArrayList<>(team.getPlayers());
        list.sort((a, b) -> b.getPerformanceScore() - a.getPerformanceScore());
        return new ArrayList<>(list.subList(0, Math.min(n, list.size())));
    }

    /** Generates a Poisson-distributed random integer with mean lambda. */
    private static int poisson(double lambda) {
        double L = Math.exp(-Math.max(0.01, lambda));
        int k = 0;
        double p = 1.0;
        do { k++; p *= Math.random(); } while (p > L);
        return k - 1;
    }
}
