package wdm;

public class MatcherPredicate extends Matcher{
    private Matcher innerMatcher;
    private MatchPredicate predicate;

    public MatcherPredicate(Matcher innerMatcher, MatchPredicate predicate) {
        this.innerMatcher = innerMatcher;
        this.predicate = predicate;
    }

    public MatcherPredicate(Matcher innerMatcher, String text) {
        this(innerMatcher, (label, txt) -> txt.equals(text));
    }

    public MatcherPredicate(String label, String text) {
        this(new MatcherString(label), text);
    }

    @Override
    public boolean preMatch(String name) {
        return innerMatcher.preMatch(name);
    }

    @Override
    public boolean postMatch(String name, String text) {
        return predicate.matches(name, text) && innerMatcher.postMatch(name, text);
    }

    @Override
    public String toString() {
        return "MatcherPredicate";
    }
}
