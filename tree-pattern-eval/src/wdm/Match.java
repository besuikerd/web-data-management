package wdm;

import java.util.Map;

public class Match {
    public static final int STATE_X = 0;

    private int start;
    private MatchState state;
    private Match parent;
    private Map<PatternNode, Match[]> children;
    private TPEStack stack;

    public MatchState getState() {
        return state;
    }


}
