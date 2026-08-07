package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

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
import javax.swing.ListSelectionModel;

import facade.FitTrackFacade;
import model.Exercise;

public class ExerciseFrame extends JFrame {
    private final FitTrackFacade facade;
    private final DefaultListModel<Exercise> listModel = new DefaultListModel<>();
    private final JList<Exercise> exerciseList = new JList<>(listModel);

    private final JTextField nameField = new JTextField(14);
    private final JTextField categoryField = new JTextField(10);

    public ExerciseFrame(FitTrackFacade facade) {
        super("FitTrack - Exercises");
        this.facade = facade;

        setSize(520, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.setBorder(BorderFactory.createTitledBorder(
                "Create / Edit  (profile: " + facade.getCurrentProfile().getName() + ")"));
        form.add(new JLabel("Name:"));
        form.add(nameField);
        form.add(new JLabel("Category:"));
        form.add(categoryField);
        add(form, BorderLayout.NORTH);

        exerciseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(exerciseList);
        scroll.setBorder(BorderFactory.createTitledBorder("Exercises for current profile"));
        add(scroll, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout());
        JButton btnCreate = new JButton("Create");
        JButton btnUpdate = new JButton("Update Selected");
        JButton btnDelete = new JButton("Delete Selected");
        JButton btnBack = new JButton("Back");

        btnCreate.addActionListener(e -> createExercise());
        btnUpdate.addActionListener(e -> updateExercise());
        btnDelete.addActionListener(e -> deleteExercise());
        btnBack.addActionListener(e -> dispose());

        buttons.add(btnCreate);
        buttons.add(btnUpdate);
        buttons.add(btnDelete);
        buttons.add(btnBack);
        add(buttons, BorderLayout.SOUTH);

        exerciseList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Exercise sel = exerciseList.getSelectedValue();
                if (sel != null) {
                    nameField.setText(sel.getName());
                    categoryField.setText(sel.getCategory() != null ? sel.getCategory() : "");
                }
            }
        });

        refreshList();
    }

    private void refreshList() {
        listModel.clear();
        for (Exercise e : facade.getAllExercises()) {
            listModel.addElement(e);
        }
    }

    private void createExercise() {
        try {
            String name = nameField.getText().trim();
            String cat = categoryField.getText().trim();
            facade.createExercise(name, cat.isEmpty() ? null : cat);
            refreshList();
            nameField.setText("");
            categoryField.setText("");
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void updateExercise() {
        Exercise sel = exerciseList.getSelectedValue();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Select an exercise first");
            return;
        }
        try {
            String name = nameField.getText().trim();
            String cat = categoryField.getText().trim();
            facade.updateExercise(sel, name, cat.isEmpty() ? null : cat);
            refreshList();
            JOptionPane.showMessageDialog(this, "Updated");
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void deleteExercise() {
        Exercise sel = exerciseList.getSelectedValue();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Select an exercise first");
            return;
        }
        int conf = JOptionPane.showConfirmDialog(this,
                "Delete \"" + sel.getName() + "\"? It will also be removed from all workouts.",
                "Confirm", JOptionPane.YES_NO_OPTION);
        if (conf == JOptionPane.YES_OPTION) {
            facade.deleteExercise(sel);
            refreshList();
            nameField.setText("");
            categoryField.setText("");
        }
    }
}