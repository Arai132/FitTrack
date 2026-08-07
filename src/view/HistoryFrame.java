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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import facade.FitTrackFacade;
import model.WorkoutSession;
import strategy.DateSearchStrategy;
import strategy.NameSearchStrategy;
import strategy.SearchStrategy;

public class HistoryFrame extends JFrame {
    private final FitTrackFacade facade;
    private final DefaultListModel<WorkoutSession> listModel = new DefaultListModel<>();
    private final JList<WorkoutSession> historyList = new JList<>(listModel);
    private final JTextField queryField = new JTextField(14);
    private final JComboBox<String> strategyCombo = new JComboBox<>(new String[] { "By Name", "By Date" });

    public HistoryFrame(FitTrackFacade facade) {
        super("FitTrack - Workout History");
        this.facade = facade;

        setSize(540, 440);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder(
                "Search  (profile: " + facade.getCurrentProfile().getName() + ")"));
        searchPanel.add(new JLabel("Query:"));
        searchPanel.add(queryField);
        searchPanel.add(strategyCombo);

        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> doSearch());
        searchPanel.add(btnSearch);

        JButton btnShowAll = new JButton("Show All");
        btnShowAll.addActionListener(e -> showAll());
        searchPanel.add(btnShowAll);
        add(searchPanel, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(historyList);
        scroll.setBorder(BorderFactory.createTitledBorder("Sessions for current profile"));
        add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout());
        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dispose());
        bottom.add(btnClose);
        add(bottom, BorderLayout.SOUTH);

        showAll();
    }

    private void showAll() {
        listModel.clear();
        for (WorkoutSession s : facade.getHistory()) {
            listModel.addElement(s);
        }
    }

    private void doSearch() {
        String query = queryField.getText().trim();
        SearchStrategy strategy = "By Date".equals(strategyCombo.getSelectedItem())
                ? new DateSearchStrategy()
                : new NameSearchStrategy();
        List<WorkoutSession> results = facade.searchHistory(strategy, query);
        listModel.clear();
        for (WorkoutSession s : results) {
            listModel.addElement(s);
        }
    }
}