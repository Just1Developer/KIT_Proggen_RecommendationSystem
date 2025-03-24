package net.justonedev.model.stream;

public interface Runnable<T> {
    void run(T element);
}
