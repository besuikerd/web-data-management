package wdm;

/**
 * Created by maarten on 12-5-15.
 */
public class TPEStackAttribute extends TPEStack {

    public TPEStackAttribute(TPEStack parent, Matcher matcher, boolean selected){
        super(parent, matcher, selected);
    }

    @Override
    public boolean parentHasMatch() {
        return this.getParent().top() != null && this.getParent().top().getState() == MatchState.OPEN;
    }

    @Override
    public void createMatch(int pre) {
        System.out.println("Pushing attribute match: " + matcher.toString());
        Match m = new Match(pre, this.getParent().top(), this);
        this.push(m);
    }
}
