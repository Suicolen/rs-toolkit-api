package api.definition.skeletal.animation;

import api.definition.sequence.SeqBase;
import api.definition.skeletal.math.Matrix4f;
import api.definition.skeletal.math.Quaternionf;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SkeletalAnimFrameset {

    private int skeletalId;
    private int version;

    // transforms that work per triangle (e.g. transparency)
    private AnimationCurve[][] triangleTransformCurves = null;
    // transforms that work per vertex (e.g. translation, rotation, scale)
    private AnimationCurve[][] vertexTransformCurves = null;

    private CurveOperandType[] operandTypes;

    private SeqBase base;

    private int frameId = 0;

    private boolean hasAlphaTransforms;

    public boolean hasAlphaTransforms() {
        return this.hasAlphaTransforms;
    }

    // new fields for encoding purposes
    private int unknown1;
    private int unknown2;
    private int transformCount;

    public void update(int tick, AnimationBone bone, int transformIndex, int frameId) {
        Matrix4f matrix = Matrix4f.take();
        this.updateRotation(matrix, transformIndex, bone, tick);
        this.updateScale(matrix, transformIndex, bone, tick);
        this.updateTranslation(matrix, transformIndex, bone, tick);
        bone.setCurrentLocalMatrix(matrix);
        matrix.release();
    }

    public void updateRotation(Matrix4f matrix, int transformIndex, AnimationBone bone, int tick) {
        float[] eulerAngles = bone.getEulerAnglesOther(this.frameId);
        float eulerX = eulerAngles[0];
        float eulerY = eulerAngles[1];
        float eulerZ = eulerAngles[2];
        if (this.vertexTransformCurves[transformIndex] != null) {
            AnimationCurve xCurve = this.vertexTransformCurves[transformIndex][0];
            AnimationCurve yCurve = this.vertexTransformCurves[transformIndex][1];
            AnimationCurve zCurve = this.vertexTransformCurves[transformIndex][2];
            if (xCurve != null) {
                eulerX = xCurve.getValue(tick);
            }
            if (yCurve != null) {
                eulerY = yCurve.getValue(tick);
            }
            if (zCurve != null) {
                eulerZ = zCurve.getValue(tick);
            }
        }
        Quaternionf xRotationQuaternion = Quaternionf.take();
        xRotationQuaternion.fromAxisAngle(1.0F, 0.0F, 0.0F, eulerX);
        Quaternionf yRotationQuaternion = Quaternionf.take();
        yRotationQuaternion.fromAxisAngle(0.0F, 1.0F, 0.0F, eulerY);
        Quaternionf zRotationQuaternion = Quaternionf.take();
        zRotationQuaternion.fromAxisAngle(0.0F, 0.0F, 1.0F, eulerZ);

        Quaternionf finalRotationQuaternion = Quaternionf.take();
        // rz * rx * ry
        finalRotationQuaternion.multiply(zRotationQuaternion);
        finalRotationQuaternion.multiply(xRotationQuaternion);
        finalRotationQuaternion.multiply(yRotationQuaternion);

        Matrix4f rotationMatrix = Matrix4f.take();
        rotationMatrix.setFromQuaternion(finalRotationQuaternion);
        matrix.multiply(rotationMatrix);

        xRotationQuaternion.release();
        yRotationQuaternion.release();
        zRotationQuaternion.release();
        finalRotationQuaternion.release();
        rotationMatrix.release();
    }

    public void updateTranslation(Matrix4f matrix, int transformIndex, AnimationBone bone, int tick) {
        float[] translation = bone.getTranslation(this.frameId);
        float x = translation[0];
        float y = translation[1];
        float z = translation[2];
        if (this.vertexTransformCurves[transformIndex] != null) {
            AnimationCurve xCurve = this.vertexTransformCurves[transformIndex][3];
            AnimationCurve yCurve = this.vertexTransformCurves[transformIndex][4];
            AnimationCurve zCurve = this.vertexTransformCurves[transformIndex][5];
            if (xCurve != null) {
                x = xCurve.getValue(tick);
            }
            if (yCurve != null) {
                y = yCurve.getValue(tick);
            }
            if (zCurve != null) {
                z = zCurve.getValue(tick);
            }
        }
        matrix.values[12] = x;
        matrix.values[13] = y;
        matrix.values[14] = z;
    }

    public void updateScale(Matrix4f matrix, int transformIndex, AnimationBone bone, int tick) {
        float[] scale = bone.getScale(this.frameId);
        float x = scale[0];
        float y = scale[1];
        float z = scale[2];
        if (this.vertexTransformCurves[transformIndex] != null) {
            AnimationCurve xCurve = this.vertexTransformCurves[transformIndex][6];
            AnimationCurve yCurve = this.vertexTransformCurves[transformIndex][7];
            AnimationCurve zCurve = this.vertexTransformCurves[transformIndex][8];
            if (xCurve != null) {
                x = xCurve.getValue(tick);
            }
            if (yCurve != null) {
                y = yCurve.getValue(tick);
            }
            if (zCurve != null) {
                z = zCurve.getValue(tick);
            }
        }
        Matrix4f scaleMatrix = Matrix4f.take();
        scaleMatrix.scale(x, y, z);
        matrix.multiply(scaleMatrix);
        scaleMatrix.release();
    }
}