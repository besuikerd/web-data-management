package test;

import wdm.TPEStack;
import wdm.TPEStackBranch;
import wdm.TPEStackRoot;
import wdm.match.Match;
import wdm.matcher.MatcherPredicate;
import wdm.matcher.MatcherString;

import java.util.List;

public class PredicateTest extends CTPTest{
    public static void main(String[] args) {
        new PredicateTest().runTests();
    }

    @Override
    public void runTests() {
        testSelectEmailLastNameWithMinimumAge();
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
            if(i == 0){
                System.out.println(prettifyResult(result));
                System.out.println("row count: " + rowCount);
            }
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
