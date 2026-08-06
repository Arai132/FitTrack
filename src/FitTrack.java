// This file is the runner
//whatever imports here. 

import javax.swing.SwingUtilities;

import controller.MainController;
import data.DataStore;
import facade.FitTrackFacade;
import view.MainFrame;

public class FitTrack {
    public static void main(String[] args) {
        // 1. Load previous data (if any)
        DataStore.getInstance().load();

        // 2. Auto-save when the JVM exits (window close, System.exit, etc.)
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DataStore.getInstance().save();
        }));
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