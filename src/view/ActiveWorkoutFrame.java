package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import facade.FitTrackFacade;
import model.ExerciseSet;
import model.Workout;
import model.WorkoutSession;
import observer.WorkoutObserver;

/**
 * View + Observer.
 * WorkoutSession (Subject) pushes updates here when sets are marked complete.
 */
public class ActiveWorkoutFrame extends JFrame implements WorkoutObserver {
    private final FitTrackFacade facade;
    private WorkoutSession session;

    private final DefaultListModel<ExerciseSet> setModel = new DefaultListModel<>();
    private final JList<ExerciseSet> setList = new JList<>(setModel);
    private final JLabel progressLabel = new JLabel("Select a workout and press Start", SwingConstants.CENTER);
    private final JComboBox<Workout> workoutCombo;

    public ActiveWorkoutFrame(FitTrackFacade facade) {
        super("FitTrack - Track Current Workout");
        this.facade = facade;

        setSize(1000, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBorder(BorderFactory.createTitledBorder(
                "Start Workout  (profile: " + facade.getCurrentProfile().getName() + ")"));
        List<Workout> workouts = facade.getActiveWorkouts();
        workoutCombo = new JComboBox<>(workouts.toArray(new Workout[0]));
        top.add(new JLabel("Workout:"));
        top.add(workoutCombo);
        JButton btnStart = new JButton("Start");
        btnStart.addActionListener(e -> startSession());
        top.add(btnStart);
        add(top, BorderLayout.NORTH);

        setList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(setList);
        scroll.setBorder(BorderFactory.createTitledBorder("Sets"));
        add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        progressLabel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        bottom.add(progressLabel, BorderLayout.NORTH);

        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton btnComplete = new JButton("Mark Selected Set Complete");
        JButton btnFinish = new JButton("Finish & Save to History");
        JButton btnClose = new JButton("Close");

        btnComplete.addActionListener(e -> markComplete());
        btnFinish.addActionListener(e -> finishSession());
        btnClose.addActionListener(e -> dispose());

        btnPanel.add(btnComplete);
        btnPanel.add(btnFinish);
        btnPanel.add(btnClose);
        bottom.add(btnPanel, BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);
    }

    private void startSession() {
        Workout w = (Workout) workoutCombo.getSelectedItem();
        if (w == null) {
            JOptionPane.showMessageDialog(this, "No workout selected");
            return;
        }
        try {
            session = facade.startWorkout(w);
            session.addObserver(this); // register as Observer
            refreshSets();
            progressLabel.setText("Started: " + session);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void refreshSets() {
        setModel.clear();
        if (session != null) {
            for (ExerciseSet s : session.getSets()) {
                setModel.addElement(s);
            }
        }
    }

    private void markComplete() {
        if (session == null || session.isFinished()) {
            JOptionPane.showMessageDialog(this, "Start a workout first (or session already finished)");
            return;
        }
        int idx = setList.getSelectedIndex();
        if (idx < 0) {
            JOptionPane.showMessageDialog(this, "Select a set");
            return;
        }
        ExerciseSet set = session.getSets().get(idx);
        if (set.isCompleted()) {
            JOptionPane.showMessageDialog(this, "Already completed");
            return;
        }

        String repsStr = JOptionPane.showInputDialog(this,
                "Actual reps (target " + set.getWorkoutExercise().getTargetReps() + "):",
                String.valueOf(set.getWorkoutExercise().getTargetReps()));
        if (repsStr == null)
            return;

        Integer weight = set.getWorkoutExercise().getTargetWeight();
        if (weight != null) {
            String wStr = JOptionPane.showInputDialog(this,
                    "Actual weight (lb):", String.valueOf(weight));
            if (wStr == null)
                return;
            try {
                weight = Integer.parseInt(wStr.trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid weight");
                return;
            }
        }

        try {
            int reps = Integer.parseInt(repsStr.trim());
            // Subject notifies this Observer
            session.markSetComplete(idx, reps, weight);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number");
        }
    }

    private void finishSession() {
        if (session == null) {
            JOptionPane.showMessageDialog(this, "No active session");
            return;
        }
        if (!session.isFinished()) {
            int conf = JOptionPane.showConfirmDialog(this,
                    "Not all sets completed. Save anyway?", "Confirm",
                    JOptionPane.YES_NO_OPTION);
            if (conf != JOptionPane.YES_OPTION)
                return;
        }
        facade.finishAndSaveSession(session);
        JOptionPane.showMessageDialog(this, "Session saved to history!");
        session = null;
        setModel.clear();
        progressLabel.setText("Session saved. Start another if you like.");
    }

    // ---- Observer callbacks (pushed by WorkoutSession) ----

    @Override
    public void onSetCompleted(WorkoutSession s, ExerciseSet set) {
        SwingUtilities.invokeLater(() -> {
            refreshSets();
            progressLabel.setText("Progress: " + s.getCompletedCount() + " / " + s.getSets().size()
                    + "  |  Last: " + set);
        });
    }

    @Override
    public void onSessionFinished(WorkoutSession s) {
        SwingUtilities.invokeLater(() -> {
            refreshSets();
            progressLabel.setText("All sets done! Click 'Finish & Save to History'");
            JOptionPane.showMessageDialog(this, "Congratulations - workout complete!");
        });
    }
}