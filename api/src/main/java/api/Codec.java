package api;

public interface Codec<I, O> {
    void decode(I input, O definition);
    byte[] encode(O definition);
}