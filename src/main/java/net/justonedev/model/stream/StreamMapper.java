package net.justonedev.model.stream;

/**
 * The mapper function for stream elements. Maps an element of type T to an element of type K,
 * used in {@linkplain DataStream#map(StreamMapper)}.
 * @author uwwfh
 * @param <T> The type of the stream elements.
 * @param <K> The new type of the new stream elements.
 */
public interface StreamMapper<T, K> {
    /**
     * The mapping function. Takes in an element of type T and maps it to an element of type K.
     * @param element The element (Type T).
     * @return The mapped element (Type K).
     */
    K map(T element);
}
