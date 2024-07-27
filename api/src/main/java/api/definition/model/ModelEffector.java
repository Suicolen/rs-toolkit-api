package api.definition.model;

public class ModelEffector {

    public int id;
    public int vertex;
    public int x;
    public int z;
    public int y;
    public ModelEffector unknown1;

    public ModelEffector(int id, int vertex) {
        this.id = id;
        this.vertex = vertex;
    }

    public ModelEffector transform(int vertex) {
        return new ModelEffector(id, vertex);
    }
}
