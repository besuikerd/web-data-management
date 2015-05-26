package wdm;


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import wdm.match.Match;
import wdm.matcher.MatcherOpt;
import wdm.matcher.MatcherPredicate;
import wdm.matcher.MatcherString;
import wdm.util.Pair;
import wdm.xml.XMLNode;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class StackEvalTest extends DefaultHandler {


    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
        args = new String[]{"res/xml/persons.xml"};
        final String filePath = args[0];

        if(args.length > 0){
            TPEStack root = new TPEStackRoot(new MatcherString("person"));
            TPEStack name = new TPEStackBranch(root, new MatcherString("name"), true);
            TPEStack last = new TPEStackBranch(name, new MatcherPredicate("last", (label, text) -> !text.isEmpty()), true);

            TPEStack root = new TPEStackRoot(new MatcherString("person"));
            TPEStack email = new TPEStackBranch(root, new MatcherString("email"), true);
            TPEStack name = new TPEStackBranch(root, new MatcherString("name"));
            TPEStack last = new TPEStackBranch(root, new MatcherString("last"), true);

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();

            List<Match> result = new StackEval(root).evaluate(handler -> parser.parse(filePath, handler));

            System.out.println("\nThe tuples:");

            for(Match m: result) {
                for(List<Match> tuple: m.getTuples()) {
                    System.out.println(tuple);
                    for(Match m2 : tuple){
                        System.out.println(m2.getXml().toXMLString());
                    }
                }
                System.out.println();
            }
        }
    }



}
