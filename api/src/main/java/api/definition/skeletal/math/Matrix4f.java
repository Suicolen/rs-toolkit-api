package api.definition.skeletal.math;

import java.util.Arrays;

public final class Matrix4f {

    public static final Matrix4f IDENTITY;

    public float[] values = new float[16];

    private static final Matrix4f[] pool;
    private static final int poolLimit;
    private static int poolCursor;

    static {
        pool = new Matrix4f[100];
        poolLimit = 100;
        poolCursor = 0;
        IDENTITY = new Matrix4f();
    }

    public Matrix4f() {
        this.identity();
    }

    public Matrix4f(Matrix4f matrix) {
        this.setFrom(matrix);
    }

    public void release() {
        synchronized (pool) {
            if (poolCursor < poolLimit - 1) {
                pool[poolCursor++] = this;
            }
        }
    }

    public static Matrix4f take() {
        synchronized (pool) {
            if (poolCursor == 0) {
                return new Matrix4f();
            } else {
                pool[--poolCursor].identity();
                return pool[poolCursor];
            }
        }
    }

    // the 2 could possibly be reversed, i don't know for sure yet
    public float[] getEulerAnglesZYX() {
        float[] eulerAngles = new float[3];
        if (this.values[2] < 0.999 && this.values[2] > -0.999) { // singularity checks
            eulerAngles[1] = (float) -Math.asin(this.values[2]);
            double c = Math.cos(eulerAngles[1]);
            eulerAngles[0] = (float) Math.atan2(this.values[6] / c, this.values[10] / c);
            eulerAngles[2] = (float) Math.atan2(this.values[1] / c, this.values[0] / c);
        } else {
            eulerAngles[0] = 0.0F;
            eulerAngles[1] = (float) Math.atan2(this.values[2], 0.0);
            eulerAngles[2] = (float) Math.atan2(-this.values[9], this.values[5]);
        }
        return eulerAngles;
    }
    
    public float[] getEulerAnglesXYZ() {
        float[] eulerAngles = new float[]{((float) (-Math.asin(this.values[6]))), 0.0F, 0.0F};
        double cosPitch = Math.cos(eulerAngles[0]);
        double sinPitch;
        double sinYaw;
        // singularity checks
        if (Math.abs(cosPitch) > 0.005) {
            sinPitch = this.values[2];
            sinYaw = this.values[10];
            double sinRoll = this.values[4];
            double cosRoll = this.values[5];
            eulerAngles[1] = (float) (Math.atan2(sinPitch, sinYaw));
            eulerAngles[2] = (float) (Math.atan2(sinRoll, cosRoll));
        } else {
            sinPitch = this.values[1];
            sinYaw = this.values[0];
            if (this.values[6] < 0.0F) {
                eulerAngles[1] = (float) (Math.atan2(sinPitch, sinYaw));
            } else {
                eulerAngles[1] = (float) (-Math.atan2(sinPitch, sinYaw));
            }
            eulerAngles[2] = 0.0F;
        }
        return eulerAngles;
    }

    public void identity() {
        this.values[0] = 1.0F;
        this.values[1] = 0.0F;
        this.values[2] = 0.0F;
        this.values[3] = 0.0F;
        this.values[4] = 0.0F;
        this.values[5] = 1.0F;
        this.values[6] = 0.0F;
        this.values[7] = 0.0F;
        this.values[8] = 0.0F;
        this.values[9] = 0.0F;
        this.values[10] = 1.0F;
        this.values[11] = 0.0F;
        this.values[12] = 0.0F;
        this.values[13] = 0.0F;
        this.values[14] = 0.0F;
        this.values[15] = 1.0F;
    }

    public void zero() {
        this.values[0] = 0.0F;
        this.values[1] = 0.0F;
        this.values[2] = 0.0F;
        this.values[3] = 0.0F;
        this.values[4] = 0.0F;
        this.values[5] = 0.0F;
        this.values[6] = 0.0F;
        this.values[7] = 0.0F;
        this.values[8] = 0.0F;
        this.values[9] = 0.0F;
        this.values[10] = 0.0F;
        this.values[11] = 0.0F;
        this.values[12] = 0.0F;
        this.values[13] = 0.0F;
        this.values[14] = 0.0F;
        this.values[15] = 0.0F;
    }

