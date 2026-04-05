import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Graphical user interface for Soccer Tracker.
 *
 * Sections (sidebar navigation):
 *   Teams · Players · Statistics · Rankings · Match Predictor
 *
 * Run with:  javac *.java && java SoccerTrackerGUI
 */
public class SoccerTrackerGUI extends JFrame {

    // ─── Colour Palette ──────────────────────────────────────────────────────
    static final Color BG        = new Color(18,  26,  21);
    static final Color SIDEBAR   = new Color(12,  40,  22);
    static final Color PANEL     = new Color(24,  36,  28);
    static final Color ROW_A     = new Color(28,  42,  33);
    static final Color ROW_B     = new Color(23,  35,  27);
    static final Color HEADER_BG = new Color(10,  55,  28);
    static final Color BORDER    = new Color(40,  80,  55);
    static final Color ACCENT    = new Color(52, 211, 153);
    static final Color TEXT      = new Color(225, 240, 232);
    static final Color TEXT_DIM  = new Color(110, 148, 125);
    static final Color COL_GOAL  = new Color(56,  189, 248);
    static final Color COL_ASST  = new Color(129, 140, 248);
    static final Color COL_YC    = new Color(250, 204,  21);
    static final Color COL_RC    = new Color(239,  68,  68);
    static final Color COL_PLUS  = new Color( 74, 222, 128);
    static final Color BTN_BG    = new Color( 52, 211, 153);
    static final Color BTN_FG    = new Color( 10,  30,  18);
    static final Color HOME_CLR  = new Color( 56, 189, 248);   // home team accent
    static final Color AWAY_CLR  = new Color(249, 115,  22);   // away team accent

    // ─── Fonts ───────────────────────────────────────────────────────────────
    static final Font F_TITLE  = new Font("Helvetica Neue", Font.BOLD,  22);
    static final Font F_HEADER = new Font("Helvetica Neue", Font.BOLD,  13);
    static final Font F_BODY   = new Font("Helvetica Neue", Font.PLAIN, 13);
    static final Font F_SMALL  = new Font("Helvetica Neue", Font.PLAIN, 11);
    static final Font F_NAV    = new Font("Helvetica Neue", Font.BOLD,  14);

    // ─── Shared State ────────────────────────────────────────────────────────
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JLabel statusLabel;
    private JLabel balanceLabel;   // shown in sidebar

    // Teams panel
    private DefaultTableModel teamsModel;

    // Players panel
    private DefaultTableModel playersModel;
    private JComboBox<String> playerTeamBox;

    // Stats panel
    private JComboBox<String> statsTeamBox;
    private JComboBox<String> statsPlayerBox;

    // Rankings panel
    private DefaultTableModel rankModel;
    private DefaultTableModel scorersModel;

    // Match predictor panel
    private JComboBox<String> homeBox, awayBox;
    private JLabel homeStrengthLbl, awayStrengthLbl;
    private JLabel homeOddsLbl, drawOddsLbl, awayOddsLbl;
    private JLabel homePctLbl, awayPctLbl;
    private JPanel probBarHome, probBarAway;
    private JTextArea topPlayersArea;
    private JLabel resultLabel;
    private JTextField betField;
    private JButton betHomeBtn, betDrawBtn, betAwayBtn;

    // ─── Constructor ─────────────────────────────────────────────────────────

