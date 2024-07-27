package api.definition.skeletal.math;

public final class Quaternionf {

    private static final Quaternionf[] pool;
    private static final int poolLimit;
    private static int poolCursor;

    public static final Quaternionf IDENTITY = new Quaternionf();

    float x;
    float y;
    float w;
    float z;

    static {
        poolLimit = 100;
        pool = new Quaternionf[100];
        poolCursor = 0;
    }

    public Quaternionf() {
        this.identity();
    }

    public Quaternionf(float x, float y, float w, float z) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.z = z;
    }

    public void release() {
        synchronized (pool) {
            if (poolCursor < poolLimit - 1) {
                pool[poolCursor++] = this;
            }
        }
    }

    public static Quaternionf take() {
        synchronized (pool) {
            if (poolCursor == 0) {
                return new Quaternionf();
            } else {
                pool[--poolCursor].identity();
                return pool[poolCursor];
            }
        }
    }

    void set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    // constructs a quaternion from an axis and an angle
    // this assumes that the axis is normalized, i.e. length(axis) = 1
    // the quaternion is also normalized, i.e. cos(angle/2) ^ 2 + ax*ax * sin(angle/2) ^ 2 + ay*ay * sin(angle/2) ^ 2 + az*az * sin(angle/2)2 ^ 2 = 1
    public void fromAxisAngle(float x, float y, float z, float angle) {
        float sin = ((float) (Math.sin(angle * 0.5F)));
        float cos = ((float) (Math.cos(angle * 0.5F)));
        this.x = sin * x;
        this.y = y * sin;
        this.z = sin * z;
        this.w = cos;
    }

    void identity() {
        this.x = 0.0F;
        this.y = 0.0F;
        this.z = 0.0F;
        this.w = 1.0F;
    }

    // multiply this quaternion with another quaternion and store the result in this instance
    public void multiply(Quaternionf other) {
        this.set(other.x * this.w + other.w * this.x + other.y * this.z - other.z * this.y, other.z * this.x + this.w * other.y + (this.y * other.w - this.z * other.x), other.x * this.y + other.w * this.z - this.x * other.y + other.z * this.w, other.w * this.w - other.x * this.x - this.y * other.y - other.z * this.z);
    }

    public boolean equals(Object o) {
        if (!(o instanceof Quaternionf)) {
            return false;
        } else {
            Quaternionf other = ((Quaternionf) (o));
            return this.x == other.x && this.y == other.y && other.z == this.z && other.w == this.w;
        }
    }

    public String toString() {
        return this.x + "," + this.y + "," + this.z + "," + this.w;
    }

    public int hashCode() {
        float hashCode = 1.0F;
        hashCode = hashCode * 31.0F + this.x;
        hashCode = this.y + hashCode * 31.0F;
        hashCode = 31.0F * hashCode + this.z;
        hashCode = 31.0F * hashCode + this.w;
        return ((int) (hashCode));
    }
}