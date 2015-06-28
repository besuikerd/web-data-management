package wdm.matcher;

/**
 * Created by maarten on 12-5-15.
 */
public class MatcherAny extends Matcher {

    @Override
    public boolean preMatch(String name) {
        return true;
    }

    @Override
    public String toString() {
        return "*";
    }
}
