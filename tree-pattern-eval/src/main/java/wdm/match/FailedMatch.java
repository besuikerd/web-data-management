package wdm.match;

import wdm.tpe.TPEStack;

public class FailedMatch extends Match{
    public FailedMatch(TPEStack stack, String label, Match parent) {
        super(-1, label, parent, stack);
        close();
    }

    @Override
    public String toString() {
        return "null";
    }
}
