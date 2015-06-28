package wdm.tpe.builder;

import wdm.matcher.*;
import wdm.tpe.TPEStack;
import wdm.tpe.TPEStackBranch;
import wdm.tpe.TPEStackRoot;
import wdm.util.ImmutableList;


public class TPEBuilder {

    private Matcher matcher;
    private ImmutableList<TPEBuilderEntry> children;
    private ImmutableList<TPEBuilder> scopes;

    private TPEBuilder(Matcher matcher, ImmutableList<TPEBuilderEntry> children, ImmutableList<TPEBuilder> scopes) {
        this.matcher = matcher;
        this.children = children;
        this.scopes = scopes;
    }


    private TPEBuilder updateMatcher(Matcher matcher){
        return new TPEBuilder(matcher, children, scopes);
    }

    private TPEBuilder updateChildren(ImmutableList<TPEBuilderEntry> children){
        return new TPEBuilder(matcher, children, scopes);
    }

    private TPEBuilder updateScopes(ImmutableList<TPEBuilder> scopes){
        return new TPEBuilder(matcher, children, scopes);
    }

    public static TPEBuilder builder(Matcher rootMatcher){
        return new TPEBuilder(rootMatcher, ImmutableList.<TPEBuilderEntry>empty(), ImmutableList.<TPEBuilder>empty());
    }

    public static TPEBuilder builder(){
        return builder(new MatcherAny());
    }

    public static TPEBuilder builder(String label){
        return builder(new MatcherString(label));
    }

    public TPEBuilder in(Matcher matcher, TPEBuilderContext ctx){
        return new TPEBuilder(this.matcher, children, scopes.cons(ctx.apply(builder(matcher))));
    }

    public TPEBuilder in(String label, TPEBuilderContext ctx){
        return in(new MatcherString(label), ctx);
    }

    public TPEBuilder child(boolean select, Matcher matcher){
        return updateChildren(children.cons(new TPEBuilderEntry(select, matcher)));
    }

    public TPEBuilder child(Matcher matcher){
        return child(false, matcher);
    }

    public TPEBuilder child(String label){
        return child(false, new MatcherString(label));
    }

    public TPEBuilder child(){
        return child(new MatcherAny());
    }

    public TPEBuilder childWhere(boolean select, Matcher matcher, MatchPredicate predicate){
        return child(select, new MatcherPredicate(matcher, predicate));
    }

    public TPEBuilder childWhere(boolean select, String label, MatchPredicate predicate){
        return childWhere(select, new MatcherString(label), predicate);
    }

    public TPEBuilder childWhere(String label, MatchPredicate predicate){
        return childWhere(false, label, predicate);
    }

    public TPEBuilder select(Matcher matcher){
        return child(true, matcher);
    }

    public TPEBuilder select(String label){
        return select(new MatcherString(label));
    }

    public TPEBuilder selectWhere(String label, MatchPredicate predicate){
        return childWhere(true, label, predicate);
    }

    public TPEBuilder select(){
        return select(new MatcherAny());
    }

    public TPEBuilder selectOptional(Matcher matcher){
        return child(true, new MatcherOpt(matcher));
    }

    public TPEBuilder selectOptional(String label){
        return selectOptional(new MatcherString(label));
    }

    public TPEBuilder where(MatchPredicate predicate){
        /*
        TPEBuilderEntry head = children.head();
        Matcher newMatcher = new MatcherPredicate(children.head().matcher, predicate);
        return updateChildren(children.tail().cons(new TPEBuilderEntry(head.selected, newMatcher)));
         */
        return updateMatcher(new MatcherPredicate(matcher, predicate));
    }



    public TPEStack build(){
        TPEStack root = new TPEStackRoot(matcher);
        children.forEach(child -> {
            new TPEStackBranch(root, child.matcher, child.selected);
        });
        scopes.forEach(scope -> scope.buildScope(root));
        return root;
    }

    private void buildScope(TPEStack parent){
        final TPEStack current = new TPEStackBranch(parent, matcher);
        children.forEach(child -> {
            new TPEStackBranch(current, child.matcher, child.selected);
        });

        scopes.forEach(scope -> scope.buildScope(current));
    }

    @FunctionalInterface
    public interface TPEBuilderContext{
        TPEBuilder apply(TPEBuilder ctx);
    }
}
