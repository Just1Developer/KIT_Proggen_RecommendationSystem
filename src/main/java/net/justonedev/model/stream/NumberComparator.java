package net.justonedev.model.stream;

/**
 * The number comparator function. Maps an element of type T to an integer, used in stream secondary sorting.
 * @author uwwfh
 * @param <T> The type of the stream elements.
 */
public interface NumberComparator<T> {
    /**
     * The mapping function. Maps an element to a given number (which should not exceed 999), used in custom
     * secondary sorting in ascending order.
     * @param element The element to map.
     * @return The number to sort by.
     */
    int mapToInt(T element);
}
