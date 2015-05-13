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

    @Override
    public void createMatch(int pre) {
        System.out.println("pushing " + matcher.toString());
        Match m = new Match(pre, this.getParent().top(), this);
        this.push(m);
    }

    @Override
    public void createFailedMatch() {
        System.out.println("pushing failed match" + matcher.toString());
        Match m = new FailedMatch(this.getParent().top(), this);
        this.push(m);
    }
}
