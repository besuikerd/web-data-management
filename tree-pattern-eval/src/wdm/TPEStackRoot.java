package wdm;

/**
 * Created by maarten on 12-5-15.
 */
public class TPEStackRoot extends TPEStack {

    TPEStackRoot(Matcher matcher, boolean selected) {
        super(null, matcher, selected);
    }

    @Override
    public boolean parentHasMatch() {
        return true;
    }

    @Override
    public void createMatch(int pre) {
        System.out.println("pushing root " + matcher.toString());
        Match m = new Match(pre, null, this);
        this.push(m);
    }

    @Override
    public void createFailedMatch() {
        System.out.println("pushing failed root match " + matcher.toString());
        Match m = new FailedMatch(null, this);
        this.push(m);
    }
}
