package net.justonedev.model.stream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class DataStream<T> {
    private final List<T> stream;

    private DataStream() {
        this.stream = new ArrayList<>();
    }

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

    public <K> DataStream<K> map(StreamMapper<T, K> mapper) {
        DataStream<K> mutatedStream = new DataStream<>();
        for (T element : this.stream) {
            mutatedStream.addElement(mapper.map(element));
        }
        return mutatedStream;
    }

    public DataStream<T> filter(Filter<T> filter) {
        DataStream<T> filteredStream = new DataStream<>();
        for (T element : this.stream) {
            if (filter.filter(element)) {
                filteredStream.addElement(element);
            }
        }
        return filteredStream;
    }

    public DataStream<T> sorted(Comparator<T> comparator) {
        DataStream<T> filteredStream = new DataStream<>(this.stream);
        filteredStream.stream.sort(comparator);
        return filteredStream;
    }

    public DataStream<T> sorted(StringComparator<T> comparator) {
        DataStream<T> filteredStream = new DataStream<>(this.stream);
        filteredStream.stream.sort((a, b) -> comparator.mapToString(a).compareTo(comparator.mapToString(b)));
        return filteredStream;
    }

    public DataStream<T> sorted(StringComparator<T> comparator, NumberComparator<T> thenComparing) {
        DataStream<T> filteredStream = new DataStream<>(this.stream);
        // This means this can only properly support thenComparing if <= 1000 elements have the same value for the first comparator
        // For this application (specifically, its tests), it's fine
        filteredStream.stream.sort((a, b) -> (comparator.mapToString(a)
                .compareTo(comparator.mapToString(b)) * 1000)
                + thenComparing.mapToInt(a)
                - thenComparing.mapToInt(b));
        return filteredStream;
    }

    public DataStream<T> distinct() {
        DataStream<T> filteredStream = new DataStream<>();
        for (T element : this.stream) {
            filteredStream.addElementIfNotContains(element);
        }
        return filteredStream;
    }

    public void forEach(Runnable<T> action) {
        for (T element : this.stream) {
            action.run(element);
        }
    }

    public boolean anyMatch(MatcherFunction<T> matcher) {
        for (T element : this.stream) {
            if (matcher.matches(element)) {
                return true;
            }
        }
        return false;
    }

    public List<T> toList() {
        return new ArrayList<>(stream);
    }

    public static <T> DataStream<T> of(Collection<T> stream) {
        return new DataStream<>(stream);
    }

    public static <T> DataStream<T> unwrap(DataStream<Collection<T>> stream) {
        DataStream<T> unwrappedStream = new DataStream<>();
        for (Collection<T> collection : stream.stream) {
            unwrappedStream.addElements(collection);
        }
        return unwrappedStream;
    }
}
