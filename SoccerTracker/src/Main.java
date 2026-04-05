import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Launch the graphical interface
        SwingUtilities.invokeLater(SoccerTrackerGUI::new);
    }
}
