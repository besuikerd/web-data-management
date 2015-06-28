package test;

import org.xml.sax.SAXException;
import wdm.tpe.StackEval;
import wdm.tpe.TPEStack;
import wdm.match.Match;
import wdm.tpe.builder.TPEBuilder;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public abstract class CTPTest {
    protected String resourcePath = "res/xml";
    private SAXParserFactory parserFactory = SAXParserFactory.newInstance();


    public List<Match> match(TPEStack root, String filename){
        try{
            final SAXParser parser = parserFactory.newSAXParser();
            return new StackEval(root).evaluate(handler -> parser.parse(resourcePath + File.separator + filename, handler));
        } catch(SAXException | IOException | ParserConfigurationException e) {
            System.err.println("Unable to process xml, cause: " + e.getMessage());
            return null;
        }
    }

    protected String prettifyResult(List<Match> result){
        StringBuilder builder = new StringBuilder();
        if(result != null){
            if(result.isEmpty()){
                builder.append("empty result"  + "\n");
            } else{
                int i = 0;
                for(Match match : result) {
                    for (List<Match> row : match.getTuples()) {
                        builder.append("===== ROW " + (i++) + " =====\n");
                        row.forEach(column -> {
                            builder.append("==== " + column.getLabel() + " =====" + "\n");
                            builder.append(column.getXml() == null ? "null" : column.getXml().toXMLString() + "\n");
                            builder.append("\n");
                        });
                    }
                }

            }
        }
        return builder.toString();
    }

    protected void assertNMatches(String queryName, TPEStack root, String file, int n){
        List<Match> result = match(root, file);
        int rowCount = rowCount(result);
        if(rowCount != n){
            fail(String.format("[%s] expected %d results, got: %d", queryName, n, rowCount));
        }
    }

    protected void outputMatches(String queryName, TPEStack root, String file){
        String line = "========";

        System.out.println(line +  " Query: " + line + "\n");

        System.out.println(queryName + "\n");

        System.out.println(line + " Result: " + line + "\n");

        List<Match> result = match(root, file);
        for(Match m : result){
            for(List<Match> tuple: m.getTuples()) {
                System.out.println(tuple);
                for(Match m2 : tuple){

                    System.out.println(m2.getXml() == null ? "\nnull\n" : m2.getXml().toXMLString());
                }
            }
        }
    }

    protected void assertTrue(boolean condition){
        if(!condition){
            fail("true != " + condition);
        }
    }

    protected void assertLT(Number a, Number b){
        if(a.doubleValue() >= b.doubleValue()){
            fail(String.format("!(%s < %s)", a, b));
        }
    }

    protected void assertLTE(Number a, Number b){
        if(a.doubleValue() > b.doubleValue()){
            fail(String.format("!(%s <= %s)", a, b));
        }
    }

    protected void assertGT(Number a, Number b){
        if(a.doubleValue() <= b.doubleValue()){
            fail(String.format("!(%s > %s)", a, b));
        }
    }

    protected void assertGTE(Number a, Number b){
        if(a.doubleValue() < b.doubleValue()){
            fail(String.format("!(%s >= %s)", a, b));
        }
    }

    protected void assertFalse(boolean condition){
        if(!condition){
            fail("false != " + condition);
        }
    }

    protected void assertEquals(Object a, Object b){
        if(a == null && b != null){
            fail("null != " + b);
        } else if(a != null && b == null){
            fail(a + " != null");
        } else if(!a.equals(b)){
            fail(a + " != " + b);
        }
    }

    protected void fail(String msg){
        System.err.println("Assertion failed: " + msg);
    }

    protected int rowCount(List<Match> result){
        return result.stream().collect(Collectors.summingInt(x -> x.getTuples().size()));
    }

    public abstract void runTests();
}