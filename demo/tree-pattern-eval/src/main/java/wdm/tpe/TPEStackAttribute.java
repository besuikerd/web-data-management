package wdm.tpe;

import wdm.match.MatchState;
import wdm.matcher.Matcher;

/**
 * Created by maarten on 12-5-15.
 */
public class TPEStackAttribute extends TPEStack {

    public TPEStackAttribute(TPEStack parent, Matcher matcher){
        super(parent, matcher, false);
    }

    @Override
    public boolean parentHasMatch() {
        return this.getParent().top() != null && this.getParent().top().getState() == MatchState.OPEN;
    }
}
