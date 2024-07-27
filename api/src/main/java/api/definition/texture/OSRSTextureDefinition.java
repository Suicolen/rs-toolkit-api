package api.definition.texture;

import lombok.Data;

import java.util.Arrays;

@Data
public class OSRSTextureDefinition {
    private int rgb;
    private boolean opaque;
    private int id;
    private int[] spriteGroupIds;
    private int[] field1780;
    private int[] field1781;
    private int[] field1786;
    private int animationSpeed;
    private int animationDirection;
    private int[] pixels;

    public OSRSTextureDefinition copy() {
        OSRSTextureDefinition copy = new OSRSTextureDefinition();
        copy.rgb = rgb;
        copy.opaque = opaque;
        copy.id = id;
        copy.spriteGroupIds = spriteGroupIds != null ? Arrays.copyOf(spriteGroupIds, spriteGroupIds.length) : null;
        copy.field1780 = field1780 != null ? Arrays.copyOf(field1780, field1780.length) : null;
        copy.field1781 = field1781 != null ? Arrays.copyOf(field1781, field1781.length) : null;
        copy.field1786 = field1786 != null ? Arrays.copyOf(field1786, field1786.length) : null;
        copy.animationSpeed = animationSpeed;
        copy.animationDirection = animationDirection;
        copy.pixels = pixels != null ? Arrays.copyOf(pixels, pixels.length) : null;
        return copy;
    }
}
