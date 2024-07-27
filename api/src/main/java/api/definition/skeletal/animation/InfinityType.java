package api.definition.skeletal.animation;

import api.definition.skeletal.misc.SerialEnum;

public enum InfinityType implements SerialEnum {

    CONSTANT(0, 0),
    LINEAR(1, 1),
    CYCLE(2, 2),
    CYCLE_RELATIVE(3, 3),
    OSCILLATE(4, 4);

    private final int ordinal;

    private final int serialId;

    InfinityType(int ordinal, int serialId) {
        this.ordinal = ordinal;
        this.serialId = serialId;
    }

    public int id() {
        return this.serialId;
    }

    public static final InfinityType[] VALUES = values();
}
