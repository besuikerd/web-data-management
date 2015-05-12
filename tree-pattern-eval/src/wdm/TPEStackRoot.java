package wdm;

/**
 * Created by maarten on 12-5-15.
 */
public class TPEStackRoot extends TPEStack {

    TPEStackRoot(Matcher matcher) {
        super(null, matcher);
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
}
