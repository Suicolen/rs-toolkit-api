package api;

import java.util.Map;

public interface Packer<T> {
    void pack(Codec<?, T> encoder, Map<Integer, T> src);
    void packSingle(Codec<?, T> encoder, T definition);
}
