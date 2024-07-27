package api.math.geometry;

import javafx.geometry.Point3D;

public record Vertex(Point3D position, double u, double v, int index) {
}
