package api.definition.skeletal.animation;

import api.definition.skeletal.misc.SerialEnum;

public enum CurveTransformType implements SerialEnum {

    VERTEX_ROTATE(0, 0),
    VERTEX_TRANSLATE(1, 1),
    aClass146_1(2, 2),
    VERTEX_SCALE(3, 3),
    field1506(4, 4),
    field1507(5, 5),
    field1508(6, 6),
    field1509(7, 7),
    NULL(8, 8);

    private final int field1512;

    private final int serialId;

    CurveTransformType(int var3, int serialId) {
        this.field1512 = var3;
        this.serialId = serialId;
    }

    public int id() {
        return this.serialId;
    }

    public static final CurveTransformType[] VALUES = values();
}
