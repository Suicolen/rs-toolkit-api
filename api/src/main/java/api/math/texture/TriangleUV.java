package api.math.texture;

public record TriangleUV(float u1, float u2, float u3, float v1, float v2, float v3) {

    public boolean isBetween(float lowerBound, float upperBound) {
        return (u1 >= lowerBound && u1 <= upperBound) && (u2 >= lowerBound && u2 <= upperBound) && (u3 >= lowerBound && u3 <= upperBound)
                && (v1 >= lowerBound && v1 <= upperBound) && (v2 >= lowerBound && v2 <= upperBound) && (v3 >= lowerBound && v3 <= upperBound);
    }

    public float sum() {
        return u1 + u2 + u3 + v1 + v2 + v3;
    }
}
