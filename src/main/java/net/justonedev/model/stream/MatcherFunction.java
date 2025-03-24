package net.justonedev.model.stream;

public interface MatcherFunction<T> {
    boolean matches(T element);
}
