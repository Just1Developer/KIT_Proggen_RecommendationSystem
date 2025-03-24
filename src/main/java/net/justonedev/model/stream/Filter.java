package net.justonedev.model.stream;

public interface Filter<T> {
    boolean filter(T element);
}
