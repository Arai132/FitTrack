// This file is the runner
//whatever imports here. 

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {

        // maybe set the loook or something here.

        // these don't exist yet, add later.
        SwingUtilities.invokeLater(() -> {
            FitTrackFacade facade = new FitTrackFacade(); // creates DataStore + factories
            MainController controller = new MainController(facade);
            MainFrame mainFrame = new MainFrame(facade, controller);

            mainFrame.setVisible(true);
        });
    }
}