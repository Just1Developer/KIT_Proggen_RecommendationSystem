package net.justonedev.model.stream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * A stream class for any data, provides all stream (not allowed here) functionality that is used in the scope of this project.
 * @param <T> The type of the stream.
 * @author uwwfh
 */
public class DataStream<T> {
    private static final int MAX_TOLERANCE_SAME_SORTING_VALUE = 1000;

    private final List<T> stream;

    private DataStream() {
        this.stream = new ArrayList<>();
    }

    /**
     * Creates a new datastream from the items in the given collection. Will create a shallow copy.
     * @param stream The reference data.
     */
    public DataStream(Collection<T> stream) {
        this.stream = new ArrayList<>(stream);
    }

    private void addElement(T element) {
        stream.add(element);
    }

    private void addElementIfNotContains(T element) {
        if (!stream.contains(element)) {
            stream.add(element);
        }
    }

    private void addElements(Collection<T> elements) {
        stream.addAll(elements);
    }

    /**
     * Maps all the elements in the stream to a different element, and returns a new stream of the new elements.
     * @param mapper The mapping function.
     * @return The new stream.
     * @param <K> The type of the new, mapped-to, elements.
     */
    public <K> DataStream<K> map(StreamMapper<T, K> mapper) {
        DataStream<K> mutatedStream = new DataStream<>();
        for (T element : this.stream) {
            mutatedStream.addElement(mapper.map(element));
        }
        return mutatedStream;
    }

    /**
     * Filters a copy of the stream to only include elements that are accepted by the filter function,
     * so for which the filter function returns true.
     * Returns a new Stream with the filtered elements.
     * @param filter The filtering function.
     * @return A new stream with only the filtered elements.
     */
    public DataStream<T> filter(Filter<T> filter) {
        DataStream<T> filteredStream = new DataStream<>();
        for (T element : this.stream) {
            if (filter.filter(element)) {
                filteredStream.addElement(element);
            }
        }
        return filteredStream;
    }

    /**
     * Sorts the stream by a given comparator. Creates a new stream object with the elements sorted.
     * @param comparator The comparator to sort by.
     * @return A new stream with the sorted elements.
     */
    public DataStream<T> sorted(Comparator<T> comparator) {
        DataStream<T> filteredStream = new DataStream<>(this.stream);
        filteredStream.stream.sort(comparator);
        return filteredStream;
    }

    /**
     * Sorts the stream by a given string comparator. Creates a new stream object with the elements sorted.
     * This specifically sorts by a string, as the usual Comparator.comparing(...) uses the {@code java.util.function}
     * package, which is not allowed.
     *
     * @param comparator The comparator to sort by.
     * @return A new stream with the sorted elements.
     */
    public DataStream<T> sorted(StringComparator<T> comparator) {
        DataStream<T> filteredStream = new DataStream<>(this.stream);
        filteredStream.stream.sort((a, b) -> comparator.mapToString(a).compareTo(comparator.mapToString(b)));
        return filteredStream;
    }

    /**
     * Sorts the stream by a given string comparator, and then sorts by the given integer comparator.
     * Creates a new stream object with the elements sorted.
     * Only supports up to {@linkplain #MAX_TOLERANCE_SAME_SORTING_VALUE} - 1 elements to have the same string comparator value.<br/>
     * This specifically sorts by a string and then integer, as the usual Comparator.comparing(...).thenComparing(...)
     * uses the {@code java.util.function} package, which is not allowed.
     *
     * @param comparator The comparator to sort by.
     * @param thenComparing The integer comparator to sort by in case of ties.
     * @return A new stream with the sorted elements.
     */
    public DataStream<T> sorted(StringComparator<T> comparator, NumberComparator<T> thenComparing) {
        DataStream<T> filteredStream = new DataStream<>(this.stream);
        // This means this can only properly support thenComparing if <= 1000 elements have the same value for the first comparator
        // For this application (specifically, its tests), it's fine
        filteredStream.stream.sort((a, b) -> (comparator.mapToString(a)
                .compareTo(comparator.mapToString(b)) * MAX_TOLERANCE_SAME_SORTING_VALUE)
                + thenComparing.mapToInt(a)
                - thenComparing.mapToInt(b));
        return filteredStream;
    }

    /**
     * Returns a copy of the current stream with only distinct elements. Keeps the order of the elements.
     * @return A new stream with no duplicates.
     */
    public DataStream<T> distinct() {
        DataStream<T> filteredStream = new DataStream<>();
        for (T element : this.stream) {
            filteredStream.addElementIfNotContains(element);
        }
        return filteredStream;
    }

    /**
     * Performs a given function for each element of the stream.
     * @param action The action to perform.
     */
    public void forEach(Runnable<T> action) {
        for (T element : this.stream) {
            action.run(element);
        }
    }

    /**
     * Applies the given matcher function to all elements and returns if the function returns {@code true}
     * for any (1 or more) of the elements.
     *
     * @param matcher The matcher function (if the element matches)
     * @return If any of the elements matches the matcher function.
     */
    public boolean anyMatch(MatcherFunction<T> matcher) {
        for (T element : this.stream) {
            if (matcher.matches(element)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Converts the stream to a list. Creates a shallow copy.
     * @return The stream elements in a list.
     */
    public List<T> toList() {
        return new ArrayList<>(stream);
    }

    /**
     * Creates a new stream from a given collection of elements.
     * Creates a shallow copy of the collection for internal use.
     *
     * @param stream The elements to build the stream from.
     * @return A new stream from the elements.
     * @param <T> The type of the elements and stream.
     */
    public static <T> DataStream<T> of(Collection<T> stream) {
        return new DataStream<>(stream);
    }

    /**
     * Unwraps a Stream of List&lt;T&gt; to a Stream of &lt;T&gt; by simply unwrapping the lists and adding the elements
     * to the stream. Keeps the order of lists and within the lists. May generate duplicates, you may need to
     * use {@linkplain #distinct()}.
     *
     * @param stream The stream of lists to unwrap.
     * @return A new stream of just the elements of the lists.
     * @param <T> The type of the elements in the lists.
     */
    public static <T> DataStream<T> unwrap(DataStream<Collection<T>> stream) {
        DataStream<T> unwrappedStream = new DataStream<>();
        for (Collection<T> collection : stream.stream) {
            unwrappedStream.addElements(collection);
        }
        return unwrappedStream;
    }
}
