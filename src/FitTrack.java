// This file is the runner
//whatever imports here. 

import javax.swing.SwingUtilities;

import controller.MainController;
import facade.FitTrackFacade;
import view.MainFrame;

public class FitTrack {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FitTrackFacade facade = new FitTrackFacade(); // creates DataStore + factories
            MainController controller = new MainController(facade);
            MainFrame mainFrame = new MainFrame(facade, controller);

            mainFrame.setVisible(true);
        });
    }
}