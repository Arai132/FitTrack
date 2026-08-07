package report;

import java.io.Serializable;

/**
 * Aggregated stats across a profile's whole workout history
 * (UC9 - View Progress Summary).
 */
public class ProgressSummary implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int totalSessions;
    private final int completedSessions;
    private final int totalSetsLogged;

    public ProgressSummary(int totalSessions, int completedSessions, int totalSetsLogged) {
        this.totalSessions = totalSessions;
        this.completedSessions = completedSessions;
        this.totalSetsLogged = totalSetsLogged;
    }

    public int getTotalSessions() {
        return totalSessions;
    }

    public int getCompletedSessions() {
        return completedSessions;
    }

    public int getTotalSetsLogged() {
        return totalSetsLogged;
    }

    @Override
    public String toString() {
        return "ProgressSummary{sessions=" + totalSessions + ", completed=" + completedSessions
                + ", setsLogged=" + totalSetsLogged + "}";
    }
}
