package api;

import java.util.Map;

public interface Unpacker<I, T> {
    Map<Integer, T> unpack(Codec<I, T> decoder, Factory<T> factory);
    T unpackSingle(int id, Codec<I, T> decoder, Factory<T> factory);
}
