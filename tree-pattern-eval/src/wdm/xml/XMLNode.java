package wdm.xml;


import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.xml.sax.Attributes;
import wdm.util.Pair;

public class XMLNode implements XMLEntity {
    private int index;
    private StringBuilder textBuilder;
    private String label;
    private List<XMLNode> children;
    private List<Pair<String, String>> attributes;

    public XMLNode(int index, String label, List<Pair<String, String>> attributes) {
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

    public List<Pair<String, String>> getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        return "XMLNode(" + label + ", "  + getText() + ", " + children + ")";
    }

    @Override
    public String toXMLString(){
        StringBuilder builder = new StringBuilder();
        bufferedToXMLString(builder, 0);
        return builder.toString();
    }

    private void bufferedToXMLString(StringBuilder builder, int level){
        char[] wsBuf = new char[level * 2];
        Arrays.fill(wsBuf, ' ');
        String ws = new String(wsBuf);

        builder.append(ws + "<" + label);
        if(!attributes.isEmpty()){
            builder.append(' ');
        }
        for(int i = 0 ; i < attributes.size() ; i++){
            Pair<String, String> attribute = attributes.get(i);
            builder.append(attribute.first + " = \"" + attribute.second + '\"');
            if(i != attributes.size() - 1){
                builder.append(' ');
            }
        }

        builder.append(">\n");
        for(XMLNode child : children){
            child.bufferedToXMLString(builder, level + 1);
        }
        builder.append(ws + "</" + label + ">\n");
    }
}
