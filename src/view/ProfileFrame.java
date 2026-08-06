package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

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
import javax.swing.SwingConstants;

import controller.MainController;
import facade.FitTrackFacade;
import model.Profile;

/**
 * Multi-profile manager.
 * List all profiles, create new ones, switch, update, delete.
 */
public class ProfileFrame extends JFrame {
    private final FitTrackFacade facade;
    private final MainController mainController;

    private final DefaultListModel<Profile> listModel = new DefaultListModel<>();
    private final JList<Profile> profileList = new JList<>(listModel);

    private final JTextField nameField = new JTextField(14);
    private final JTextField heightField = new JTextField(6);
    private final JTextField weightField = new JTextField(6);
    private final JLabel currentLabel = new JLabel(" ", SwingConstants.CENTER);

    public ProfileFrame(FitTrackFacade facade, MainController mainController) {
        super("FitTrack - Profiles");
        this.facade = facade;
        this.mainController = mainController;

        setSize(560, 360);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        currentLabel.setBorder(BorderFactory.createEmptyBorder(6, 6, 4, 6));
        add(currentLabel, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 2, 10, 0));
        center.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        profileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane listScroll = new JScrollPane(profileList);
        listScroll.setBorder(BorderFactory.createTitledBorder("All Profiles"));
        center.add(listScroll);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Profile Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        form.add(nameField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        form.add(new JLabel("Height (cm):"), gbc);
        gbc.gridx = 1;
        form.add(heightField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        form.add(new JLabel("Weight (kg):"), gbc);
        gbc.gridx = 1;
        form.add(weightField, gbc);
        center.add(form);
        add(center, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout());
        JButton btnCreate = new JButton("Create");
        JButton btnSwitch = new JButton("Switch To Selected");
        JButton btnUpdate = new JButton("Update Selected");
        JButton btnDelete = new JButton("Delete Selected");
        JButton btnClose = new JButton("Close");

        btnCreate.addActionListener(e -> createProfile());
        btnSwitch.addActionListener(e -> switchProfile());
        btnUpdate.addActionListener(e -> updateProfile());
        btnDelete.addActionListener(e -> deleteProfile());
        btnClose.addActionListener(e -> dispose());

        buttons.add(btnCreate);
        buttons.add(btnSwitch);
        buttons.add(btnUpdate);
        buttons.add(btnDelete);
        buttons.add(btnClose);
        add(buttons, BorderLayout.SOUTH);

        profileList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Profile sel = profileList.getSelectedValue();
                if (sel != null) {
                    nameField.setText(sel.getName());
                    heightField.setText(String.valueOf(sel.getHeightCm()));
                    weightField.setText(String.valueOf(sel.getCurrentWeightKg()));
                }
            }
        });

        refreshList();
        updateCurrentLabel();
    }

    private void refreshList() {
        listModel.clear();
        for (Profile p : facade.getAllProfiles()) {
            listModel.addElement(p);
        }
        Profile current = facade.getCurrentProfile();
        if (current != null) {
            profileList.setSelectedValue(current, true);
        }
    }

    private void updateCurrentLabel() {
        Profile p = facade.getCurrentProfile();
        currentLabel.setText(p == null
                ? "No profile selected"
                : "Current profile: " + p.getName());
    }

    private void createProfile() {
        try {
            String name = nameField.getText().trim();
            double height = Double.parseDouble(heightField.getText().trim());
            double weight = Double.parseDouble(weightField.getText().trim());
            facade.createProfile(name, height, weight);
            refreshList();
            updateCurrentLabel();
            mainController.refreshMain();
            nameField.setText("");
            heightField.setText("");
            weightField.setText("");
            JOptionPane.showMessageDialog(this, "Profile created and selected");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter valid numbers for height/weight");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void switchProfile() {
        Profile sel = profileList.getSelectedValue();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Select a profile first");
            return;
        }
        facade.switchProfile(sel);
        updateCurrentLabel();
        mainController.refreshMain();
        JOptionPane.showMessageDialog(this, "Switched to: " + sel.getName());
    }

    private void updateProfile() {
        Profile sel = profileList.getSelectedValue();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Select a profile first");
            return;
        }
        Profile previous = facade.getCurrentProfile();
        facade.switchProfile(sel);
        try {
            String name = nameField.getText().trim();
            double height = Double.parseDouble(heightField.getText().trim());
            double weight = Double.parseDouble(weightField.getText().trim());
            facade.updateProfile(name, height, weight);
            refreshList();
            mainController.refreshMain();
            JOptionPane.showMessageDialog(this, "Profile updated");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter valid numbers");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } finally {
            if (previous != null && previous != sel) {
                facade.switchProfile(previous);
            }
            updateCurrentLabel();
        }
    }

    private void deleteProfile() {
        Profile sel = profileList.getSelectedValue();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Select a profile first");
            return;
        }
        int conf = JOptionPane.showConfirmDialog(this,
                "Delete profile \"" + sel.getName() + "\" and ALL its data?",
                "Confirm", JOptionPane.YES_NO_OPTION);
        if (conf == JOptionPane.YES_OPTION) {
            facade.deleteProfile(sel);
            refreshList();
            updateCurrentLabel();
            mainController.refreshMain();
            nameField.setText("");
            heightField.setText("");
            weightField.setText("");
        }
    }
}