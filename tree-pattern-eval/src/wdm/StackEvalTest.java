package wdm;


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.List;
import java.util.Stack;

public class StackEvalTest extends DefaultHandler {


    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
        args = new String[]{"res/xml/persons.xml"};

        if(args.length > 0){
            TPEStack root = new TPEStackRoot(new MatcherString("person"), true);
//            TPEStack root = new TPEStackRoot(new MatcherString("person"), false);
//            TPEStack name = new TPEStackBranch(root, new MatcherString("name"), true);
//            TPEStack last = new TPEStackBranch(name, new MatcherString("last"), true);
//            TPEStack email = new TPEStackBranch(root, new MatcherString("email"), true);
//            TPEStack email = new TPEStackBranch(root, new MatcherPredicate(new MatcherOpt("email"), (label, text) -> text.endsWith("@home")), true);
//            TPEStack root = new TPEStackRoot(new MatcherPredicate(new MatcherOpt("person"), (label, text) -> text.endsWith("bla")), true);

            TPEStack sub = new TPEStackBranch(root, new MatcherString("sub"), true);
//            TPEStack piet = new TPEStackAttribute(root, new MatcherString("name"), true);


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

                System.out.println(m.toXml());
            }
        }
    }


    TPEStack rootStack;
    int currentPre;
    Stack<XMLNode> preOfOpenNodes;
    Stack<Match> tempStack;

    public StackEvalTest(TPEStack rootStack) {
        this.rootStack = rootStack;
        this.preOfOpenNodes = new Stack<>();
        this.currentPre = 0;
        this.tempStack = new Stack<>();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        XMLNode currentNode = new XMLNode(currentPre, qName, attributes);
        if(!preOfOpenNodes.isEmpty()) {
            preOfOpenNodes.peek().addChild(currentNode);
        }
        preOfOpenNodes.push(currentNode);

        for (TPEStack s : rootStack.getDescendantStacks()) {
            if (s.isMatch(qName) && s.parentHasMatch()) {
                s.createMatch(currentPre, qName);
            }
        }
        for (int i = 0; i < attributes.getLength(); i++) {
            for (TPEStack s : rootStack.getDescendantStacks()) {
//                if (attributes.getQName(i).equals(s.getName()) && s.getParent().top().getState() == MatchState.OPEN) {
                if (s.isMatch(attributes.getQName(i)) && s.parentHasMatch() && (s instanceof TPEStackAttribute) && s.matcher.postMatch(attributes.getQName(i), attributes.getValue(i))) {
//                    Match ma = new Match(currentPre, s.getParent().top(), s);
//                    s.push(ma);
                    Match m = s.createMatch(currentPre, qName);
                    m.setContent(attributes.getValue(i));
                }
            }
        }
        currentPre++;
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        XMLNode preOfLastOpen = preOfOpenNodes.pop();

        for(TPEStack s : rootStack.getDescendantStacks()){
            if (s.isMatch(qName) && s.hasOpenMatch(preOfLastOpen.getIndex())){
                Match m = s.top();
                m.setContent(preOfLastOpen.getText());
                m.setXml(preOfLastOpen);

                System.out.println("Popping match: " + m + " with text: " + preOfLastOpen.getText());
                System.out.println("Popping match: " + m + " with xml: " + m.getXml());

                for (TPEStack pChild : s.getChildren()){
                    if(!m.getChildren().containsKey(pChild)) {
                        if(!pChild.isOptional()) {
                            if (m.getParent() != null) {
                                m.getParent().removeChild(s, m.getStart());
                                System.out.println("Removing match from parent");
                            }
                        } else {
                            pChild.createMatch(MatcherOpt.MATCH_FAILED, "");
                            pChild.pop();
                        }
                    }
                }

                if(!s.matcher.postMatch(m.getLabel(), preOfLastOpen.getText())) {
                    if (s.getParent() != null) {
                        m.getParent().removeChild(s, m.getStart());
                        System.out.println("Predicate failed, Removing match from parent");
                    }
                } else if (m.getParent() == null && m.getChildren().size() >= s.getChildren().size()) {
                    tempStack.push(m);
                }

                m.close();
                s.pop();
            }


        }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        preOfOpenNodes.peek().appendText(new String(ch, start, length).trim());
//        System.out.println(start);
//        System.out.println(length);
    }
}
