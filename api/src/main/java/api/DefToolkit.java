package api;

import java.util.Map;

public abstract class DefToolkit<T, R> implements Factory<T> {

    protected Map<Integer, T> definitions;

    public abstract Map<Integer, T> all();

    public abstract R get(int id);

    public abstract Map<Integer, T> unpack();

    public abstract void pack(Map<Integer, T> src);

    public abstract T unpackSingle(int id);
    public abstract void packSingle(T definition);

    // this should return either the highest key in definitions (0 if uninitialized)
    // or the highest archive/file id (depends on whether the data is per archive or per file) whichever is largest
    // for older caches (e.g. 317) where data isn't stored per archive/file, it should just unpack and return the highest
    // this could be optimized for 317 as well tho
    public abstract int getHighestId();

    public Map<Integer, T> getDefinitions() {
        if (definitions == null) {
            definitions = unpack();
        }
        return definitions;
    }

}
