package api;

/**
 * Constructs an instance of type T
 */
public interface Factory<T> {
    T construct();
}