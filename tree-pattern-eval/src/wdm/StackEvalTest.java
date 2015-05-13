package wdm;


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class StackEvalTest extends DefaultHandler {


    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
        args = new String[]{"res/xml/persons.xml"};

        if(args.length > 0){
//            TPEStack root = new TPEStackRoot(new MatcherString("person"));
            TPEStack root = new TPEStackRoot(new MatcherAny());
            TPEStack email = new TPEStackBranch(root, new MatcherString("email"));
            TPEStack name = new TPEStackBranch(root, new MatcherString("name"));
            TPEStack last = new TPEStackBranch(name, new MatcherString("last"));

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();

            StackEvalTest eval = new StackEvalTest(root);
            parser.parse(args[0], eval);
            System.out.println(eval.tempStack);

            System.out.println("\nThe tuples:");
            for(Match m: eval.tempStack) {
                for(List<Match> tuple: m.getTuples()) {
                    System.out.println(tuple);
                }
                System.out.println();
            }
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
            if (s.isMatch(qName) && s.parentHasMatch()) {
                s.createMatch(currentPre);
            }
        }
        for (int i = 0; i < attributes.getLength(); i++) {
            for (TPEStack s : rootStack.getDescendantStacks()) {
//                if (attributes.getQName(i).equals(s.getName()) && s.getParent().top().getState() == MatchState.OPEN) {
                if (s.isMatch(attributes.getQName(i)) && s.parentHasMatch()) {
//                    Match ma = new Match(currentPre, s.getParent().top(), s);
//                    s.push(ma);
                    s.createMatch(currentPre);
                }
            }
        }
        preOfOpenNodes.push(currentPre++);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        int preOfLastOpen = preOfOpenNodes.pop();
        if(rootStack.isMatch(qName)){
            System.out.println("root closing");
        }

        for(TPEStack s : rootStack.getDescendantStacks()){
            if (s.isMatch(qName) && s.hasOpenMatch(preOfLastOpen)){
                Match m = s.pop();
                System.out.println("Popping match");
                for (TPEStack pChild : s.getChildren()){
                    if(!m.getChildren().containsKey(pChild)){
                        if(m.getParent() != null) {
                            m.getParent().getChildren().remove(s);
                            System.out.println("Removing match from parent");
                            break;
                        }
                    }
                }
                if (m.getParent() == null) {
                    if(m.getChildren().size() >= s.getChildren().size()) {
                        tempStack.push(m);
                    }
                }
//                if (m.getParent() == null && m.getChildren().size() >= s.getChildren().size()) {
//                    tempStack.push(m);
//                }
                m.close();
            }
        }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        char current[] = new char[length];
        System.arraycopy(ch, start, current, 0, length);
//        System.out.println(current);
//        System.out.println(start);
//        System.out.println(length);
    }
}
