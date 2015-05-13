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
    public Match createMatch(int pre, String label) {
        Match m = matcher.createMatch(this, null, pre, label);
        push(m);
        return m;
    }
}
