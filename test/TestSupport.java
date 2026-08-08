import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Minimal hand-rolled test scaffolding: no external test framework, just
 * plain Java. Each test class builds a Suite, runs named checks against
 * it, and prints a pass/fail summary. AllTests aggregates every suite.
 */
public final class TestSupport {

    private static int grandTotal = 0;
    private static int grandFailed = 0;

    private TestSupport() {
    }

    public static Suite suite(String name) {
        return new Suite(name);
    }

    public static boolean allPassed() {
        return grandFailed == 0;
    }

    public static void printGrandTotal() {
        System.out.println();
        System.out.println("========================================");
        System.out.println("TOTAL: " + (grandTotal - grandFailed) + "/" + grandTotal + " passed");
        System.out.println("========================================");
    }

    public static final class Suite {
        private final String name;
        private int total = 0;
        private int failed = 0;
        private final List<String> failures = new ArrayList<>();

        private Suite(String name) {
            this.name = name;
        }

        public void run(String testName, Runnable test) {
            total++;
            grandTotal++;
            try {
                test.run();
                System.out.println("  PASS  " + testName);
            } catch (Throwable t) {
                failed++;
                grandFailed++;
                failures.add(testName + " -> " + t);
                System.out.println("  FAIL  " + testName + "  (" + t + ")");
            }
        }

        public boolean summary() {
            System.out.println(name + ": " + (total - failed) + "/" + total + " passed");
            System.out.println();
            return failed == 0;
        }
    }

    // ----- assertions -----

    public static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    public static void assertFalse(boolean condition, String message) {
        assertTrue(!condition, message);
    }

    public static void assertEquals(Object expected, Object actual, String message) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(message + " (expected=<" + expected + "> actual=<" + actual + ">)");
        }
    }

    public static void assertNull(Object value, String message) {
        if (value != null) {
            throw new AssertionError(message + " (was <" + value + ">)");
        }
    }

    public static void assertNotNull(Object value, String message) {
        if (value == null) {
            throw new AssertionError(message);
        }
    }

    public static void assertThrows(Class<? extends Throwable> expectedType, Runnable action, String message) {
        try {
            action.run();
        } catch (Throwable t) {
            if (expectedType.isInstance(t)) {
                return;
            }
            throw new AssertionError(message + " (threw " + t.getClass().getSimpleName()
                    + " instead of " + expectedType.getSimpleName() + ")");
        }
        throw new AssertionError(message + " (no exception thrown)");
    }
}
