package net.justonedev.model.stream;

/**
 * The matcher function. Maps an element of type T to a boolean, used in streams for matching conditions, so seeing if
 * any elements match a given condition.
 * @author uwwfh
 * @param <T> The type of the stream elements.
 */
public interface MatcherFunction<T> {
    /**
     * The matching function. Should return true if the element matches, false if not. Used in anyMatch,
     * which will look up if this function returns true for any element.
     * @param element The element.
     * @return True if it matches the condition provided by this method, false if not.
     */
    boolean matches(T element);
}