    public void setFrom(Matrix4f m) {
        System.arraycopy(m.values, 0, this.values, 0, 16);
    }

    public void scale(float scale) {
        this.scale(scale, scale, scale);
    }

    public void scale(float x, float y, float z) {
        this.identity();
        this.values[0] = x;
        this.values[5] = y;
        this.values[10] = z;
    }

    public void add(Matrix4f other) {
        for (int index = 0; index < this.values.length; index++) {
            float[] matrix = this.values;
            matrix[index] += other.values[index];
        }
    }

    public void testMultiply(Matrix4f other) {
        Matrix4f result = new Matrix4f();

        for (int i = 0; i < 16; i += 4) {
            for (int j = 0; j < 4; j++) {
                result.values[i + j] = 0f;
                for (int k = 0; k < 4; k++)
                    result.values[i + j] += (values[i + k] * other.values[k * 4 + j]);
            }
        }

        setFrom(result);
    }

    public void multiply(Matrix4f other) {
        /*if (true) {
            testMultiply(other);
            return;
        }*/
        float m00 = this.values[2] * other.values[8] + this.values[1] * other.values[4] + this.values[0] * other.values[0] + other.values[12] * this.values[3];
        float m01 = this.values[3] * other.values[13] + other.values[9] * this.values[2] + this.values[0] * other.values[1] + other.values[5] * this.values[1];
        float m02 = this.values[2] * other.values[10] + this.values[1] * other.values[6] + other.values[2] * this.values[0] + other.values[14] * this.values[3];
        float m03 = this.values[3] * other.values[15] + other.values[11] * this.values[2] + this.values[0] * other.values[3] + this.values[1] * other.values[7];
        float m10 = this.values[7] * other.values[12] + other.values[0] * this.values[4] + other.values[4] * this.values[5] + other.values[8] * this.values[6];
        float m11 = this.values[7] * other.values[13] + other.values[1] * this.values[4] + this.values[5] * other.values[5] + other.values[9] * this.values[6];
        float m12 = this.values[7] * other.values[14] + other.values[6] * this.values[5] + this.values[4] * other.values[2] + this.values[6] * other.values[10];
        float m13 = other.values[15] * this.values[7] + this.values[6] * other.values[11] + this.values[5] * other.values[7] + other.values[3] * this.values[4];
        float m20 = this.values[9] * other.values[4] + this.values[8] * other.values[0] + this.values[10] * other.values[8] + this.values[11] * other.values[12];
        float m21 = other.values[9] * this.values[10] + this.values[8] * other.values[1] + other.values[5] * this.values[9] + this.values[11] * other.values[13];
        float m22 = other.values[2] * this.values[8] + this.values[9] * other.values[6] + other.values[10] * this.values[10] + this.values[11] * other.values[14];
        float m23 = other.values[15] * this.values[11] + other.values[11] * this.values[10] + other.values[7] * this.values[9] + this.values[8] * other.values[3];
        float m30 = this.values[15] * other.values[12] + other.values[4] * this.values[13] + other.values[0] * this.values[12] + this.values[14] * other.values[8];
        float m31 = this.values[12] * other.values[1] + other.values[5] * this.values[13] + this.values[14] * other.values[9] + other.values[13] * this.values[15];
        float m32 = other.values[10] * this.values[14] + this.values[13] * other.values[6] + other.values[2] * this.values[12] + other.values[14] * this.values[15];
        float m33 = other.values[15] * this.values[15] + other.values[3] * this.values[12] + this.values[13] * other.values[7] + other.values[11] * this.values[14];
        set(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33);
    }

