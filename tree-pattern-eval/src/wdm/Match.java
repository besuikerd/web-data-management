package wdm;

import java.util.*;

public class Match {
    public static final int STATE_X = 0;

    private int start;
    private MatchState state;
    private Match parent;
    private TPEStack stack;
    private Map<TPEStack, List<Match>> children;

    public Match(int start, Match parent, TPEStack stack){
        this.start = start;
        this.parent = parent;
        this.stack = stack;
        this.state = MatchState.OPEN;
        children = new HashMap<>();
        if(parent != null) {
            parent.addChild(stack, this);
        }
    }

    public void addChild(TPEStack stack, Match m) {
        List<Match> matches = children.get(stack);
        if(matches == null){
            matches = new LinkedList<>();
            children.put(stack, matches);
        }
        matches.add(m);
    }

    public MatchState getState() {
        return state;
    }

    public int getStart() {
        return start;
    }

    public Match getParent() {
        return parent;
    }

    public Map<TPEStack, List<Match>> getChildren() {
        return children;
    }

    public void close(){
        this.state = MatchState.CLOSED;
    }

    @Override
    public String toString() {
        return String.format("Match(%d, %s %s)",  start, stack.getName(), parent == null ? "ROOT" : parent.toString());
    }
}
