package wdm.tpe.builder;

import wdm.matcher.Matcher;

public class TPEBuilderEntry {
    public final boolean selected;
    public final Matcher matcher;

    public TPEBuilderEntry(boolean selected, Matcher matcher) {
        this.selected = selected;
        this.matcher = matcher;
    }
}
