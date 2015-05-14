package wdm.xml;

public class XMLAttribute implements XMLEntity {
    private int index;
    private String label;
    private String text;

    public XMLAttribute(int index, String label, String text) {
        this.index = index;
        this.label = label;
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public int getIndex() {
        return index;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toXMLString() {
        return label + " = " + text;
    }
}