    public SoccerTrackerGUI() {
        super("Soccer Tracker  ⚽");
        Data.load();

        try { UIManager.setLookAndFeel(new javax.swing.plaf.metal.MetalLookAndFeel()); }
        catch (Exception ignored) {}

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 700);
        setMinimumSize(new Dimension(860, 580));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);

        buildUI();
        setVisible(true);
    }

    // ─── Top-level Layout ────────────────────────────────────────────────────

    private void buildUI() {
        setLayout(new BorderLayout(0, 0));
        add(buildSidebar(), BorderLayout.WEST);

        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG);
        contentPanel.add(buildTeamsPanel(),         "TEAMS");
        contentPanel.add(buildPlayersPanel(),        "PLAYERS");
        contentPanel.add(buildStatsPanel(),          "STATS");
        contentPanel.add(buildRankingsPanel(),       "RANKINGS");
        contentPanel.add(buildMatchPredictorPanel(), "PREDICTOR");
        add(contentPanel, BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);

        cardLayout.show(contentPanel, "TEAMS");
    }

    // ─── Sidebar ─────────────────────────────────────────────────────────────

    private JPanel buildSidebar() {
        JPanel sb = new JPanel();
        sb.setBackground(SIDEBAR);
        sb.setPreferredSize(new Dimension(210, 0));
        sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));
        sb.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER));

        JLabel logo = new JLabel("  ⚽  Soccer Tracker");
        logo.setFont(new Font("Helvetica Neue", Font.BOLD, 15));
        logo.setForeground(ACCENT);
        logo.setBorder(new EmptyBorder(22, 6, 22, 0));
        logo.setAlignmentX(LEFT_ALIGNMENT);
        sb.add(logo);
        sb.add(sidebarSep());

        sb.add(Box.createRigidArea(new Dimension(0, 8)));
        sb.add(navBtn("  Teams",           "TEAMS"));
        sb.add(navBtn("  Players",         "PLAYERS"));
        sb.add(navBtn("  Statistics",      "STATS"));
        sb.add(navBtn("  Rankings",        "RANKINGS"));
        sb.add(Box.createRigidArea(new Dimension(0, 6)));
        sb.add(sidebarSep());
        sb.add(Box.createRigidArea(new Dimension(0, 6)));
        sb.add(navBtn("  Match Predictor", "PREDICTOR"));
        sb.add(Box.createVerticalGlue());
        sb.add(sidebarSep());

        // Balance display
        balanceLabel = new JLabel("  💰 " + Data.getBalance() + " coins");
        balanceLabel.setFont(F_SMALL);
        balanceLabel.setForeground(COL_YC);
        balanceLabel.setBorder(new EmptyBorder(8, 6, 4, 0));
        balanceLabel.setAlignmentX(LEFT_ALIGNMENT);
        sb.add(balanceLabel);

        JLabel hint = new JLabel("  Data auto-saved  💾");
        hint.setFont(F_SMALL);
        hint.setForeground(TEXT_DIM);
        hint.setBorder(new EmptyBorder(2, 6, 14, 0));
        hint.setAlignmentX(LEFT_ALIGNMENT);
        sb.add(hint);
        return sb;
    }

    private JButton navBtn(String text, String card) {
        JButton btn = new JButton(text);
        btn.setFont(F_NAV);
        btn.setForeground(TEXT_DIM);
        btn.setBackground(SIDEBAR);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(210, 44));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setForeground(ACCENT); btn.setBackground(new Color(20,62,35)); }
            public void mouseExited(MouseEvent e)  { btn.setForeground(TEXT_DIM); btn.setBackground(SIDEBAR); }
        });
        btn.addActionListener(e -> { cardLayout.show(contentPanel, card); refreshPanel(card); });
        return btn;
    }

    private void refreshPanel(String card) {
        switch (card) {
            case "TEAMS"     -> rebuildTeamsTable();
            case "PLAYERS"   -> { refreshPlayerTeamBox(); rebuildPlayersTable(); }
            case "STATS"     -> refreshStatsBoxes();
            case "RANKINGS"  -> rebuildRankings();
            case "PREDICTOR" -> { refreshMatchBoxes(); updateOddsDisplay(); }
        }
    }

    // ─── Status Bar ──────────────────────────────────────────────────────────

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bar.setBackground(new Color(10,18,13));
        bar.setBorder(BorderFactory.createMatteBorder(1,0,0,0, BORDER));
        bar.setPreferredSize(new Dimension(0, 26));
        statusLabel = new JLabel("  Ready");
        statusLabel.setFont(F_SMALL);
        statusLabel.setForeground(TEXT_DIM);
        bar.add(statusLabel);
        return bar;
    }

    private void setStatus(String msg) { statusLabel.setText("  " + msg); }

    // ─── Teams Panel ─────────────────────────────────────────────────────────

    private JPanel buildTeamsPanel() {
        JPanel p = contentPanel("Teams",
                makeButton("+ Club Team",     e -> addClubTeam()),
                makeButton("+ National Team", e -> addNationalTeam()));
        String[] cols = {"Team Name", "Type", "Country", "Players"};
        teamsModel = noEditModel(cols);
        JTable t = styledTable(teamsModel);
        t.getColumnModel().getColumn(0).setPreferredWidth(240);
        t.getColumnModel().getColumn(1).setPreferredWidth(130);
        t.getColumnModel().getColumn(2).setPreferredWidth(160);
        t.getColumnModel().getColumn(3).setPreferredWidth(80);
        p.add(styledScroll(t), BorderLayout.CENTER);
        rebuildTeamsTable();
        return p;
    }

    private void rebuildTeamsTable() {
        if (teamsModel == null) return;
        teamsModel.setRowCount(0);
        for (Team t : Data.getTeams())
            teamsModel.addRow(new Object[]{ t.getName(), t.getTypeLabel(),
                    t.isNationalTeam() ? t.getCountry() : "—", t.getPlayers().size() });
    }

    private void addClubTeam() {
        String name = prompt("Club team name:");
        if (name == null) return;
        if (Data.addClubTeam(name)) { rebuildTeamsTable(); setStatus("Added: " + name); }
        else error("'" + name + "' already exists.");
    }

    private void addNationalTeam() {
        JTextField nf = inputField(), cf = inputField();
        if (JOptionPane.showConfirmDialog(this,
                new Object[]{"Team name:", nf, "Country:", cf}, "Add National Team",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;
        String n = nf.getText().trim(), c = cf.getText().trim();
        if (n.isEmpty() || c.isEmpty()) { error("Both fields required."); return; }
        if (Data.addNationalTeam(n, c)) { rebuildTeamsTable(); setStatus("Added: " + n); }
        else error("'" + n + "' already exists.");
    }

    // ─── Players Panel ────────────────────────────────────────────────────────

    private JPanel buildPlayersPanel() {
        playerTeamBox = styledCombo(200);
        playerTeamBox.addActionListener(e -> rebuildPlayersTable());
        JPanel extras = hPanel(dimLabel("Team: "), playerTeamBox,
                Box.createHorizontalStrut(8), makeButton("+ Add Player", e -> addPlayer()));
        JPanel p = contentPanel("Players", extras);
        String[] cols = {"#","Name","Position","Goals","Assists","PK","YC","RC","Score"};
        playersModel = noEditModel(cols);
        JTable t = styledTable(playersModel);
        t.setDefaultRenderer(Object.class, new StatsRenderer());
        t.getColumnModel().getColumn(0).setPreferredWidth(50);
        t.getColumnModel().getColumn(1).setPreferredWidth(210);
        t.getColumnModel().getColumn(2).setPreferredWidth(140);
        for (int i = 3; i <= 8; i++) t.getColumnModel().getColumn(i).setPreferredWidth(68);
        p.add(styledScroll(t), BorderLayout.CENTER);
        refreshPlayerTeamBox();
        return p;
    }

    private void refreshPlayerTeamBox() {
        if (playerTeamBox == null) return;
        playerTeamBox.removeAllItems();
        playerTeamBox.addItem("All Teams");
        for (Team t : Data.getTeams()) playerTeamBox.addItem(t.getName());
    }

    private void rebuildPlayersTable() {
        if (playersModel == null) return;
        playersModel.setRowCount(0);
        String sel = playerTeamBox != null && playerTeamBox.getSelectedItem() != null
                ? (String) playerTeamBox.getSelectedItem() : "All Teams";
        for (Team team : Data.getTeams()) {
            if (!sel.equals("All Teams") && !team.getName().equals(sel)) continue;
            ArrayList<Player> list = new ArrayList<>(team.getPlayers());
            list.sort((a, b) -> Integer.compare(a.getJerseyNumber(), b.getJerseyNumber()));
            for (Player pl : list)
                playersModel.addRow(new Object[]{ "#"+pl.getJerseyNumber(), pl.getName(),
                        pl.getPosition(), pl.getGoals(), pl.getAssists(),
                        pl.getPenaltyKicks(), pl.getYellowCards(), pl.getRedCards(),
                        pl.getPerformanceScore() });
        }
    }

    private void addPlayer() {
        if (Data.getTeams().isEmpty()) { error("Add a team first."); return; }
        JComboBox<String> tb = styledCombo(200);
        for (Team t : Data.getTeams()) tb.addItem(t.getName());
        JTextField nf = inputField(), jf = inputField(), pf = inputField();
        if (JOptionPane.showConfirmDialog(this,
                new Object[]{"Team:",tb,"Name:",nf,"Jersey #:",jf,"Position:",pf},
                "Add Player", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE)
                != JOptionPane.OK_OPTION) return;
        String n = nf.getText().trim(), pos = pf.getText().trim();
        if (n.isEmpty() || pos.isEmpty()) { error("All fields required."); return; }
        int j; try { j = Integer.parseInt(jf.getText().trim()); }
        catch (NumberFormatException ex) { error("Jersey must be a number."); return; }
        Team team = Data.findTeam((String) tb.getSelectedItem());
        if (Data.addPlayerToTeam(team, new Player(n, j, pos))) {
            refreshPlayerTeamBox(); rebuildPlayersTable();
            setStatus("Added " + n + " (#" + j + ") to " + team.getName());
        } else error("Jersey #" + j + " already taken on '" + team.getName() + "'.");
    }

    // ─── Stats Panel ─────────────────────────────────────────────────────────

    private JPanel buildStatsPanel() {
        statsTeamBox   = styledCombo(200);
        statsPlayerBox = styledCombo(200);
        statsTeamBox.addActionListener(e -> refreshStatsPlayerBox());

        JPanel p = new JPanel(new BorderLayout(0, 20));
        p.setBackground(BG); p.setBorder(new EmptyBorder(24,28,24,28));
        p.add(panelTitle("Record Statistics"), BorderLayout.NORTH);

        JPanel inner = new JPanel(); inner.setBackground(BG);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

        JPanel selRow = hPanel(dimLabel("Team:"), statsTeamBox,
                Box.createHorizontalStrut(18), dimLabel("Player:"), statsPlayerBox);
        selRow.setAlignmentX(LEFT_ALIGNMENT); inner.add(selRow);
        inner.add(Box.createRigidArea(new Dimension(0,28)));

        JPanel grid = new JPanel(new GridLayout(2,3,12,12));
        grid.setBackground(BG); grid.setMaximumSize(new Dimension(700,130));
        grid.setAlignmentX(LEFT_ALIGNMENT);
        grid.add(statBtn("⚽  Goals",         COL_GOAL, "goals",         "Goals"));
        grid.add(statBtn("🅰   Assists",       COL_ASST, "assists",       "Assists"));
        grid.add(statBtn("🎯  Penalty Kicks",  ACCENT,   "penalty kicks", "Penalty Kicks"));
        grid.add(statBtn("🟨  Yellow Card",    COL_YC,   "yellow cards",  "Yellow Cards"));
        grid.add(statBtn("🟥  Red Card",       COL_RC,   "red cards",     "Red Cards"));
        inner.add(grid);
        inner.add(Box.createRigidArea(new Dimension(0,28)));

        JLabel formula = new JLabel(
            "<html><font color='#6b9e82'>Score = </font>"
            + "<font color='#38bdf8'>Goals×3</font><font color='#6b9e82'> + </font>"
            + "<font color='#818cf8'>Assists×2</font><font color='#6b9e82'> + </font>"
            + "<font color='#34d399'>PK×1</font><font color='#6b9e82'> − </font>"
            + "<font color='#facc15'>YellowCards×2</font><font color='#6b9e82'> − </font>"
            + "<font color='#ef4444'>RedCards×5</font></html>");
        formula.setFont(F_BODY); formula.setAlignmentX(LEFT_ALIGNMENT);
        inner.add(formula);
        p.add(inner, BorderLayout.CENTER);
        refreshStatsBoxes();
        return p;
    }

    private void refreshStatsBoxes() { refreshStatsTeamBox(); }
    private void refreshStatsTeamBox() {
        if (statsTeamBox == null) return;
        statsTeamBox.removeAllItems();
        for (Team t : Data.getTeams()) statsTeamBox.addItem(t.getName());
        refreshStatsPlayerBox();
    }
    private void refreshStatsPlayerBox() {
        if (statsPlayerBox == null || statsTeamBox == null) return;
        statsPlayerBox.removeAllItems();
        Team t = Data.findTeam((String) statsTeamBox.getSelectedItem());
        if (t != null) for (Player p : t.getPlayers()) statsPlayerBox.addItem(p.getName());
    }

    private JButton statBtn(String text, Color color, String key, String label) {
        JButton btn = new JButton("<html><center>" + text + "</center></html>");
        btn.setFont(F_HEADER); btn.setForeground(color); btn.setBackground(PANEL);
        btn.setBorder(BorderFactory.createLineBorder(
                new Color(color.getRed(), color.getGreen(), color.getBlue(), 130), 2));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(35,52,42)); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(PANEL); }
        });
        btn.addActionListener(e -> recordStat(label, key));
        return btn;
    }

    private void recordStat(String label, String key) {
        String tn = (String) statsTeamBox.getSelectedItem();
        String pn = (String) statsPlayerBox.getSelectedItem();
        if (tn == null || pn == null) { error("Select a team and player first."); return; }
        Team team = Data.findTeam(tn); if (team == null) return;
        Player player = team.findPlayerByName(pn); if (player == null) return;
        String in = JOptionPane.showInputDialog(this,
                "Add how many " + label.toLowerCase() + " for " + pn + "?",
                "Record " + label, JOptionPane.PLAIN_MESSAGE);
        if (in == null) return;
        int amount; try { amount = Integer.parseInt(in.trim()); }
        catch (NumberFormatException ex) { error("Please enter a number."); return; }
        if (amount < 0) { error("Amount cannot be negative."); return; }
        switch (key) {
            case "goals"         -> player.addGoals(amount);
            case "assists"       -> player.addAssists(amount);
            case "penalty kicks" -> player.addPenaltyKicks(amount);
            case "yellow cards"  -> player.addYellowCards(amount);
            case "red cards"     -> player.addRedCards(amount);
        }
        Data.save();
        rebuildPlayersTable(); rebuildRankings();
        setStatus(pn + ": +" + amount + " " + label.toLowerCase());
    }

    // ─── Rankings Panel ───────────────────────────────────────────────────────

    private JPanel buildRankingsPanel() {
        JPanel p = contentPanel("Rankings");
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(PANEL); tabs.setForeground(TEXT_DIM);
        tabs.setFont(F_HEADER);

        String[] rc = {"Rank","Name","Goals","Assists","PK","YC","RC","Score"};
        rankModel = noEditModel(rc);
        JTable rt = styledTable(rankModel);
        rt.setDefaultRenderer(Object.class, new StatsRenderer());
        rt.getColumnModel().getColumn(0).setPreferredWidth(60);
        rt.getColumnModel().getColumn(1).setPreferredWidth(220);
        for (int i = 2; i <= 7; i++) rt.getColumnModel().getColumn(i).setPreferredWidth(72);
        tabs.addTab("  🏆  Performance Rankings  ", styledScroll(rt));

        String[] sc = {"Rank","Name","Team","Goals"};
        scorersModel = noEditModel(sc);
        JTable st = styledTable(scorersModel);
        st.getColumnModel().getColumn(0).setPreferredWidth(60);
        st.getColumnModel().getColumn(1).setPreferredWidth(220);
        st.getColumnModel().getColumn(2).setPreferredWidth(200);
        st.getColumnModel().getColumn(3).setPreferredWidth(80);
        tabs.addTab("  ⚽  Top Goal Scorers  ", styledScroll(st));

        p.add(tabs, BorderLayout.CENTER);
        rebuildRankings();
        return p;
    }

    private void rebuildRankings() {
        if (rankModel == null) return;
        String[] medals = {"🥇","🥈","🥉"};
        rankModel.setRowCount(0);
        ArrayList<Player> byScore = Data.getAllPlayersSortedByPerformance();
        for (int i = 0; i < byScore.size(); i++) {
            Player pl = byScore.get(i);
            rankModel.addRow(new Object[]{ i<3?medals[i]:"#"+(i+1), pl.getName(),
                    pl.getGoals(), pl.getAssists(), pl.getPenaltyKicks(),
                    pl.getYellowCards(), pl.getRedCards(), pl.getPerformanceScore() });
        }
        if (scorersModel == null) return;
        scorersModel.setRowCount(0);
        ArrayList<Player> byGoals = Data.getAllPlayersSortedByGoals();
        for (int i = 0; i < byGoals.size(); i++) {
            Player pl = byGoals.get(i);
            String teamName = "—";
            for (Team t : Data.getTeams())
                if (t.findPlayerByName(pl.getName()) != null) { teamName = t.getName(); break; }
            scorersModel.addRow(new Object[]{ i<3?medals[i]:"#"+(i+1),
                    pl.getName(), teamName, pl.getGoals() });
        }
    }

    // ─── Match Predictor Panel ────────────────────────────────────────────────

    private JPanel buildMatchPredictorPanel() {
        JPanel main = new JPanel(new BorderLayout(0, 18));
        main.setBackground(BG);
        main.setBorder(new EmptyBorder(24, 28, 24, 28));

        // ── Title row with balance ────────────────────────────────────────────
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setBackground(BG);
        titleRow.add(panelTitle("Match Predictor"), BorderLayout.WEST);
        main.add(titleRow, BorderLayout.NORTH);

        // ── Centre: two team columns + probability bar ────────────────────────
        JPanel centre = new JPanel(new BorderLayout(0, 14));
        centre.setBackground(BG);

        // Team selector row
        homeBox = styledCombo(220); awayBox = styledCombo(220);
        ActionListener refreshOdds = e -> updateOddsDisplay();
        homeBox.addActionListener(refreshOdds);
        awayBox.addActionListener(refreshOdds);

        JPanel selectorRow = hPanel(
                teamPill("HOME", HOME_CLR), homeBox,
                Box.createHorizontalStrut(20),
                new JLabel(boldColored("  vs  ", TEXT_DIM)),
                Box.createHorizontalStrut(20),
                teamPill("AWAY", AWAY_CLR), awayBox);
        centre.add(selectorRow, BorderLayout.NORTH);

        // Probability section
        JPanel probSection = new JPanel();
        probSection.setBackground(BG);
        probSection.setLayout(new BoxLayout(probSection, BoxLayout.Y_AXIS));

        // Percentage labels + bar
        JPanel pctRow = new JPanel(new BorderLayout());
        pctRow.setBackground(BG);
        homePctLbl = colorLabel("—", HOME_CLR, Font.BOLD, 18);
        awayPctLbl = colorLabel("—", AWAY_CLR, Font.BOLD, 18);
        JLabel vsLbl = colorLabel(" win probability ", TEXT_DIM, Font.PLAIN, 13);
        pctRow.add(homePctLbl,   BorderLayout.WEST);
        pctRow.add(vsLbl,        BorderLayout.CENTER);
        pctRow.add(awayPctLbl,   BorderLayout.EAST);
        pctRow.setAlignmentX(LEFT_ALIGNMENT);
        probSection.add(pctRow);
        probSection.add(Box.createRigidArea(new Dimension(0, 8)));

        // Visual probability bar (two coloured panels side-by-side)
        JPanel barWrapper = new JPanel(new GridLayout(1, 2, 2, 0));
        barWrapper.setBackground(BG);
        barWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        barWrapper.setAlignmentX(LEFT_ALIGNMENT);
        probBarHome = new JPanel(); probBarHome.setBackground(HOME_CLR);
        probBarAway = new JPanel(); probBarAway.setBackground(AWAY_CLR);
        barWrapper.add(probBarHome); barWrapper.add(probBarAway);
        probSection.add(barWrapper);
        probSection.add(Box.createRigidArea(new Dimension(0, 12)));

        // Odds row
        homeOddsLbl = colorLabel("@—", HOME_CLR, Font.BOLD, 14);
        drawOddsLbl = colorLabel("Draw @—", TEXT_DIM, Font.PLAIN, 13);
        awayOddsLbl = colorLabel("@—", AWAY_CLR, Font.BOLD, 14);
        JPanel oddsRow = hPanel(homeOddsLbl,
                Box.createHorizontalStrut(24), drawOddsLbl,
                Box.createHorizontalStrut(24), awayOddsLbl);
        oddsRow.setAlignmentX(LEFT_ALIGNMENT);
        probSection.add(oddsRow);
        probSection.add(Box.createRigidArea(new Dimension(0, 16)));

        // Top players summary
        topPlayersArea = new JTextArea(8, 50);
        topPlayersArea.setEditable(false);
        topPlayersArea.setBackground(PANEL);
        topPlayersArea.setForeground(TEXT);
        topPlayersArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        topPlayersArea.setBorder(new EmptyBorder(10, 12, 10, 12));
        JScrollPane tpScroll = new JScrollPane(topPlayersArea);
        tpScroll.setBorder(BorderFactory.createLineBorder(BORDER));
        tpScroll.setAlignmentX(LEFT_ALIGNMENT);
        probSection.add(tpScroll);

        centre.add(probSection, BorderLayout.CENTER);
        main.add(centre, BorderLayout.CENTER);

        // ── South: bet controls + result ──────────────────────────────────────
        JPanel south = new JPanel();
        south.setBackground(PANEL);
        south.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(14, 18, 14, 18)));
        south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));

        JPanel betRow = hPanel(
                dimLabel("Bet amount:"),
                Box.createHorizontalStrut(8),
                betField = betInput(),
                Box.createHorizontalStrut(4),
                dimLabel("coins"),
                Box.createHorizontalStrut(22),
                betHomeBtn = betButton("Bet HOME", HOME_CLR),
                Box.createHorizontalStrut(8),
                betDrawBtn = betButton("Draw",     TEXT_DIM),
                Box.createHorizontalStrut(8),
                betAwayBtn = betButton("Bet AWAY", AWAY_CLR));
        betRow.setAlignmentX(LEFT_ALIGNMENT);
        south.add(betRow);
        south.add(Box.createRigidArea(new Dimension(0, 10)));

        resultLabel = new JLabel(" ");
        resultLabel.setFont(new Font("Helvetica Neue", Font.BOLD, 14));
        resultLabel.setForeground(TEXT);
        resultLabel.setAlignmentX(LEFT_ALIGNMENT);
        south.add(resultLabel);

        betHomeBtn.addActionListener(e -> placeBet(1));
        betDrawBtn.addActionListener(e -> placeBet(0));
        betAwayBtn.addActionListener(e -> placeBet(-1));

        main.add(south, BorderLayout.SOUTH);

        refreshMatchBoxes();
        updateOddsDisplay();
        return main;
    }

    private void refreshMatchBoxes() {
        if (homeBox == null) return;
        homeBox.removeAllItems();
        awayBox.removeAllItems();
        for (Team t : Data.getTeams()) {
            homeBox.addItem(t.getName());
            awayBox.addItem(t.getName());
        }
        // Default: different teams selected
        if (awayBox.getItemCount() > 1) awayBox.setSelectedIndex(1);
        updateOddsDisplay();
    }

    private void updateOddsDisplay() {
        if (homeBox == null || homeBox.getItemCount() == 0) return;
        String hn = (String) homeBox.getSelectedItem();
        String an = (String) awayBox.getSelectedItem();
        if (hn == null || an == null) return;

        Team home = Data.findTeam(hn);
        Team away = Data.findTeam(an);
        if (home == null || away == null) return;

        double[] probs = MatchPredictor.probabilities(home, away);
        double p1 = probs[0], draw = probs[1], p2 = probs[2];

        int pct1 = (int) Math.round(p1 * 100);
        int pct2 = (int) Math.round(p2 * 100);

        homePctLbl.setText(hn + "  " + pct1 + "%");
        awayPctLbl.setText(pct2 + "%  " + an);

        // Resize probability bar
        int totalCols = 100;
        int homeCols = Math.max(2, Math.min(98, pct1));
        probBarHome.setPreferredSize(new Dimension(homeCols * 5, 20));
        probBarAway.setPreferredSize(new Dimension((totalCols - homeCols) * 5, 20));
        probBarHome.revalidate(); probBarAway.revalidate();

        double o1 = MatchPredictor.odds(p1);
        double od = MatchPredictor.odds(draw);
        double o2 = MatchPredictor.odds(p2);
        homeOddsLbl.setText(hn + " @" + String.format("%.2f", o1) + "x");
        drawOddsLbl.setText("Draw  @" + String.format("%.2f", od) + "x");
        awayOddsLbl.setText(an + " @" + String.format("%.2f", o2) + "x");

        // Update bet button labels
        betHomeBtn.setText("Bet HOME  @" + String.format("%.2f", o1) + "x");
        betDrawBtn.setText("Draw  @" + String.format("%.2f", od) + "x");
        betAwayBtn.setText("Bet AWAY  @" + String.format("%.2f", o2) + "x");

        // Top players summary
        updateTopPlayersText(home, away);
        resultLabel.setText(" ");
    }

    private void updateTopPlayersText(Team home, Team away) {
        StringBuilder sb = new StringBuilder();
        double hs = MatchPredictor.teamStrength(home);
        double as = MatchPredictor.teamStrength(away);
        sb.append(String.format("  %-28s  │  %-28s%n",
                "HOME: " + home.getName() + " (str " + (int)hs + ")",
                "AWAY: " + away.getName() + " (str " + (int)as + ")"));
        sb.append(String.format("  %-28s  │  %-28s%n", "─".repeat(26), "─".repeat(26)));

        ArrayList<Player> hp = MatchPredictor.topPlayers(home, 5);
        ArrayList<Player> ap = MatchPredictor.topPlayers(away, 5);
        int rows = Math.max(hp.size(), ap.size());
        for (int i = 0; i < rows; i++) {
            String left  = i < hp.size()
                    ? String.format("  %-20s  G:%-3d Sc:%-4d", hp.get(i).getName(),
                      hp.get(i).getGoals(), hp.get(i).getPerformanceScore()) : "";
            String right = i < ap.size()
                    ? String.format("  %-20s  G:%-3d Sc:%-4d", ap.get(i).getName(),
                      ap.get(i).getGoals(), ap.get(i).getPerformanceScore()) : "";
            sb.append(String.format("  %-28s  │  %-28s%n", left, right));
        }
        topPlayersArea.setText(sb.toString());
    }

    private void placeBet(int side) {  // 1=home, 0=draw, -1=away
        String hn = (String) homeBox.getSelectedItem();
        String an = (String) awayBox.getSelectedItem();
        if (hn == null || an == null || hn.equals(an)) {
            error("Select two different teams."); return;
        }
        Team home = Data.findTeam(hn), away = Data.findTeam(an);
        if (home == null || away == null) { error("Teams not found."); return; }

        int betAmt;
        try { betAmt = Integer.parseInt(betField.getText().trim()); }
        catch (NumberFormatException e) { error("Enter a valid bet amount."); return; }
        if (betAmt <= 0)              { error("Bet must be greater than 0."); return; }
        if (betAmt > Data.getBalance()) { error("Not enough coins! Balance: " + Data.getBalance()); return; }

        double[] probs = MatchPredictor.probabilities(home, away);
        double odds = side ==  1 ? MatchPredictor.odds(probs[0])
                    : side ==  0 ? MatchPredictor.odds(probs[1])
                    :              MatchPredictor.odds(probs[2]);

        // Simulate the match
        int[] score = MatchPredictor.simulateScore(home, away);
        int g1 = score[0], g2 = score[1];
        int result = g1 > g2 ? 1 : g1 < g2 ? -1 : 0;
        boolean won = result == side;

        int delta = won ? (int)(betAmt * odds) - betAmt : -betAmt;
        Data.addToBalance(delta);

        String resultStr = hn + "  " + g1 + " — " + g2 + "  " + an;
        String outcome = won
                ? "🎉  You won " + (int)(betAmt * odds) + " coins!  (+" + delta + ")"
                : "💸  You lost " + betAmt + " coins.";
        String newBal = "  Balance: " + Data.getBalance() + " coins";

        resultLabel.setText("<html><font color='" + (won ? "#4ade80" : "#f87171") + "'>"
                + resultStr + "  —  " + outcome + "</font>"
                + "<font color='#6b9482'>" + newBal + "</font></html>");

        // Refresh balance label in sidebar
        balanceLabel.setText("  💰 " + Data.getBalance() + " coins");
        setStatus("Match: " + resultStr + "  |  " + (won ? "WON" : "LOST") + " " + Math.abs(delta) + " coins");
    }

    // ─── Reusable UI Factories ───────────────────────────────────────────────

    private JPanel contentPanel(String title, Component... topRight) {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setBackground(BG); p.setBorder(new EmptyBorder(24, 28, 24, 28));
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(BG); row.add(panelTitle(title), BorderLayout.WEST);
        if (topRight.length > 0) { JPanel r = hPanel(topRight); r.setBackground(BG); row.add(r, BorderLayout.EAST); }
        p.add(row, BorderLayout.NORTH);
        return p;
    }

    private JLabel panelTitle(String text) {
        JLabel l = new JLabel(text); l.setFont(F_TITLE); l.setForeground(TEXT); return l;
    }
    private JLabel dimLabel(String text) {
        JLabel l = new JLabel(text); l.setFont(F_BODY); l.setForeground(TEXT_DIM); return l;
    }
    private JLabel colorLabel(String text, Color color, int style, int size) {
        JLabel l = new JLabel(text); l.setFont(new Font("Helvetica Neue", style, size));
        l.setForeground(color); return l;
    }
    private String boldColored(String text, Color c) {
        return "<html><font color='#" + String.format("%02x%02x%02x",c.getRed(),c.getGreen(),c.getBlue())
                + "'><b>" + text + "</b></font></html>";
    }
    private JLabel teamPill(String text, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Helvetica Neue", Font.BOLD, 11));
        l.setForeground(color);
        l.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 1),
                new EmptyBorder(2, 8, 2, 8)));
        return l;
    }

    private JPanel hPanel(Component... comps) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        p.setBackground(BG);
        for (Component c : comps) p.add(c);
        return p;
    }

    private JButton makeButton(String text, ActionListener a) {
        JButton b = new JButton(text); b.setFont(F_HEADER); b.setBackground(BTN_BG);
        b.setForeground(BTN_FG); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(160,34));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(ACCENT.darker()); }
            public void mouseExited(MouseEvent e)  { b.setBackground(BTN_BG); }
        });
        b.addActionListener(a); return b;
    }

    private JButton betButton(String text, Color color) {
        JButton b = new JButton(text);
        b.setFont(new Font("Helvetica Neue", Font.BOLD, 12));
        b.setForeground(color); b.setBackground(PANEL);
        b.setBorder(BorderFactory.createLineBorder(
                new Color(color.getRed(), color.getGreen(), color.getBlue(), 160), 2));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(new Color(35,52,42)); }
            public void mouseExited(MouseEvent e)  { b.setBackground(PANEL); }
        });
        return b;
    }

    private JTextField betInput() {
        JTextField f = new JTextField("100", 7);
        f.setBackground(PANEL); f.setForeground(TEXT); f.setFont(F_BODY);
        f.setCaretColor(TEXT);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(3,8,3,8)));
        return f;
    }

    private JTable styledTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setBackground(ROW_A); t.setForeground(TEXT); t.setFont(F_BODY);
        t.setRowHeight(34); t.setGridColor(BORDER);
        t.setSelectionBackground(new Color(40,100,62)); t.setSelectionForeground(TEXT);
        t.setShowHorizontalLines(true); t.setShowVerticalLines(false);
        t.setFillsViewportHeight(true); t.setIntercellSpacing(new Dimension(0,1));
        JTableHeader h = t.getTableHeader();
        h.setBackground(HEADER_BG); h.setForeground(ACCENT); h.setFont(F_HEADER);
        h.setPreferredSize(new Dimension(0,38)); h.setReorderingAllowed(false);
        ((DefaultTableCellRenderer) h.getDefaultRenderer()).setBorder(new EmptyBorder(0,12,0,12));
        return t;
    }

    private JScrollPane styledScroll(JTable t) {
        JScrollPane s = new JScrollPane(t);
        s.getViewport().setBackground(ROW_A);
        s.setBorder(BorderFactory.createLineBorder(BORDER,1));
        s.setBackground(ROW_A); return s;
    }

    private JComboBox<String> styledCombo(int w) {
        JComboBox<String> b = new JComboBox<>();
        b.setBackground(PANEL); b.setForeground(TEXT); b.setFont(F_BODY);
        b.setPreferredSize(new Dimension(w,32)); return b;
    }

    private JTextField inputField() {
        JTextField f = new JTextField(18);
        f.setBackground(PANEL); f.setForeground(TEXT); f.setFont(F_BODY); f.setCaretColor(TEXT);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(4,8,4,8)));
        return f;
    }

    private DefaultTableModel noEditModel(String[] cols) {
        return new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
    }

    private JSeparator sidebarSep() {
        JSeparator s = new JSeparator();
        s.setForeground(BORDER); s.setBackground(BORDER);
        s.setMaximumSize(new Dimension(210,1)); return s;
    }

    private String prompt(String msg) {
        String v = JOptionPane.showInputDialog(this, msg, "Input", JOptionPane.PLAIN_MESSAGE);
        return (v == null || v.isBlank()) ? null : v.trim();
    }
    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // ─── Custom Table Cell Renderer ───────────────────────────────────────────

    class StatsRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean sel, boolean foc, int row, int col) {
            Component c = super.getTableCellRendererComponent(table, value, sel, foc, row, col);
            if (!sel) {
                c.setBackground(row % 2 == 0 ? ROW_A : ROW_B);
                c.setForeground(TEXT);
                String cn = table.getColumnModel().getColumn(col).getHeaderValue().toString();
                switch (cn) {
                    case "Goals"   -> c.setForeground(COL_GOAL);
                    case "Assists" -> c.setForeground(COL_ASST);
                    case "YC"      -> c.setForeground(COL_YC);
                    case "RC"      -> c.setForeground(COL_RC);
                    case "Score"   -> {
                        try { int s = Integer.parseInt(value.toString());
                              c.setForeground(s > 0 ? COL_PLUS : s < 0 ? COL_RC : TEXT_DIM);
                        } catch (NumberFormatException ignored) {}
                    }
                }
            }
            if (c instanceof JLabel lbl) lbl.setBorder(new EmptyBorder(0,12,0,12));
            return c;
        }
    }

    // ─── Entry Point ──────────────────────────────────────────────────────────

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SoccerTrackerGUI::new);
    }
}
