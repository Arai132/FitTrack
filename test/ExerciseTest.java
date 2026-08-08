import model.Exercise;

public class ExerciseTest {

    public static void main(String[] args) {
        TestSupport.Suite s = TestSupport.suite("ExerciseTest");

        s.run("constructor rejects null name", () -> TestSupport.assertThrows(IllegalArgumentException.class,
                () -> new Exercise(null), "should reject null name"));

        s.run("constructor rejects blank name", () -> TestSupport.assertThrows(IllegalArgumentException.class,
                () -> new Exercise("   "), "should reject blank name"));

        s.run("single-arg constructor leaves category null", () -> {
            Exercise e = new Exercise("Push Up");
            TestSupport.assertEquals("Push Up", e.getName(), "name");
            TestSupport.assertNull(e.getCategory(), "category");
        });

        s.run("two-arg constructor sets category", () -> {
            Exercise e = new Exercise("Squat", "Legs");
            TestSupport.assertEquals("Squat", e.getName(), "name");
            TestSupport.assertEquals("Legs", e.getCategory(), "category");
        });

        s.run("constructor assigns a non-blank id", () -> {
            Exercise e = new Exercise("Squat");
            TestSupport.assertTrue(e.getId() != null && !e.getId().isBlank(), "id should be generated");
        });

        s.run("setName rejects blank", () -> {
            Exercise e = new Exercise("Squat");
            TestSupport.assertThrows(IllegalArgumentException.class, () -> e.setName(""), "should reject blank name");
        });

        s.run("setName updates name", () -> {
            Exercise e = new Exercise("Squat");
            e.setName("Front Squat");
            TestSupport.assertEquals("Front Squat", e.getName(), "name after setName");
        });

        s.run("setCategory allows null", () -> {
            Exercise e = new Exercise("Squat", "Legs");
            e.setCategory(null);
            TestSupport.assertNull(e.getCategory(), "category after setCategory(null)");
        });

        s.run("equals/hashCode are identity-based via id, not name", () -> {
            Exercise a = new Exercise("Squat");
            Exercise b = new Exercise("Squat");
            TestSupport.assertFalse(a.equals(b), "two exercises with same name but different id should not be equal");
            TestSupport.assertTrue(a.equals(a), "an exercise should equal itself");
            TestSupport.assertEquals(a.hashCode(), a.hashCode(), "hashCode should be stable");
        });

        s.run("equals returns false for null and other types", () -> {
            Exercise a = new Exercise("Squat");
            TestSupport.assertFalse(a.equals(null), "should not equal null");
            TestSupport.assertFalse(a.equals("Squat"), "should not equal a non-Exercise");
        });

        s.run("toString includes name and category", () -> {
            Exercise e = new Exercise("Squat", "Legs");
            String text = e.toString();
            TestSupport.assertTrue(text.contains("Squat") && text.contains("Legs"), "toString should mention name/category");
        });

        s.summary();
    }
}
