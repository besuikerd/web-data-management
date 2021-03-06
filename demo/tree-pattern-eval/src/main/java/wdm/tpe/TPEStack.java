package wdm.tpe;

import wdm.match.Match;
import wdm.match.MatchState;
import wdm.matcher.Matcher;
import wdm.matcher.MatcherOpt;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

abstract public class TPEStack {
    private Stack<Match> matches;
    private TPEStack parent;
    private List<TPEStack> children;
    protected Matcher matcher;
    protected boolean selected;

    public TPEStack(TPEStack parent, Matcher matcher, boolean selected){
        this.parent = parent;
        this.matcher = matcher;
        this.matches = new Stack<>();
        this.children = new ArrayList<>();
        if(parent != null){
            parent.addChild(this);
        }
        this.selected = selected;
    }

    public List<TPEStack> getChildren(){
        return children;
    }

    public TPEStack getParent() {
        return parent;
    }

    public void addChild(TPEStack child){
        children.add(child);
    }

    public List<TPEStack> getDescendantStacks(){
        List<TPEStack> result = new LinkedList<>();
        for(TPEStack child : children){
            result.addAll(child.getDescendantStacks());
        }
        result.add(this);
        return result;
    }

    public Match pop(){
        if(matches.isEmpty()) {
            return null;
        } else {
            return matches.pop();
        }
    }

    public Match top(){
        if(matches.isEmpty()) {
            return null;
        } else {
            return matches.peek();
        }
    }

    public void push(Match m){
        matches.push(m);
    }

    @Override
    public String toString() {
//        return String.format("%s(%s)", matcher.toString(), getChildren().toString());
        return String.format("TPEStack(%s)", matcher.toString());
    }

    public Stack<Match> getMatches() {
        return matches;
    }

    public boolean isMatch(String label) {
        return matcher.preMatch(label);
    }
    public boolean hasOpenMatch(int pre) {
        return this.top() != null && this.top().getState() == MatchState.OPEN && this.top().getStart() == pre;
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isOptional(){
        return matcher instanceof MatcherOpt;
    }

    public Match createMatch(int pre, String label){
        Match m = matcher.createMatch(this, this.parent.top(), pre, label);
        push(m);
        return m;
    }

    abstract public boolean parentHasMatch();

    public TPEStack select(){
        this.selected = true;
        return this;
    }

    public Matcher getMatcher() {
        return matcher;
    }
}
