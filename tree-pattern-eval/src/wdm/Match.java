package wdm;

import java.util.*;
import java.util.stream.Collectors;

public class Match {
    public static final int STATE_X = 0;

    private int start;
    private MatchState state;
    private Match parent;
    private TPEStack stack;
    private Map<TPEStack, List<Match>> children;

    private String content = null;

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
//        return String.format("Match(%d, %s %s)",  start, stack.getName(), parent == null ? "ROOT" : parent.toString());
//        return String.format("Match(start: %d, name: %s, parent name: %s)",  start, stack.getName(), parent == null ? "ROOT" : parent.stack.getName());

        return String.format("Match(start: %d, TPEStack: %s)",  start, stack.toString());
    }

    public List<List<Match>> getAllTuples() {
        List<List<Match>> result = new LinkedList<>();
        List<Match> li = new LinkedList<Match>();
        li.add(this);
        result.add(li);

        for(List<Match> childList : children.values()) { //Forception >.<
            List<List<Match>> temp = new LinkedList<>();
            for(Match child : childList) {
                for(List<Match> tuple : child.getAllTuples()) {
                    for(List<Match> resTuple : result) {
                        List<Match> newTuple = new ArrayList<Match>(tuple);
                        newTuple.addAll(resTuple);
                        temp.add(newTuple);
                    }
                }
            }
            result.removeAll(result);
            result.addAll(temp);
        }
        return result;
    }

    public List<List<Match>> getTuples() {
        return getAllTuples().stream().map(x -> x.stream().filter(y -> y.stack.isSelected()).collect(Collectors.toList())).collect(Collectors.toList());
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
