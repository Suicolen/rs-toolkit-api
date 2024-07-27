package api.definition.skeletal.animation;

import lombok.Data;

@Data
public class AnimationBoneWrapper {
    private AnimationBone[] bones;

    private int maxConnections;

    public AnimationBoneWrapper() {

    }

    public void postDecode() {
        try {
            this.initParents();
        } catch (Exception e) {
            System.out.println(STR."Failed to init parents: \{e.getMessage()}");
        }
    }

    private void initParents() {
        AnimationBone[] bones = this.bones;
        for (AnimationBone bone : bones) {
            int parentId = bone.getParentId();
            if (parentId >= 0) {
                bone.setParent(this.bones[parentId]);
            }
        }
    }

    public int getBoneCount() {
        return this.bones.length;
    }

    public AnimationBone getAnimationBone(int boneIndex) {
        return this.bones[boneIndex];
    }

    public void updateTransforms(SkeletalAnimFrameset skeletalAnimFrameset, int tick) {
        this.updateTransforms(skeletalAnimFrameset, tick, null, false);
    }

    public void updateTransforms(SkeletalAnimFrameset skeletalFrameset, int tick, boolean[] mask, boolean state) {
        int frameId = skeletalFrameset.getFrameId();
        int transformIndex = 0;
        AnimationBone[] bones = this.getBones();
        for (AnimationBone bone : bones) {
            if (mask == null || state == mask[transformIndex]) {
                skeletalFrameset.update(tick, bone, transformIndex, frameId);
            }
            transformIndex++;
        }
    }
}