package wdm.matcher;

import wdm.match.FailedMatch;
import wdm.match.Match;
import wdm.tpe.TPEStack;

/**
 * Created by maarten on 12-5-15.
 */
public class MatcherOpt extends Matcher {

    public static final int MATCH_FAILED = -1;

    private Matcher innerMatcher;

    public MatcherOpt(Matcher matcher) {
        this.innerMatcher = matcher;
    }

    MatcherOpt(String matcher){
        this(new MatcherString(matcher));
    }

    @Override
    public Match createMatch(TPEStack stack, Match parentMatch, int pre, String label) {
        return pre == MATCH_FAILED ? new FailedMatch(stack, label, parentMatch) : super.createMatch(stack, parentMatch, pre, label);
    }

    @Override
    public boolean preMatch(String name) {
        return innerMatcher.preMatch(name);
    }

    public Matcher getInnerMatcher() {
        return innerMatcher;
    }

    @Override
    public String toString() {
        return "Opt[" + this.innerMatcher.toString() + "]";
    }
}
