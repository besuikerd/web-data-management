package wdm;

/**
 * Created by maarten on 12-5-15.
 */
public class MatcherString extends Matcher {
    private String pattern;
    MatcherString(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public boolean isMatch(String name) {
        return name.equals(pattern);
    }

    @Override
    public String toString() {
        return this.pattern;
    }
}
