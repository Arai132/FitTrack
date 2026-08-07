package report;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * One completed data point for a single exercise, pulled from a past
 * WorkoutSession, so progress over time can be reviewed per exercise.
 */
public class ExerciseTrendPoint implements Serializable {

    private static final long serialVersionUID = 1L;

    private final LocalDateTime date;
    private final int actualReps;
    private final Integer actualWeight;

    public ExerciseTrendPoint(LocalDateTime date, int actualReps, Integer actualWeight) {
        this.date = date;
        this.actualReps = actualReps;
        this.actualWeight = actualWeight;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public int getActualReps() {
        return actualReps;
    }

    public Integer getActualWeight() {
        return actualWeight;
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append(date).append(": ").append(actualReps).append(" reps");
        if (actualWeight != null) {
            text.append(" @ ").append(actualWeight).append(" lb");
        }
        return text.toString();
    }
}
