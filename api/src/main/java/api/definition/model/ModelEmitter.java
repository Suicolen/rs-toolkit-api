package api.definition.model;

public class ModelEmitter {

    public int id;
    public int face;
    public int vertexA;
    public int vertexB;
    public int vertexC;
    public byte priority;
    public int x3;
    public ModelEmitter unknown1;
    public int y3;
    public int y2;
    public int x1;
    public int z1;
    public int x2;
    public int y1;
    public int z2;
    public int z3;

    public ModelEmitter(int id, int face, int vertexA, int vertexB, int vertexC, byte priority) {
        this.id = id;
        this.face = face;
        this.vertexA = vertexA;
        this.vertexB = vertexB;
        this.vertexC = vertexC;
        this.priority = priority;
    }

    public ModelEmitter transform(int face, int a, int b, int c) {
        return new ModelEmitter(id, face, a, b, c, priority);
    }
}
