package wdm.match;

import wdm.TPEStack;
import wdm.TPEStackAttribute;
import wdm.xml.XMLEntity;

import java.util.*;
import java.util.stream.Collectors;

public class Match {
    public static final int STATE_X = 0;

    private int start;
    private String label;
    private MatchState state;
    private Match parent;
    private TPEStack stack;
    private Map<TPEStack, List<Match>> children;
    private String content = null;
    private XMLEntity xml = null;

    public Match(int start, String label, Match parent, TPEStack stack){
        this.start = start;
        this.label = label;
        this.parent = parent;
        this.stack = stack;
        this.state = MatchState.OPEN;
        children = new LinkedHashMap<>();
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

    public void removeChild(TPEStack s, int pre) {
        List<Match> list = children.get(s);
        for(int i = 0 ; i < list.size() ; i++){
            Match m = list.get(i);
            if(m.getStart() == pre){
                list.remove(i);
                if(list.isEmpty()){
                    children.remove(s);
                }
                break;
            }
        }
    }

    public void close(){
        this.state = MatchState.CLOSED;
    }

    @Override
    public String toString() {
//        return String.format("Match(%d, %s %s)",  start, stack.getName(), parent == null ? "ROOT" : parent.toString());
//        return String.format("Match(start: %d, name: %s, parent name: %s)",  start, stack.getName(), parent == null ? "ROOT" : parent.stack.getName());

        return String.format("Match(start: %d, TPEStack: %s, label: %s, content: %.20s)",  start, stack.toString(), label, content);
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

    public String getLabel() {
        return label;
    }

    public String toXml() {
        StringBuilder builder = new StringBuilder();
        bufferedToXml(builder);
        return builder.toString();
    }

    private void bufferedToXml(StringBuilder builder){

        builder.append("<" + label);
        Iterator<Map.Entry<TPEStack, List<Match>>> iterator = children.entrySet().iterator();
        //grab attributes
        while(iterator.hasNext()){
            Map.Entry<TPEStack, List<Match>> next = iterator.next();
            if(next.getKey() instanceof TPEStackAttribute){
                for(Match m : next.getValue()){
                    builder.append(m.label + " = " + m.content);
                }
            } else{
                break;
            }
        }
        builder.append(">\n");
        while(iterator.hasNext()){
            while(iterator.hasNext()){
                for(Match m : iterator.next().getValue()){
                    m.bufferedToXml(builder);
                }
            }
        }
        builder.append("</" + label + ">");
    }


    public XMLEntity getXml() {
        return xml;
    }

    public void setXml(XMLEntity xml) {
        this.xml = xml;
    }
}
