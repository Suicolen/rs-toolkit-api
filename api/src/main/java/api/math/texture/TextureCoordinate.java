package api.math.texture;

import api.io.InputBuffer;
import api.io.OutputBuffer;
import api.math.Vector3i;
import api.math.geometry.Quad;
import javafx.geometry.Point3D;
import lombok.AllArgsConstructor;
import lombok.Data;

// for editing purposes we want this class to be mutable
@Data
@AllArgsConstructor
public class TextureCoordinate {

    // the origin of the texture
    private Point3D p;
    // the horizontal end of the texture
    private Point3D m;
    // the vertical end of the texture
    private Point3D n;

    public TextureCoordinate copy() {
        return new TextureCoordinate(p, m, n);
    }

    public Quad createQuad() {
        // given 3 points p, m, n where
        // p is the origin, m is horizontal end of the texture and n is vertical end of the texture
        // a quad abcd can be computed like this:
        // first we gotta make 2 new variables s, t
        // s = m - p
        // t = n - p
        // then abcd can be computed
        // a = p
        // b = p + s
        // c = p + t
        // d = p + s + t
        Point3D s = m.subtract(p);
        Point3D t = n.subtract(p);
        Point3D b = p.subtract(s);
        Point3D c = p.add(t);
        Point3D d = p.add(s).add(t);
        return new Quad(p, b, c, d);
    }

    public boolean isSame(TextureCoordinate other) {
        return other != null && p.equals(other.p) && m.equals(other.m) && n.equals(other.n);
    }

    public boolean isZero() {
        return p.getX() == 0 && p.getY() == 0 && p.getZ() == 0 && m.getX() == 0 && m.getY() == 0 && m.getZ() == 0 && n.getX() == 0 && n.getY() == 0 && n.getZ() == 0;
    }

    public static TextureCoordinate decode(byte[] data) {
        InputBuffer buffer = new InputBuffer(data);
        Vector3i p = new Vector3i(buffer.g2s(), buffer.g2s(), buffer.g2s());
        Vector3i m = new Vector3i(buffer.g2s(), buffer.g2s(), buffer.g2s());
        Vector3i n = new Vector3i(buffer.g2s(), buffer.g2s(), buffer.g2s());
        return new TextureCoordinate(p.toPoint3D(), m.toPoint3D(), n.toPoint3D());
    }

    public byte[] encode() {
        OutputBuffer buffer = new OutputBuffer();
        buffer.p2((int) p.getX());
        buffer.p2((int) p.getY());
        buffer.p2((int) p.getZ());

        buffer.p2((int) m.getX());
        buffer.p2((int) m.getY());
        buffer.p2((int) m.getZ());

        buffer.p2((int) n.getX());
        buffer.p2((int) n.getY());
        buffer.p2((int) n.getZ());

        return buffer.toArray();
    }

    public void setPMN(double pX, double pY, double pZ, double mX, double mY, double mZ, double nX, double nY, double nZ) {
        p = new Point3D(pX, pY, pZ);
        m = new Point3D(mX, mY, mZ);
        n = new Point3D(nX, nY, nZ);
    }

    public static TextureCoordinate fromPMN(Point3D p, Point3D m, Point3D n) {
        return new TextureCoordinate(p, m, n);
    }

    public static TextureCoordinate fromIndividualCoordinates(double pX, double pY, double pZ, double mX, double mY, double mZ, double nX, double nY, double nZ) {
        return new TextureCoordinate(new Point3D(pX, pY, pZ), new Point3D(mX, mY, mZ), new Point3D(nX, nY, nZ));
    }

    public static TextureCoordinate fromIndividualCoordinates(Point3D p, Point3D m, Point3D n) {
        return fromIndividualCoordinates(p.getX(), p.getY(), p.getZ(), m.getX(), m.getY(), m.getZ(), n.getX(), n.getY(), n.getZ());
    }

}