    public void setFromQuaternion(Quaternionf quaternion) {
        float ww = quaternion.w * quaternion.w;
        float wx = quaternion.w * quaternion.x;
        float yw = quaternion.y * quaternion.w;
        float wz = quaternion.w * quaternion.z;
        float xx = quaternion.x * quaternion.x;
        float xy = quaternion.x * quaternion.y;
        float zx = quaternion.z * quaternion.x;
        float yy = quaternion.y * quaternion.y;
        float zy = quaternion.z * quaternion.y;
        float zz = quaternion.z * quaternion.z;
        this.values[0] = ww + xx - zz - yy;
        this.values[1] = wz + xy + xy + wz;
        this.values[2] = zx - yw - yw + zx;
        this.values[4] = xy + (xy - wz - wz);
        this.values[5] = ww + yy - xx - zz;
        this.values[6] = wx + wx + zy + zy;
        this.values[8] = zx + yw + zx + yw;
        this.values[9] = zy + (zy - wx - wx);
        this.values[10] = zz + ww - yy - xx;
    }

    public void setFrom4x3(Matrix4x3f matrix4x3) {
        this.values[0] = matrix4x3.m00;
        this.values[1] = matrix4x3.m10;
        this.values[2] = matrix4x3.m20;
        this.values[3] = 0.0F;
        this.values[4] = matrix4x3.m01;
        this.values[5] = matrix4x3.m11;
        this.values[6] = matrix4x3.m21;
        this.values[7] = 0.0F;
        this.values[8] = matrix4x3.m02;
        this.values[9] = matrix4x3.m12;
        this.values[10] = matrix4x3.m22;
        this.values[11] = 0.0F;
        this.values[12] = matrix4x3.m03;
        this.values[13] = matrix4x3.m13;
        this.values[14] = matrix4x3.m23;
        this.values[15] = 1.0F;
    }

    public float determinant() {
        return this.values[10] * this.values[4] * this.values[3] * this.values[13] + (this.values[13] * this.values[8] * this.values[7] * this.values[2] + this.values[12] * this.values[5] * this.values[2] * this.values[11] + (this.values[15] * this.values[4] * this.values[2] * this.values[9] + this.values[12] * this.values[1] * this.values[7] * this.values[10] + (this.values[14] * this.values[1] * this.values[4] * this.values[11] + (this.values[13] * this.values[6] * this.values[0] * this.values[11] + (this.values[10] * this.values[0] * this.values[5] * this.values[15] - this.values[5] * this.values[0] * this.values[11] * this.values[14] - this.values[15] * this.values[9] * this.values[0] * this.values[6]) + this.values[14] * this.values[0] * this.values[7] * this.values[9] - this.values[13] * this.values[7] * this.values[0] * this.values[10] - this.values[15] * this.values[4] * this.values[1] * this.values[10]) + this.values[1] * this.values[6] * this.values[8] * this.values[15] - this.values[12] * this.values[11] * this.values[1] * this.values[6] - this.values[14] * this.values[8] * this.values[1] * this.values[7]) - this.values[13] * this.values[11] * this.values[2] * this.values[4] - this.values[15] * this.values[5] * this.values[2] * this.values[8]) - this.values[7] * this.values[2] * this.values[9] * this.values[12] - this.values[14] * this.values[9] * this.values[4] * this.values[3]) + this.values[14] * this.values[3] * this.values[5] * this.values[8] - this.values[12] * this.values[3] * this.values[5] * this.values[10] - this.values[8] * this.values[6] * this.values[3] * this.values[13] + this.values[12] * this.values[3] * this.values[6] * this.values[9];
    }

