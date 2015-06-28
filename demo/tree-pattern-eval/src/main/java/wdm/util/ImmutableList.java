package wdm.util;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class ImmutableList<A>{
    public static <A> ImmutableList<A> singleton(A a){
        return new Cons<>(a, new Nil<>());
    }

    private static final ImmutableList emptyInstance = new Nil();

    @SuppressWarnings("unchecked")
    public static <A> ImmutableList<A> empty(){
        return (ImmutableList<A>) emptyInstance;
    }

    public static <A> ImmutableList<A> apply(A... elements){
        ImmutableList<A> result = empty();
        for(int i = elements.length - 1 ; i >= 0; i--){
            result = cons(elements[i], result);
        }
        return result;
    }

    public static <A> ImmutableList<A> cons(A a, ImmutableList<A> as){
        return new Cons<>(a, as);
    }

    public static <A> ImmutableList<A> flatten(ImmutableList<ImmutableList<A>> list){
        if(list.isEmpty()){
            return empty();
        } else{
            return list.head().concat(flatten(list.tail()));
        }
    }

    public ImmutableList<A> cons(A value){
        return cons(value, this);
    }


    public abstract A head();
    public abstract ImmutableList<A> tail();
    public abstract boolean isEmpty();
    public abstract ImmutableList<A> concat(ImmutableList<A> other);
    public abstract <B> ImmutableList<B> flatMap(Function<A, ImmutableList<B>> f);

    public <B> ImmutableList<B> map(Function<A, B> f){
        return flatMap(a -> singleton(f.apply(a)));
    }

    public void forEach(Consumer<A> consumer){
        map(a -> {
            consumer.accept(a);
            return a;
        });
    }

    private static class Cons<A> extends ImmutableList<A>{
        public final A value;
        public final ImmutableList<A> rest;
        public Cons(A value, ImmutableList<A> rest) {
            this.value = value;
            this.rest = rest;
        }

        @Override
        public A head() {
            return value;
        }

        @Override
        public ImmutableList<A> tail() {
            return rest;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public ImmutableList<A> concat(ImmutableList<A> other) {
            return cons(value, rest.concat(other));
        }

        @Override
        public <B> ImmutableList<B> flatMap(Function<A, ImmutableList<B>> f) {
            return f.apply(value).concat(rest.flatMap(f));
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder("[");
            forEach(e -> builder.append(e).append(", "));
            builder.setLength(builder.length() - 2); //remove last ", "
            return builder.append("]").toString();
        }
    }

    private static class Nil<A> extends ImmutableList<A>{
        private Nil(){}

        @Override
        public A head() {
            throw new NoSuchElementException("empty.head");
        }

        @Override
        public ImmutableList<A> tail() {
            throw new NoSuchElementException("empty.tail");
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public ImmutableList<A> concat(ImmutableList<A> other) {
            return other;
        }

        @Override
        public <B> ImmutableList<B> flatMap(Function<A, ImmutableList<B>> f) {
            return empty();
        }

        @Override
        public String toString() {
            return "[]";
        }
    }

}
