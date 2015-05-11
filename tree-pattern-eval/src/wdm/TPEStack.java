package wdm;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class TPEStack {
    private String name;
    private Stack<Match> matches;
    private TPEStack parent;
    private List<TPEStack> children;

    public TPEStack(TPEStack parent, String name){
        this.parent = parent;
        this.name = name;
        this.matches = new Stack<>();
        this.children = new ArrayList<>();
        if(parent != null){
            parent.addChild(this);
        }
    }

    public String getName() {
        return name;
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

    /*
    public List<TPEStack> getDescendantStacks(){
        List<TPEStack> result = new LinkedList<>();
        result.addAll(children);
        for(TPEStack child : children){
            result.addAll(child.getDescendantStacks());
        }
        return result;
    }
    */

    public List<TPEStack> getDescendantStacks(){
        List<TPEStack> result = new LinkedList<>();
        result.add(this);
        for(TPEStack child : children){
            result.addAll(child.getDescendantStacks());
        }
        return result;
    }

    public Match pop(){
        return matches.pop();
    }

    public Match top(){
        return matches.peek();
    }

    public void push(Match m){
        matches.push(m);
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", getName(), getChildren().toString());
    }
}
