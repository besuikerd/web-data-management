package wdm;


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.Arrays;
import java.util.Stack;

public class StackEvalTest extends DefaultHandler {


    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
        args = new String[]{"res/xml/persons.xml"};

        if(args.length > 0){
            TPEStack root = new TPEStack(null, "person");
            TPEStack email = new TPEStack(root, "email");
            TPEStack name = new TPEStack(root, "name");
            TPEStack last = new TPEStack(name, "last");

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();

            StackEvalTest eval = new StackEvalTest(root);
            parser.parse(args[0], eval);
            System.out.println(eval.tempStack);
        }
    }


    TPEStack rootStack;
    int currentPre;
    Stack<Integer> preOfOpenNodes;
    Stack<Match> tempStack;

    public StackEvalTest(TPEStack rootStack) {
        this.rootStack = rootStack;
        this.preOfOpenNodes = new Stack<>();
        this.currentPre = 0;
        this.tempStack = new Stack<>();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        for (TPEStack s : rootStack.getDescendantStacks()) {
            if (qName.equals(s.getName())){
                if(s.getParent() == null){
                    System.out.println("pushing root " + s.getName());
                    Match m = new Match(currentPre, null, s);
                    s.push(m);
                } else if(s.getParent().top() != null && s.getParent().top().getState() == MatchState.OPEN){
                    Match m = new Match(currentPre, s.getParent().top(), s);
                    s.push(m);
                    System.out.println("pushing " + s.getName());
                }
            }
        }
        for (int i = 0; i < attributes.getLength(); i++) {
            for (TPEStack s : rootStack.getDescendantStacks()) {
                if (attributes.getQName(i).equals(s.getName()) && s.getParent().top().getState() == MatchState.OPEN) {
                    Match ma = new Match(currentPre, s.getParent().top(), s);
                    s.push(ma);
                }
            }
        }
        preOfOpenNodes.push(currentPre++);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
            int preOfLastOpen = preOfOpenNodes.pop();
            if(qName.equals(rootStack.getName())){
                System.out.println("root closing");
            }

            for(TPEStack s : rootStack.getDescendantStacks()){
                if (qName.equals(s.getName()) && s.top() != null && s.top().getState() == MatchState.OPEN && s.top().getStart() == preOfLastOpen){
                    Match m = s.pop();
                    for (TPEStack pChild : s.getChildren()){
                        if(!m.getChildren().containsKey(pChild)){
                            if(m.getParent() != null) {
                                m.getParent().getChildren().remove(s);
                            }
                        }
                    }
                    if (m.getParent() == null && m.getChildren().size() >= s.getChildren().size()) {
                        tempStack.push(m);
                    }
                    System.out.println("popping " + m.getChildren());
                    m.close();
                }
            }
    }
}
