package net.justonedev.model.stream;

/**
 * A simple run function. Runs something for a stream element. Used in {@linkplain DataStream#forEach(Runnable)}
 * @author uwwfh
 * @param <T> The type of the stream elements.
 */
public interface Runnable<T> {
    /**
     * The run function. Just performs an arbitrary action for a stream element.
     * @param element The element.
     */
    void run(T element);
}
