package net.justonedev.model.stream;

/**
 * The filter function. Maps an element of type T to a boolean, used in {@linkplain DataStream#filter(Filter)}.
 * @author uwwfh
 * @param <T> The type of the stream elements.
 */
public interface Filter<T> {
    /**
     * The filtering function. Should return true if the given element should remain in the filtered stream, false if not.
     * @param element The element to map.
     * @return True or false, based on if the element should remain after the filer.
     */
    boolean filter(T element);
}
