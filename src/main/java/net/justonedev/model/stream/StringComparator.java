package net.justonedev.model.stream;

/**
 * The number comparator function. Maps an element of type T to a String, used in custom stream sorting.
 * @author uwwfh
 * @param <T> The type of the stream elements.
 */
public interface StringComparator<T> {
    /**
     * The mapping function. Maps an element to a given string, used in custom sorting in ascending order,
     * using {@linkplain String#compareTo(String)}.
     * @param element The element to map.
     * @return The number to sort by.
     */
    String mapToString(T element);
}
