package controller;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import facade.FitTrackFacade;
import view.ActiveWorkoutFrame;
import view.ExerciseFrame;
import view.HistoryFrame;
import view.MainFrame;
import view.ProfileFrame;
import view.WorkoutFrame;

/**
 * Controller – reacts to user actions from the views
 * and decides which window to open / what to ask the Facade.
 *
 * It does NOT contain business rules and does NOT talk to DataStore directly.
 */
public class MainController {

    private final FitTrackFacade facade;
    private MainFrame mainFrame;
    private JFrame currentChildFrame;

    public MainController(FitTrackFacade facade) {
        this.facade = facade;
    }

    public void setMainFrame(MainFrame frame) {
        this.mainFrame = frame;
    }

    private void showChildFrame(JFrame frame) {
        if (currentChildFrame != null) {
            currentChildFrame.dispose();
            currentChildFrame = null;
        }
        if (mainFrame != null && mainFrame.isVisible()) {
            mainFrame.setVisible(false);
        }

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (frame == currentChildFrame) {
                    currentChildFrame = null;
                    if (mainFrame != null) {
                        mainFrame.setVisible(true);
                        refreshMain();
                    }
                }
            }
        });

        currentChildFrame = frame;
        frame.setVisible(true);
    }

    // ----- navigation -----

    public void openProfileManager() {
        showChildFrame(new ProfileFrame(facade, this));
    }

    public void openExercises() {
        if (!ensureProfile())
            return;
        showChildFrame(new ExerciseFrame(facade));
    }

    public void openWorkouts() {
        if (!ensureProfile())
            return;
        showChildFrame(new WorkoutFrame(facade));
    }

    public void openActiveWorkout() {
        if (!ensureProfile())
            return;
        if (facade.getActiveWorkouts().isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame,
                    "Create a workout with at least one exercise first.",
                    "No Workouts", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // ActiveWorkoutFrame will register itself as WorkoutObserver
        showChildFrame(new ActiveWorkoutFrame(facade));
    }

    public void openHistory() {
        if (!ensureProfile())
            return;
        showChildFrame(new HistoryFrame(facade));
    }

    /** Called by ProfileFrame after create / switch / delete */
    public void refreshMain() {
        if (mainFrame != null)
            mainFrame.refreshStatus();
    }

    private boolean ensureProfile() {
        if (facade.getCurrentProfile() == null) {
            JOptionPane.showMessageDialog(mainFrame,
                    "Please create or select a profile first.",
                    "No Profile", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
}