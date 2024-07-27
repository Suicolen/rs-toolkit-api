package api.definition.model;

import org.joml.Vector3f;

public class FaceNormal {
    public int x;
    public int y;
    public int z;

    public Vector3f toVector3f() {
        return new Vector3f(x, y, z);
    }
}