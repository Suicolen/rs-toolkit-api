package api.math.geometry;

import api.math.texture.TextureCoordinate;
import javafx.geometry.Point3D;

public record Quad(Point3D v1, Point3D v2, Point3D v3, Point3D v4) {

    /**
     * @see TextureCoordinate#createQuad()
     */
    public TextureCoordinate toTextureCoordinate() {
        Point3D p = v1;
        Point3D s = v2.subtract(v1);
        Point3D t = v3.subtract(v1);
        Point3D m = p.add(s);
        Point3D n = p.add(t);
        return new TextureCoordinate(p, m, n);
    }

}

