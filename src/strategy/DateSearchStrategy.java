package strategy;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import model.WorkoutSession;

/**
 * Matches sessions whose date (yyyy-MM-dd) contains the query.
 * Empty query returns all sessions.
 */
public class DateSearchStrategy implements SearchStrategy {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public List<WorkoutSession> search(List<WorkoutSession> sessions, String query) {
        List<WorkoutSession> results = new ArrayList<>();
        String needle = query == null ? "" : query.trim();
        for (WorkoutSession session : sessions) {
            String sessionDate = session.getDate().format(DATE_FORMAT);
            if (needle.isEmpty() || sessionDate.contains(needle)) {
                results.add(session);
            }
        }
        return results;
    }
}
