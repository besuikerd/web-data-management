package wdm;

/**
 * Created by maarten on 12-5-15.
 */
public class MatcherOpt extends Matcher {
    private Matcher pattern;

    MatcherOpt(Matcher matcher) {
        this.pattern = matcher;
    }

    MatcherOpt(String matcher){
        this(new MatcherString(matcher));
    }

    @Override
    public boolean isMatch(String name) {
        return pattern.isMatch(name);
    }

    @Override
    public String toString() {
        return "Opt[" + this.pattern.toString() + "]";
    }
}
