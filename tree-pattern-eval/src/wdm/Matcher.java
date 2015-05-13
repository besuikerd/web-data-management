package wdm;

import java.util.Map;

/**
 * Created by maarten on 12-5-15.
 */
public abstract class Matcher {

    public Match createMatch(TPEStack stack, Match parentMatch, int pre, String label){
        return new Match(pre, label, parentMatch, stack);
    }

    public abstract boolean preMatch(String name);

    public boolean postMatch(String name, String text){
        return true;
    }
}
