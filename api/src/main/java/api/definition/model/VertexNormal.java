package api.definition.model;

import org.joml.Vector3f;

public class VertexNormal {
    public int x;
    public int y;
    public int z;
    public int magnitude;

    public Vector3f normalize() {
        return new Vector3f(x, y, z).normalize();
    }


    @Override
    public String toString() {
        return "VertexNormal{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", magnitude=" + magnitude +
                '}';
    }
}
