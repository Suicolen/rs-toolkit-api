package api.definition;

public interface Definition {
    int getId();
    void setId(int id);

    default Definition copy() {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " does not support copying");
    }

    default Definition deepCopy() {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " does not support deep copying");
    }

    default <T extends Definition> boolean is(Class<T> type) {
        return type.isInstance(this);
    }
    default <T> T as(Class<T> type) {
        return type.cast(this);
    }
}
