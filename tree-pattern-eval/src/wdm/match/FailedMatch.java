package wdm.match;

import wdm.TPEStack;

public class FailedMatch extends Match{
    public FailedMatch(TPEStack stack, Match parent) {
        super(-1, "", parent, stack);
        close();
    }

    @Override
    public String toString() {
        return "null";
    }
}
