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

    public static TPEBuilder builder(){
        return new TPEBuilder(new MatcherAny(), ImmutableList.<TPEBuilderEntry>empty(), ImmutableList.<TPEBuilder>empty());
    }

    public TPEBuilder in(Matcher matcher, TPEBuilderContext ctx){
        return new TPEBuilder(matcher, children, scopes.cons(ctx.apply(builder())));
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

    public TPEBuilder select(Matcher matcher){
        return child(true, matcher);
    }

    public TPEBuilder select(String label){
        return select(new MatcherString(label));
    }

    public TPEBuilder selectWhere(String label, MatchPredicate predicate){
        return child(true, new MatcherPredicate(label, predicate));
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
        children.forEach(child -> {
            new TPEStackBranch(parent, child.matcher, child.selected);
        });
        final TPEStack current = new TPEStackBranch(parent, matcher);
        scopes.forEach(scope -> scope.buildScope(current));
    }

    @FunctionalInterface
    public interface TPEBuilderContext{
        TPEBuilder apply(TPEBuilder ctx);
    }
}
