package api.definition.spotanim;

import api.definition.Definition;
import lombok.Data;

import java.util.Arrays;

@Data
public class SpotAnim implements Definition {
    private int id;
    private int modelId;
    private int animationId;
    private int resizeX;
    private int resizeY;
    private int rotation;
    private int ambient;
    private int contrast;
    private int[] originalColors;
    private int[] targetColors;
    private int[] originalTextures;
    private int[] targetTextures;

    /**
     * 742
     */
    private int aByte6982;
    private int anInt6980;
    private boolean aBool6968;

    public SpotAnim copy() {
        SpotAnim copy = new SpotAnim();
        copy.id = id;
        copy.modelId = modelId;
        copy.animationId = animationId;
        copy.resizeX = resizeX;
        copy.resizeY = resizeY;
        copy.rotation = rotation;
        copy.ambient = ambient;
        copy.contrast = contrast;
        copy.originalColors = originalColors;
        copy.targetColors = targetColors;
        copy.originalTextures = originalTextures;
        copy.targetTextures = targetTextures;
        copy.aByte6982 = aByte6982;
        copy.anInt6980 = anInt6980;
        copy.aBool6968 = aBool6968;
        return copy;
    }

    public SpotAnim deepCopy() {
        SpotAnim copy = new SpotAnim();
        copy.id = id;
        copy.modelId = modelId;
        copy.animationId = animationId;
        copy.resizeX = resizeX;
        copy.resizeY = resizeY;
        copy.rotation = rotation;
        copy.ambient = ambient;
        copy.contrast = contrast;
        if (originalColors != null) {
            copy.originalColors = Arrays.copyOf(originalColors, originalColors.length);
        }
        if (targetColors != null) {
            copy.targetColors = Arrays.copyOf(targetColors, targetColors.length);
        }
        if (originalTextures != null) {
            copy.originalTextures = Arrays.copyOf(originalTextures, originalTextures.length);
        }
        if (targetTextures != null) {
            copy.targetTextures = Arrays.copyOf(targetTextures, targetTextures.length);
        }
        copy.aByte6982 = aByte6982;
        copy.anInt6980 = anInt6980;
        copy.aBool6968 = aBool6968;
        return copy;
    }

}
