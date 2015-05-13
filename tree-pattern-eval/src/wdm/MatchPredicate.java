package wdm;

@FunctionalInterface
public interface MatchPredicate {
    boolean matches(String label, String text);
}
