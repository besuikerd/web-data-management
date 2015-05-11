package wdm;


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.Stack;

public class StackEvalTest extends DefaultHandler {


    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
        args = new String[]{"xml/persons.xml"};





        if(args.length > 0){
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            parser.parse(args[0], new StackEvalTest());
        }


        TPEStack root = new TPEStack(null, "person");
        TPEStack email = new TPEStack(root, "email");
        TPEStack name = new TPEStack(root, "name");
        TPEStack last = new TPEStack(name, "last");
    }


    TPEStack rootStack;
    int currentPre = 0;
    Stack<Integer> preOfOpenNodes;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        for (TPEStack s : rootStack.getDescendantStacks()) {
            if (localName == s.getName() && s.getParent().top().getState() == MatchState.OPEN) {
                Match m = new Match(currentPre, s.getParent().top(), s);
                s.push(m);
                preOfOpenNodes.push(currentPre);
            }
            currentPre++;
        }
        for (int i = 0; i < attributes.getLength(); i++) {

            for (TPEStack s : rootStack.getDescendantStacks()) {
                if (attributes.getQName(i).equals(s.getName()) && s.getParent().top().getState() == MatchState.OPEN) {
                    Match ma = new Match(currentPre, s.getParent().top(), s);
                    s.push(ma);
                }
            }
            currentPre++;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
            int preOfLastOpen = preOfOpenNodes.pop();
            for(TPEStack s : rootStack.getDescendantStacks()){
                if (s.getName().equals(localName) && s.top().getState() == MatchState.OPEN && s.top().getStart() == preOfLastOpen){
                    Match m = s.pop();
                    for (TPEStack pChild : s.getChildren()){
                        m.getChildren().remove(pChild);
                    }
                    m.close();
                }
            }
    }
}
