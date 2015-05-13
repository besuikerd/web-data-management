package wdm;


import java.util.LinkedList;
import java.util.List;
import org.xml.sax.Attributes;

public class XMLNode implements XMLEntity {
    private int index;
    private StringBuilder textBuilder;
    private String label;
    private List<XMLNode> children;
    private Attributes attributes;

    public XMLNode(int index, String label, Attributes attributes) {
        this.index = index;
        this.label = label;
        this.textBuilder = new StringBuilder();
        this.children = new LinkedList<>();
        this.attributes = attributes;
    }

    public void appendText(String text){
        textBuilder.append(text);
    }

    @Override
    public String getText(){
        return textBuilder.toString();
    }

    @Override
    public int getIndex(){
        return index;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public void addChild(XMLNode node) {
        children.add(node);
    }

    public Attributes getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        return "XMLNode(" + label + ", "  + getText() + ", " + children + ")";
    }
}
