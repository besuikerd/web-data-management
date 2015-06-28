package wdm.matcher;

@FunctionalInterface
public interface MatchPredicate {
    boolean matches(String label, String text);
}
