package net.justonedev.model.stream;

public interface StreamMapper<T, K> {
    K map(T element);
}
