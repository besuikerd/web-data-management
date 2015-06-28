package wdm.matcher;

/**
 * Created by maarten on 12-5-15.
 */
public class MatcherString extends Matcher {
    private String pattern;
    public MatcherString(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public boolean preMatch(String name) {
        return name.equals(pattern);
    }

    @Override
    public String toString() {
        return this.pattern;
    }
}
