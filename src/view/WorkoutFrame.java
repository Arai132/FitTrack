package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import facade.FitTrackFacade;
import model.Exercise;
import model.Workout;
import model.WorkoutExercise;

public class WorkoutFrame extends JFrame {
    private final FitTrackFacade facade;

    private final DefaultListModel<Workout> workoutModel = new DefaultListModel<>();
    private final JList<Workout> workoutList = new JList<>(workoutModel);

    private final DefaultListModel<WorkoutExercise> inWorkoutModel = new DefaultListModel<>();
    private final JList<WorkoutExercise> inWorkoutList = new JList<>(inWorkoutModel);

    private final DefaultListModel<Exercise> availableModel = new DefaultListModel<>();
    private final JList<Exercise> availableList = new JList<>(availableModel);

    private final JTextField nameField = new JTextField(12);
    private final JTextField setsField = new JTextField("3", 3);
    private final JTextField repsField = new JTextField("10", 3);
    private final JTextField weightField = new JTextField(4);

    public WorkoutFrame(FitTrackFacade facade) {
        super("FitTrack - Workouts");
        this.facade = facade;

        setSize(800, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBorder(BorderFactory.createTitledBorder(
                "Create Workout  (profile: " + facade.getCurrentProfile().getName() + ")"));
        top.add(new JLabel("Name:"));
        top.add(nameField);
        JButton btnCreate = new JButton("Create");
        btnCreate.addActionListener(e -> createWorkout());
        top.add(btnCreate);
        JButton btnArchive = new JButton("Delete Selected");
        btnArchive.addActionListener(e -> archiveWorkout());
        top.add(btnArchive);
        add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 3, 8, 8));
        center.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JScrollPane wScroll = new JScrollPane(workoutList);
        wScroll.setBorder(BorderFactory.createTitledBorder("Active Workouts"));
        center.add(wScroll);

        JScrollPane inScroll = new JScrollPane(inWorkoutList);
        inScroll.setBorder(BorderFactory.createTitledBorder("Exercises in Workout"));
        center.add(inScroll);

        JScrollPane availScroll = new JScrollPane(availableList);
        availScroll.setBorder(BorderFactory.createTitledBorder("Available Exercises"));
        center.add(availScroll);
        add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout());
        bottom.add(new JLabel("Sets:"));
        bottom.add(setsField);
        bottom.add(new JLabel("Reps:"));
        bottom.add(repsField);
        bottom.add(new JLabel("Weight (optional):"));
        bottom.add(weightField);

        JButton btnAdd = new JButton("<- Add Exercise");
        JButton btnRemove = new JButton("Remove Exercise ->");
        JButton btnBack = new JButton("Back");

        btnAdd.addActionListener(e -> addExercise());
        btnRemove.addActionListener(e -> removeExercise());
        btnBack.addActionListener(e -> dispose());

        bottom.add(btnAdd);
        bottom.add(btnRemove);
        bottom.add(btnBack);
        add(bottom, BorderLayout.SOUTH);

        workoutList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting())
                refreshInWorkout();
        });

        refreshWorkouts();
        refreshAvailable();
    }

    private void refreshWorkouts() {
        workoutModel.clear();
        for (Workout w : facade.getActiveWorkouts()) {
            workoutModel.addElement(w);
        }
    }

    private void refreshAvailable() {
        availableModel.clear();
        for (Exercise e : facade.getAllExercises()) {
            availableModel.addElement(e);
        }
    }

    private void refreshInWorkout() {
        inWorkoutModel.clear();
        Workout sel = workoutList.getSelectedValue();
        if (sel != null) {
            for (WorkoutExercise we : sel.getExercises()) {
                inWorkoutModel.addElement(we);
            }
        }
    }

    private void createWorkout() {
        try {
            facade.createWorkout(nameField.getText().trim());
            nameField.setText("");
            refreshWorkouts();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void archiveWorkout() {
        Workout sel = workoutList.getSelectedValue();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Select a workout");
            return;
        }
        int conf = JOptionPane.showConfirmDialog(this,
                "Delete \"" + sel.getName() + "\"?\nPast sessions stay in history.",
                "Confirm", JOptionPane.YES_NO_OPTION);
        if (conf == JOptionPane.YES_OPTION) {
            facade.deleteWorkout(sel);
            refreshWorkouts();
            inWorkoutModel.clear();
        }
    }

    private void addExercise() {
        Workout w = workoutList.getSelectedValue();
        Exercise e = availableList.getSelectedValue();
        if (w == null || e == null) {
            JOptionPane.showMessageDialog(this, "Select a workout AND an available exercise");
            return;
        }
        try {
            int sets = Integer.parseInt(setsField.getText().trim());
            int reps = Integer.parseInt(repsField.getText().trim());
            String wText = weightField.getText().trim();
            if (wText.isEmpty()) {
                facade.addExerciseToWorkout(w, e, sets, reps);
            } else {
                facade.addExerciseToWorkout(w, e, sets, reps, Integer.parseInt(wText));
            }
            refreshInWorkout();
            refreshWorkouts();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter valid numbers for sets/reps/weight");
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void removeExercise() {
        Workout w = workoutList.getSelectedValue();
        WorkoutExercise we = inWorkoutList.getSelectedValue();
        if (w == null || we == null) {
            JOptionPane.showMessageDialog(this, "Select a workout AND an exercise inside it");
            return;
        }
        facade.removeExerciseFromWorkout(w, we);
        refreshInWorkout();
        refreshWorkouts();
    }
}