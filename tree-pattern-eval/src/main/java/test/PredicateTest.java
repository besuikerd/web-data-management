package test;

import wdm.matcher.MatcherAny;
import wdm.tpe.TPEStack;
import wdm.tpe.TPEStackAttribute;
import wdm.tpe.TPEStackBranch;
import wdm.tpe.TPEStackRoot;
import wdm.match.Match;
import wdm.matcher.MatcherPredicate;
import wdm.matcher.MatcherString;
import wdm.tpe.builder.TPEBuilder;

import java.util.List;

public class PredicateTest extends CTPTest{
    public static void main(String[] args) {
        new PredicateTest().runTests();
    }

    /**
     * for $p in //person[email][name/last startsWith("H")][first]
     *  return ($p/email, $p/name/last)
     */
    TPEBuilder peopleWithLastNamePredicate = TPEBuilder.builder()
        .in("person", person -> person
            .select("email")
            .in("name", name -> name
                .selectWhere("last", (label, text) -> text.startsWith("H"))
                .child("first")
            )
        );

    /**
     * for $p in //person[email]
     *                   [name/last]
     * return ($p//email, $p/name/last)
     */
    TPEBuilder q1 = TPEBuilder.builder("person")
            .select("email")
        .in("name", name -> name
            .select("last")
        );

    /**
     * for $p in //person[name/last]
     * return ($p//email, $p/name/last)
     */
    TPEBuilder q2 = TPEBuilder.builder("person")
        .selectOptional("email")
        .in("name", name -> name
            .select("last")
        );

    /**
     * for $p in //person[email]
     *  return $p//name/*
     */
    TPEBuilder q3 = TPEBuilder.builder("person")
        .child("email")
        .in("name", TPEBuilder::select);

    /**
     * for $p in //person[//first]
     *                   [//last]
     * where $p/email=m@home
     * return ($p//first, $p//last)
     */
    TPEBuilder q4 = TPEBuilder.builder("person")
        .childWhere("email", (label, text) -> text.equals("m@home"))
        .select("first")
        .select("last");

    /**
     * for $p in //person
     * where $p/email=m@home
     * return <res>{$p/&#42;/last}</res>
     *
     */
    TPEBuilder q5=TPEBuilder.builder("person")
    .childWhere("email", (label, text) -> text.equals("m@home"))
    .in(new MatcherAny(), any -> any
                    .select("last")
    );


    /**
     * for $p in //person
     * where $p/email[domain] = "com"
     * return ($p//first, $p//email)
     */
    TPEStack q6(){
        TPEStack root = new TPEStackRoot("person");
        TPEStack name = new TPEStackBranch(root, "name");
        TPEStack first = new TPEStackBranch(name, "first", true);
        TPEStack email = new TPEStackBranch(root, "email", true);
        TPEStack domain = new TPEStackAttribute(email, new MatcherPredicate("domain", (label, text) -> text.equals("com")));
        return root;
    }


    @Override
    public void runTests() {
        testBookExamples();
        testSelectEmailLastNameWithMinimumAge();
        testPeopleWithLastNamePredicate();
//        System.out.println(prettifyResult(match(q1.build(), "persons.xml")));
    }

    public void testBookExamples(){
        String bookFile = "book_persons.xml";


        String q1 = "for $p in //person[email] [name/last] \n\treturn ($p//email, $p/name/last)";
        outputMatches(q1, this.q1.build(), bookFile);

        String q2 = "for $p in //person[name/last]\n\treturn ($p//email, $p/name/last)";
        outputMatches(q2, this.q2.build(), bookFile);

        String q3 = "for $p in //person[email]\n\treturn $p//name/*";
        outputMatches(q3, this.q3.build(), bookFile);

        String q4 = "for $p in //person[//first] [//last]\n\t where $p/email=m@home\n\treturn ($p//first, $p//last)";
        outputMatches(q4, this.q4.build(), bookFile);

        String q5 = "for $p in //person\n\twhere $p/email=m@home\n\treturn <res>{$p/&#42;/last}</res>";
        outputMatches(q5, this.q5.build(), bookFile);

        String q6 = "for $p in //person\n\twhere $p/email[domain] = \"com\"\n\treturn ($p//first, $p//email)";
        outputMatches(q6, this.q6(), bookFile);
    }

    public void testPeopleWithLastNamePredicate(){
        String file = "persons.xml";
        assertNMatches("PeopleWithLastNamePredicate", peopleWithLastNamePredicate.build(), file, 14);
    }

    /**
     * tests if predicate is correctly tested. increments the age predicate by 10 between  10 and 80 and counts if the
     * number of rows is at least as big as the previous result
     */
    public void testSelectEmailLastNameWithMinimumAge(){
        int lastRowCount = Integer.MAX_VALUE;
        for(int i = 10 ; i < 80 ; i += 10){
            List<Match> result = selectEmailLastNameWithMinimumAge(i);
            int rowCount = rowCount(result);
            assertLTE(rowCount, lastRowCount);
            lastRowCount = rowCount;
        }
    }

    /**
     * for $p in //person
     *          [email]
     *          [name/last]
     *          [age > {minimumAge}]
     * return ($p/email, p/name/last)
     */
    public List<Match> selectEmailLastNameWithMinimumAge(final int minimumAge){
        TPEStack person = new TPEStackRoot("person");
        TPEStack email = new TPEStackBranch(person, "email").select();
        TPEStack name = new TPEStackBranch(person, "name");
        TPEStack lastName = new TPEStackBranch(name, "last").select();
        TPEStack age = new TPEStackBranch(person, new MatcherPredicate(new MatcherString("age"), (label, text) -> {
            try{
                return Integer.parseInt(text) > minimumAge;
            } catch(NumberFormatException e){
                return false;
            }
        })).select();

        List<Match> matches = match(person, "persons.xml");
        return matches;
    }
}
