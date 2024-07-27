package api.definition.skeletal.animation;

import api.definition.skeletal.misc.SerialEnum;

public enum CurveOperandType implements SerialEnum {
    NULL(0, 0, null, 0),
    VERTEX(1, 1, null, 9),
    BILLBOARD(2, 2, null, 3),
    COLOR(3, 3, null, 6),
    TRANS(4, 4, null, 1),
    field1452(5, 5, null, 3);

    private final int serialId;

    private final int id;

    private final int dimensions;

    CurveOperandType(int serialId, int id, String name, int dimensions) {
        this.serialId = serialId;
        this.id = id;
        this.dimensions = dimensions;
    }

    private static final CurveOperandType[] VALUES = values();

    public static CurveOperandType lookUpById(int id) {
        CurveOperandType type = SerialEnum.findEnumerated(VALUES, id);
        if (type == null) {
            type = CurveOperandType.NULL;
        }
        return type;
    }

    public int id() {
        return this.id;
    }

    public int getDimensions() {
        return this.dimensions;
    }
}