package api.definition.skeletal.animation;

import api.definition.skeletal.misc.SerialEnum;

public enum CurveTransformComponent implements SerialEnum {
    NULL(0, 0, null, -1, -1),
    ROTATE_X(1, 1, null, 0, 2),
    ROTATE_Y(2, 2, null, 1, 2),
    ROTATE_Z(3, 3, null, 2, 2),
    TRANSLATE_X(4, 4, null, 3, 1),
    TRANSLATE_Y(5, 5, null, 4, 1),
    TRANSLATE_Z(6, 6, null, 5, 1),
    SCALE_X(7, 7, null, 6, 3),
    SCALE_Y(8, 8, null, 7, 3),
    SCALE_Z(9, 9, null, 8, 3),
    COLOR_H(10, 10, null, 0, 7),
    COLOR_S(11, 11, null, 1, 7),
    COLOR_L(12, 12, null, 2, 7),
    COLOR_R(13, 13, null, 3, 7),
    COLOR_G(14, 14, null, 4, 7),
    COLOR_B(15, 15, null, 5, 7),
    TRANSPARENCY(16, 16, null, 0, 5);

    private final int ordinal;

    private final int serialId;

    private final int component;

    CurveTransformComponent(int ordinal, int serialId, String unknown1, int component, int unknown2) {
        this.ordinal = ordinal;
        this.serialId = serialId;
        this.component = component;
    }


    public int id() {
        return this.serialId;
    }

    public int component() {
        return this.component;
    }

    public static final CurveTransformComponent[] VALUES = values();

    public static CurveTransformComponent lookUpCurveComponentById(int id) {
        CurveTransformComponent curveTransformComponent = SerialEnum.findEnumerated(CurveTransformComponent.VALUES, id);
        if (curveTransformComponent == null) {
            curveTransformComponent = CurveTransformComponent.NULL;
        }
        return curveTransformComponent;
    }
}