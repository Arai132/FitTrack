package view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import controller.MainController;
import facade.FitTrackFacade;
import model.Profile;

public class MainFrame extends JFrame {
    private final FitTrackFacade facade;
    private final MainController controller;
    private final JLabel statusLabel;

    public MainFrame(FitTrackFacade facade, MainController controller) {
        super("FitTrack - Main Menu");
        this.facade = facade;
        this.controller = controller;
        controller.setMainFrame(this);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 420);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("FitTrack", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new GridLayout(0, 1, 8, 8));
        buttons.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        JButton btnProfiles = new JButton("Profiles (Create / Switch / Edit / Delete)");
        JButton btnExercises = new JButton("Exercises (Create / View / Edit / Delete)");
        JButton btnWorkouts = new JButton("Workouts (Create / Edit / Add-Remove Exercises)");
        JButton btnStart = new JButton("Start & Track Workout");
        JButton btnHistory = new JButton("Workout History (View / Search)");

        btnProfiles.addActionListener(e -> controller.openProfileManager());
        btnExercises.addActionListener(e -> controller.openExercises());
        btnWorkouts.addActionListener(e -> controller.openWorkouts());
        btnStart.addActionListener(e -> controller.openActiveWorkout());
        btnHistory.addActionListener(e -> controller.openHistory());

        buttons.add(btnProfiles);
        buttons.add(btnExercises);
        buttons.add(btnWorkouts);
        buttons.add(btnStart);
        buttons.add(btnHistory);
        add(buttons, BorderLayout.CENTER);

        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(8, 8, 12, 8));
        add(statusLabel, BorderLayout.SOUTH);

        refreshStatus();
    }

    public void refreshStatus() {
        Profile p = facade.getCurrentProfile();
        int profileCount = facade.getAllProfiles().size();
        if (p == null) {
            statusLabel.setText("No profile selected  |  Total profiles: " + profileCount);
        } else {
            statusLabel.setText("Current: " + p.getName()
                    + "  |  Exercises: " + facade.getAllExercises().size()
                    + "  |  Workouts: " + facade.getActiveWorkouts().size()
                    + "  |  History: " + facade.getHistory().size()
                    + "  |  Profiles: " + profileCount);
        }
    }
}