    public void invert() {
        float det = 1.0F / this.determinant();
        float m00 = det * (this.values[15] * this.values[10] * this.values[5] - this.values[11] * this.values[5] * this.values[14] - this.values[9] * this.values[6] * this.values[15] + this.values[13] * this.values[11] * this.values[6] + this.values[14] * this.values[9] * this.values[7] - this.values[13] * this.values[7] * this.values[10]);
        float m01 = (this.values[13] * this.values[3] * this.values[10] + (this.values[14] * this.values[1] * this.values[11] + -this.values[1] * this.values[10] * this.values[15] + this.values[2] * this.values[9] * this.values[15] - this.values[13] * this.values[2] * this.values[11] - this.values[14] * this.values[9] * this.values[3])) * det;
        float m02 = (this.values[13] * this.values[2] * this.values[7] + (this.values[1] * this.values[6] * this.values[15] - this.values[14] * this.values[7] * this.values[1] - this.values[5] * this.values[2] * this.values[15]) + this.values[3] * this.values[5] * this.values[14] - this.values[13] * this.values[6] * this.values[3]) * det;
        float m03 = det * (this.values[11] * this.values[2] * this.values[5] + this.values[7] * this.values[1] * this.values[10] + -this.values[1] * this.values[6] * this.values[11] - this.values[9] * this.values[7] * this.values[2] - this.values[10] * this.values[3] * this.values[5] + this.values[3] * this.values[6] * this.values[9]);
        float m10 = det * (this.values[15] * this.values[6] * this.values[8] + -this.values[4] * this.values[10] * this.values[15] + this.values[11] * this.values[4] * this.values[14] - this.values[11] * this.values[6] * this.values[12] - this.values[14] * this.values[8] * this.values[7] + this.values[12] * this.values[10] * this.values[7]);
        float m11 = det * (this.values[14] * this.values[3] * this.values[8] + this.values[15] * this.values[0] * this.values[10] - this.values[14] * this.values[11] * this.values[0] - this.values[8] * this.values[2] * this.values[15] + this.values[2] * this.values[11] * this.values[12] - this.values[10] * this.values[3] * this.values[12]);
        float m12 = (this.values[6] * this.values[3] * this.values[12] + (this.values[15] * this.values[2] * this.values[4] + this.values[14] * this.values[7] * this.values[0] + this.values[15] * -this.values[0] * this.values[6] - this.values[2] * this.values[7] * this.values[12] - this.values[3] * this.values[4] * this.values[14])) * det;
        float m13 = (this.values[4] * this.values[3] * this.values[10] + this.values[0] * this.values[6] * this.values[11] - this.values[0] * this.values[7] * this.values[10] - this.values[4] * this.values[2] * this.values[11] + this.values[8] * this.values[7] * this.values[2] - this.values[3] * this.values[6] * this.values[8]) * det;
        float m20 = det * (this.values[15] * this.values[9] * this.values[4] - this.values[4] * this.values[11] * this.values[13] - this.values[15] * this.values[5] * this.values[8] + this.values[5] * this.values[11] * this.values[12] + this.values[13] * this.values[8] * this.values[7] - this.values[12] * this.values[7] * this.values[9]);
        float m21 = det * (this.values[9] * this.values[3] * this.values[12] + (this.values[9] * -this.values[0] * this.values[15] + this.values[13] * this.values[11] * this.values[0] + this.values[1] * this.values[8] * this.values[15] - this.values[11] * this.values[1] * this.values[12] - this.values[13] * this.values[3] * this.values[8]));
        float m22 = det * (this.values[5] * this.values[0] * this.values[15] - this.values[7] * this.values[0] * this.values[13] - this.values[15] * this.values[4] * this.values[1] + this.values[12] * this.values[1] * this.values[7] + this.values[3] * this.values[4] * this.values[13] - this.values[12] * this.values[5] * this.values[3]);
        float m23 = (this.values[8] * this.values[5] * this.values[3] + (this.values[9] * this.values[7] * this.values[0] + this.values[11] * -this.values[0] * this.values[5] + this.values[11] * this.values[1] * this.values[4] - this.values[7] * this.values[1] * this.values[8] - this.values[9] * this.values[4] * this.values[3])) * det;
        float m30 = (this.values[6] * this.values[9] * this.values[12] + (this.values[14] * this.values[8] * this.values[5] + this.values[14] * -this.values[4] * this.values[9] + this.values[13] * this.values[10] * this.values[4] - this.values[10] * this.values[5] * this.values[12] - this.values[13] * this.values[8] * this.values[6])) * det;
        float m31 = (this.values[2] * this.values[8] * this.values[13] + this.values[12] * this.values[10] * this.values[1] + (this.values[9] * this.values[0] * this.values[14] - this.values[13] * this.values[0] * this.values[10] - this.values[8] * this.values[1] * this.values[14]) - this.values[9] * this.values[2] * this.values[12]) * det;
        float m32 = (this.values[13] * this.values[0] * this.values[6] + -this.values[0] * this.values[5] * this.values[14] + this.values[14] * this.values[4] * this.values[1] - this.values[12] * this.values[6] * this.values[1] - this.values[13] * this.values[4] * this.values[2] + this.values[2] * this.values[5] * this.values[12]) * det;
        float m33 = (this.values[5] * this.values[0] * this.values[10] - this.values[6] * this.values[0] * this.values[9] - this.values[4] * this.values[1] * this.values[10] + this.values[8] * this.values[1] * this.values[6] + this.values[2] * this.values[4] * this.values[9] - this.values[5] * this.values[2] * this.values[8]) * det;
        set(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33);
    }

