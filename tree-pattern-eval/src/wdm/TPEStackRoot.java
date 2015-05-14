package wdm;

import wdm.match.Match;
import wdm.matcher.Matcher;
import wdm.matcher.MatcherAny;
import wdm.matcher.MatcherString;

/**
 * Created by maarten on 12-5-15.
 */
public class TPEStackRoot extends TPEStack {

    public TPEStackRoot(Matcher matcher, boolean selected) {
        super(null, matcher, selected);
    }

    public TPEStackRoot(Matcher matcher) {
        this(matcher, false);
    }

    public TPEStackRoot() {
        this(new MatcherAny());
    }

    public TPEStackRoot(String label) {
        this(new MatcherString(label));
    }


    @Override
    public boolean parentHasMatch() {
        return true;
    }

    @Override
    public Match createMatch(int pre, String label) {
        Match m = matcher.createMatch(this, null, pre, label);
        push(m);
        return m;
    }
}
