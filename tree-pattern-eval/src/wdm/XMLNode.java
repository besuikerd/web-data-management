package wdm;


import java.util.LinkedList;
import java.util.List;

public class XMLNode {
    private int index;
    private StringBuilder textBuilder;

    private List<XMLNode> children;

    public XMLNode(int index) {
        this.index = index;
        this.textBuilder = new StringBuilder();
        this.children = new LinkedList<>();
    }

    public void appendText(String text){
        textBuilder.append(text);
    }

    public String getText(){
        return textBuilder.toString();
    }

    public int getIndex(){
        return index;
    }
}