    public void set(float m00, float m01, float m02, float m03, float m10, float m11, float m12, float m13, float m20, float m21, float m22, float m23, float m30, float m31, float m32, float m33) {
        this.values[0] = m00;
        this.values[1] = m01;
        this.values[2] = m02;
        this.values[3] = m03;
        this.values[4] = m10;
        this.values[5] = m11;
        this.values[6] = m12;
        this.values[7] = m13;
        this.values[8] = m20;
        this.values[9] = m21;
        this.values[10] = m22;
        this.values[11] = m23;
        this.values[12] = m30;
        this.values[13] = m31;
        this.values[14] = m32;
        this.values[15] = m33;
    }

    public float[] getScale() {
        float[] scale = new float[3];
        Vector3f x = new Vector3f(this.values[0], this.values[1], this.values[2]);
        Vector3f y = new Vector3f(this.values[4], this.values[5], this.values[6]);
        Vector3f z = new Vector3f(this.values[8], this.values[9], this.values[10]);
        scale[0] = x.length();
        scale[1] = y.length();
        scale[2] = z.length();
        return scale;
    }

    // added
    public float[] getTranslation() {
        return new float[]{values[12], values[13], values[14]};
    }

    // added
    public float[] transformVertex(float[] vertex) {
        float x = vertex[0];
        float y = -vertex[1];
        float z = -vertex[2];

        // The position is treated as a 4D vector with it's w-component being 1.0, that means that it will represent a position/location in 3d space
        // rather than a direction
        float w = 1.0F;
        int newX = ((int) (values[0] * x + values[4] * y + values[8] * z + values[12] * w));
        int newY = -((int) (values[1] * x + values[5] * y + values[9] * z + values[13] * w));
        int newZ = -((int) (values[2] * x + values[6] * y + values[10] * z + values[14] * w));

        return new float[]{newX, newY, newZ};
    }

    public Vector3f transformVertex(Vector3f vertex) {
        float x = vertex.x;
        float y = -vertex.y;
        float z = -vertex.z;

        // The position is treated as a 4D vector with it's w-component being 1.0, that means that it will represent a position/location in 3d space
        // rather than a direction
        float w = 1.0F;
        float newX = ((values[0] * x + values[4] * y + values[8] * z + values[12] * w));
        float newY = -((values[1] * x + values[5] * y + values[9] * z + values[13] * w));
        float newZ = -((values[2] * x + values[6] * y + values[10] * z + values[14] * w));

        return new Vector3f(newX, newY, newZ);
    }

    public boolean equals(Object o) {
        if (!(o instanceof Matrix4f)) {
            return false;
        } else {
            Matrix4f other = ((Matrix4f) (o));
            for (int var3 = 0; var3 < 16; ++var3) {
                if (other.values[var3] != this.values[var3]) {
                    return false;
                }
            }
            return true;
        }
    }

    public int hashCode() {
        return 31 + Arrays.hashCode(this.values);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        this.getEulerAnglesXYZ();
        this.getEulerAnglesZYX();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (j > 0) {
                    sb.append("\t");
                }
                float value = this.values[j + i * 4];
                if (Math.sqrt(value * value) < 9.999999747378752E-5) {
                    value = 0.0F;
                }
                sb.append(value);
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}