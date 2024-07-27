package api.definition.skeletal.animation;

import api.definition.skeletal.math.Matrix4f;
import api.definition.skeletal.math.Vector3f;
import lombok.Data;
@Data
public class AnimationBone {

    private int parentId;

    private AnimationBone parent;
    // this isn't used anywhere in the engine so no clue what this could be.
    private float[][] field1404;
    private Matrix4f[] localMatrices;
    private Matrix4f[] globalMatrices;
    private Matrix4f[] inverseGlobalMatrices;
    private Matrix4f currentLocalMatrix = new Matrix4f();
    private boolean shouldComputeCurrentGlobalMatrix = true;
    private Matrix4f currentGlobalMatrixTmp = new Matrix4f();
    private boolean shouldComputeCurrentBoneMatrix = true;
    private Matrix4f currentBoneMatrix = new Matrix4f();
    private float[][] eulerAngles;
    private float[][] translations;
    private float[][] scales;

    /**
     * Added
     */

    private Vector3f localTranslation;

    public void cacheTRSComponents() {
        this.eulerAngles = new float[this.localMatrices.length][3];
        this.translations = new float[this.localMatrices.length][3];
        this.scales = new float[this.localMatrices.length][3];
        Matrix4f matrix = Matrix4f.take();
        for (int index = 0; index < this.localMatrices.length; index++) {
            Matrix4f localMatrix = this.getLocalMatrix(index);
            matrix.setFrom(localMatrix);
            matrix.invert();
            this.eulerAngles[index] = matrix.getEulerAnglesXYZ();
            this.translations[index][0] = localMatrix.values[12];
            this.translations[index][1] = localMatrix.values[13];
            this.translations[index][2] = localMatrix.values[14];
            this.scales[index] = localMatrix.getScale();
            // added
            this.localTranslation = new Vector3f(translations[index][0], translations[index][1], translations[index][2]);
        }
        matrix.release();
    }

    public Matrix4f getLocalMatrix(int boneIndex) {
        return this.localMatrices[boneIndex];
    }

    public Matrix4f computeGlobalMatrix(int frameId) {
        if (this.globalMatrices[frameId] == null) {
            this.globalMatrices[frameId] = new Matrix4f(this.getLocalMatrix(frameId));
            if (this.parent != null) {
                this.globalMatrices[frameId].multiply(this.parent.computeGlobalMatrix(frameId));
            } else {
                // strange because multiplying a matrix by the identity matrix has no effect
                this.globalMatrices[frameId].multiply(Matrix4f.IDENTITY);
            }
        }
        return this.globalMatrices[frameId];
    }

    public Matrix4f getInvertedGlobalMatrix(int frameId) {
        if (this.inverseGlobalMatrices[frameId] == null) {
            this.inverseGlobalMatrices[frameId] = new Matrix4f(this.computeGlobalMatrix(frameId));
            this.inverseGlobalMatrices[frameId].invert();
        }
        return this.inverseGlobalMatrices[frameId];
    }

    public void setCurrentLocalMatrix(Matrix4f currentLocalMatrix) {
        this.currentLocalMatrix.setFrom(currentLocalMatrix);
        this.shouldComputeCurrentGlobalMatrix = true;
        this.shouldComputeCurrentBoneMatrix = true;
    }

    public Matrix4f computeCurrentGlobalMatrix() {
        if (this.shouldComputeCurrentGlobalMatrix) {
            this.currentGlobalMatrixTmp.setFrom(this.getCurrentLocalMatrix());
            if (this.parent != null) {
                this.currentGlobalMatrixTmp.multiply(this.parent.computeCurrentGlobalMatrix());
            }
            this.shouldComputeCurrentGlobalMatrix = false;
        }
        return this.currentGlobalMatrixTmp;
    }

    public Matrix4f getCurrentBoneMatrix(int frameId) {
        if (this.shouldComputeCurrentBoneMatrix) {
            this.currentBoneMatrix.setFrom(this.getInvertedGlobalMatrix(frameId));
            this.currentBoneMatrix.multiply(this.computeCurrentGlobalMatrix());
            this.shouldComputeCurrentBoneMatrix = false;
        }
        return this.currentBoneMatrix;
    }

    public float[] getEulerAnglesOther(int index) {
        return this.eulerAngles[index];
    }

    public float[] getTranslation(int index) {
        return this.translations[index];
    }

    public float[] getScale(int index) {
        return this.scales[index];
    }

    /**
     * Added
     */

    public Vector3f computeGlobalPos() {
        if (parent != null) {
            return parent.computeGlobalPos().add(localTranslation);
        }
        return localTranslation;
    }
}