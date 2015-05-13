package wdm;

public class FailedMatch extends Match{
    public FailedMatch(Match parent, TPEStack stack) {
        super(-1, parent, stack);
        close();
    }

    @Override
    public String toString() {
        return "null";
    }
}
