package wdm.tpe;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import wdm.match.Match;
import wdm.matcher.MatcherOpt;
import wdm.util.Pair;
import wdm.xml.XMLNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class StackEval extends DefaultHandler {
    private TPEStack rootStack;
    int currentPre;
    Stack<XMLNode> preOfOpenNodes;
    List<Match> result;

    public StackEval(TPEStack rootStack) {
        this.rootStack = rootStack;
        this.preOfOpenNodes = new Stack<>();
        this.currentPre = 0;
        this.result = new LinkedList<>();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        List<Pair<String, String>> attributePairs = new ArrayList<>(attributes.getLength());
        for(int i = 0 ; i < attributes.getLength() ; i++){
            attributePairs.add(Pair.create(attributes.getQName(i), attributes.getValue(i)));
        }
        XMLNode currentNode = new XMLNode(currentPre, qName, attributePairs);

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
                if (s.isMatch(attributes.getQName(i)) && s.parentHasMatch() && (s instanceof TPEStackAttribute) && s.matcher.postMatch(attributes.getQName(i), attributes.getValue(i))) {
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

                for (TPEStack pChild : s.getChildren()){
                    if(!m.getChildren().containsKey(pChild)) {
                        if(!pChild.isOptional()) {
                            if (m.getParent() != null) {
                                m.getParent().removeChild(s, m.getStart());
                            }
                        } else {
                            pChild.createMatch(MatcherOpt.MATCH_FAILED, "");
                            pChild.pop();
                        }
                    }
                }

                if(!s.matcher.postMatch(m.getLabel(), preOfLastOpen.getText().trim())) {
                    if (s.getParent() != null) {
                        m.getParent().removeChild(s, m.getStart());
                    }
                } else if (m.getParent() == null && m.getChildren().size() >= s.getChildren().size()) {
                    result.add(m);
                }
                m.close();
                s.pop();
            }


        }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException{
        preOfOpenNodes.peek().appendText(new String(ch, start, length));
    }

    public List<Match> evaluate(ParseFunc func) throws IOException, SAXException{
        func.apply(this);
        List<Match> result = this.result;
        this.result = new LinkedList<>();
        return result;
    }

    @FunctionalInterface
    public static interface ParseFunc{
        void apply(DefaultHandler handler) throws SAXException, IOException;
    }
}
