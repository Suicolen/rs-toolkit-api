package api.math.geometry;

import javafx.geometry.Point3D;

public record Triangle(Point3D v1, Point3D v2, Point3D v3) {

    public Point3D computeCentroid() {
        double cx = (v1.getX() + v2.getX() + v3.getX()) / 3.0;
        double cy = (v1.getY() + v2.getY() + v3.getY()) / 3.0;
        double cz = (v1.getZ() + v2.getZ() + v3.getZ()) / 3.0;
        return new Point3D(cx, cy, cz);
    }

    public double computeArea() {
        return 0.5 * computeNormal().magnitude();
    }

    public Point3D computeNormal() {
        Point3D u = v2.subtract(v1);
        Point3D v = v3.subtract(v1);
        return u.crossProduct(v);
    }

}
