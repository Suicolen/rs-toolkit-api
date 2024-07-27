package api.definition.sequence;

import api.definition.Definition;
import lombok.Data;

import java.util.Arrays;

@Data
public class SeqFrame implements Definition {
    private int id; // frame file id
    private int frameGroupId;
    private int[] xModifier;
    private int[] yModifier;
    private int[] zModifier;
    private int[] groupIndices;
    private int groupIndicesCount;
    private SeqBase base;
    private int transformCount;
    private boolean hasAlphaTransforms;

    /**
     * 377 and below
     */

    private int delay;

    public SeqFrame copy() {
        SeqFrame copy = new SeqFrame();
        copy.id = id;
        copy.frameGroupId = frameGroupId;
        copy.xModifier = xModifier;
        copy.yModifier = yModifier;
        copy.zModifier = zModifier;
        copy.groupIndices = groupIndices;
        copy.groupIndicesCount = groupIndicesCount;
        copy.base = base;
        copy.transformCount = transformCount;
        copy.delay = delay;
        return copy;
    }

    public SeqFrame deepCopy() {
        SeqFrame copy = new SeqFrame();
        copy.id = id;
        copy.frameGroupId = frameGroupId;
        if (xModifier != null) {
            copy.xModifier = Arrays.copyOf(xModifier, xModifier.length);
        }
        if (yModifier != null) {
            copy.yModifier = Arrays.copyOf(yModifier, yModifier.length);
        }
        if (zModifier != null) {
            copy.zModifier = Arrays.copyOf(zModifier, zModifier.length);
        }
        if (groupIndices != null) {
            copy.groupIndices = Arrays.copyOf(groupIndices, groupIndices.length);
        }
        copy.groupIndicesCount = groupIndicesCount;
        copy.base = base;
        copy.transformCount = transformCount;
        copy.delay = delay;
        return copy;
    }
}