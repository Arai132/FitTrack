package controller;

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

    public MainController(FitTrackFacade facade) {
        this.facade = facade;
    }

    public void setMainFrame(MainFrame frame) {
        this.mainFrame = frame;
    }

    // ----- navigation -----

    public void openProfileManager() {
        new ProfileFrame(facade, this).setVisible(true);
    }

    public void openExercises() {
        if (!ensureProfile())
            return;
        new ExerciseFrame(facade).setVisible(true);
    }

    public void openWorkouts() {
        if (!ensureProfile())
            return;
        new WorkoutFrame(facade).setVisible(true);
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
        new ActiveWorkoutFrame(facade).setVisible(true);
    }

    public void openHistory() {
        if (!ensureProfile())
            return;
        new HistoryFrame(facade).setVisible(true);
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