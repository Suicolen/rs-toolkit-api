package api.definition.skeletal.math;

public class FloatMaths {
    // in the skeletal anim code this is used as the epsilon
    public static final float epsilon = Math.ulp(1.0F);
    public static final float dblEpsilon;
    public static final float oneThird = 1f / 3f;
    public static final float twoThirds = 2f / 3f;
    public static final float fourThirds = 4f / 3f;

    static {
        dblEpsilon = epsilon * 2.0F;
    }
}
