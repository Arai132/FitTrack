public class AllTests {

    public static void main(String[] args) {
        ExerciseTest.main(args);
        ExerciseSetTest.main(args);
        WorkoutExerciseTest.main(args);
        WorkoutTest.main(args);
        ProfileTest.main(args);
        WorkoutSessionTest.main(args);
        DataStoreTest.main(args);
        SerializedObjectStoreTest.main(args);
        SearchStrategyTest.main(args);
        ReportTest.main(args);
        FitTrackFacadeTest.main(args);

        TestSupport.printGrandTotal();
        if (!TestSupport.allPassed()) {
            System.exit(1);
        }
    }
}
