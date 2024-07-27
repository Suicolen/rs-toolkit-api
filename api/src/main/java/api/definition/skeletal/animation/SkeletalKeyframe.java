package api.definition.skeletal.animation;

import lombok.Data;

@Data
public class SkeletalKeyframe {

    private int time;
    private float value;
    private float tanInX = Float.MAX_VALUE;
    private float tanInY = Float.MAX_VALUE;
    private float tanOutX = Float.MAX_VALUE;
    private float tanOutY = Float.MAX_VALUE;

    private SkeletalKeyframe next;

    public SkeletalKeyframe() {
    }
}