package api.definition.sequence;

import api.definition.Definition;
import api.definition.skeletal.animation.AnimationBoneWrapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Arrays;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SeqBase implements Definition {
    private int id; // group id
    @EqualsAndHashCode.Include
    private int[] types;
    @EqualsAndHashCode.Include
    private int[][] groupedLabels;
    private int length;
    private AnimationBoneWrapper animationBoneWrapper;

    /**
     * 742
     */

    private boolean[] aBoolArray7563;
    private int[] anIntArray7561;

    public SeqBase copy() {
        SeqBase copy = new SeqBase();
        copy.id = id;
        copy.types = types;
        copy.groupedLabels = groupedLabels;
        copy.length = length;
        copy.animationBoneWrapper = animationBoneWrapper;
        copy.aBoolArray7563 = aBoolArray7563;
        copy.anIntArray7561 = anIntArray7561;
        return copy;
    }

    public SeqBase deepCopy() {
        SeqBase copy = new SeqBase();
        copy.id = id;
        if (types != null) {
            copy.types = Arrays.copyOf(types, types.length);
        }
        if (groupedLabels != null) {
            copy.groupedLabels = Arrays.copyOf(groupedLabels, groupedLabels.length);
        }
        copy.length = length;
        copy.animationBoneWrapper = animationBoneWrapper;
        if (aBoolArray7563 != null) {
            copy.aBoolArray7563 = Arrays.copyOf(aBoolArray7563, aBoolArray7563.length);
        }
        if (anIntArray7561 != null) {
            copy.anIntArray7561 = Arrays.copyOf(anIntArray7561, anIntArray7561.length);
        }
        return copy;
    }

    public boolean isSkeletalAnimation() {
        return animationBoneWrapper != null;
    }
}
