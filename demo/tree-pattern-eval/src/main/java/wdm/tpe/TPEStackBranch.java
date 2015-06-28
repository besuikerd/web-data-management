package wdm.tpe;

import wdm.match.MatchState;
import wdm.matcher.Matcher;
import wdm.matcher.MatcherString;

/**
 * Created by maarten on 12-5-15.
 */
public class TPEStackBranch extends TPEStack {

    public TPEStackBranch(TPEStack parent, Matcher matcher, boolean selected){
        super(parent, matcher, selected);
    }

    public TPEStackBranch(TPEStack parent, Matcher matcher){
        this(parent, matcher, false);
    }

    public TPEStackBranch(TPEStack parent, String label){
        this(parent, label, false);
    }
    public TPEStackBranch(TPEStack parent, String label, boolean selected){
        this(parent, new MatcherString(label), selected);
    }

    @Override
    public boolean parentHasMatch() {
        return this.getParent().top() != null && this.getParent().top().getState() == MatchState.OPEN;
    }
}
