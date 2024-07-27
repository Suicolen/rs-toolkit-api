package api.definition.model;

// TODO: refactor
public class ModelBillboard {

    public int id;
    public int face;
    // i don't know for sure if this naming is correct, alternative would be 'billboard' tho
    public int label;
    public int scalar;

    public ModelBillboard(int id, int face, int label, int scalar) {
        this.id = id;
        this.face = face;
        this.label = label;
        this.scalar = scalar;
    }

    ModelBillboard fromFace(int face) {
        return new ModelBillboard(id, face, label, scalar);
    }
}
