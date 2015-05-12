package wdm;

/**
 * Created by maarten on 12-5-15.
 */
public class MatcherOpt extends Matcher {
    private String pattern;

    MatcherOpt(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public boolean isMatch(String name) {
        return true;
    }

    @Override
    public String toString() {
        return "Opt[" + this.pattern + "]";
    }
}
