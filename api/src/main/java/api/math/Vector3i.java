package api.math;

import javafx.geometry.Point3D;

public record Vector3i(int x, int y, int z) {

    public static Vector3i ZERO = new Vector3i(0, 0, 0);

    public boolean isZero() {
        return x == 0 && y == 0 && z == 0;
    }

    public Point3D toPoint3D() {
        return new Point3D(x, y, z);
    }

    public static Vector3i fromPoint3D(Point3D point) {
        return new Vector3i((int) point.getX(), (int) point.getY(), (int) point.getZ());
    }

    public static Vector3i fromDoubles(double x, double y, double z) {
        return new Vector3i((int) x, (int) y, (int) z);
    }

    public Vector3i add(Vector3i other) {
        return new Vector3i(x + other.x, y + other.y, z + other.z);
    }

    public Vector3i sub(Vector3i other) {
        return new Vector3i(x - other.x, y - other.y, z - other.z);
    }

    public Vector3i divide(int scalar) {
        return new Vector3i(x / scalar, y / scalar, z / scalar);
    }

    public Vector3i scale(double scale) {
        return fromDoubles(x * scale, y * scale, z * scale);
    }

    public Vector3i copy() {
        return new Vector3i(x, y, z);
    }

}
