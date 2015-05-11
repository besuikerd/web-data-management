package wdm;

import java.util.HashMap;
import java.util.Map;

public class Match {
    public static final int STATE_X = 0;

    private int start;
    private MatchState state;
    private Match parent;
    private TPEStack stack;

    //TODO ????
    private Map<TPEStack, Match[]> children;

    public Match(int start, Match parent, TPEStack stack){
        this.start = start;
        this.parent = parent;
        this.stack = stack;
        this.state = MatchState.OPEN;
        children = new HashMap<>();
    }


    public MatchState getState() {
        return state;
    }

    public int getStart() {
        return start;
    }

    public Map<TPEStack, Match[]> getChildren() {
        return children;
    }

    public void close(){
        this.state = MatchState.CLOSED;
    }
}
