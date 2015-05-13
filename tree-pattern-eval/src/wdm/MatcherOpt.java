package wdm;

/**
 * Created by maarten on 12-5-15.
 */
public class MatcherOpt extends Matcher {

    public static final int MATCH_FAILED = -1;

    private Matcher pattern;

    MatcherOpt(Matcher matcher) {
        this.pattern = matcher;
    }

    MatcherOpt(String matcher){
        this(new MatcherString(matcher));
    }

    @Override
    public Match createMatch(TPEStack stack, Match parentMatch, int pre, String label) {
        return pre == MATCH_FAILED ? new FailedMatch(stack, parentMatch) : super.createMatch(stack, parentMatch, pre, label);
    }

    @Override
    public boolean preMatch(String name) {
        return pattern.preMatch(name);
    }

    @Override
    public String toString() {
        return "Opt[" + this.pattern.toString() + "]";
    }
}
