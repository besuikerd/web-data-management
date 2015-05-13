package wdm;

/**
 * Created by maarten on 12-5-15.
 */
public class TPEStackBranch extends TPEStack {

    public TPEStackBranch(TPEStack parent, Matcher matcher, boolean selected){
        super(parent, matcher, selected);
    }

    @Override
    public boolean parentHasMatch() {
        return this.getParent().top() != null && this.getParent().top().getState() == MatchState.OPEN;
    }
}
