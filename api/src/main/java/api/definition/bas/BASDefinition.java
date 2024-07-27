package api.definition.bas;

import api.definition.Definition;
import lombok.Data;

@Data
public class BASDefinition implements Definition {
    private int id;
    private EquipmentDefaults equipmentDefaults;
    private int standAnim = -1;
    private int walkAnim = -1;
    private int crawlAnim = -1;
    private int crawlFollow180Anim = -1;
    private int crawlFollowCcwAnim = -1;
    private int crawlFollowCwAnim = -1;
    private int runAnim = -1;
    private int runFollow180Anim = -1;
    private int runFollowCcwAnim = -1;
    private int runFollowCwAnim = -1;
    private int someSortOfTileXOffset;
    private int someSortOfTileYOffset;
    private int[][] modelRotateTranslate;
    private int[] anIntArray2811;
    private int yawAcceleration;
    private int yawMaxSpeed;
    private int rollAcceleration;
    private int rollMaxSpeed;
    private int rollTargetAngle;
    private int pitchAcceleration;
    private int pitchMaxSpeed;
    private int pitchTargetAngle;
    private int walkSpeed = -1;
    private int standCcwAnim = -1;
    private int standCwAnim = -1;
    private int walkFollow180Anim = -1;
    private int walkFollowCcwAnim = -1;
    private int walkFollowCwAnim = -1;
    private int unknownOp43And44;
    private int UnknownOp45;
    private int crawlCwAnim = -1;
    private int crawlCcwAnim = -1;
    private int runCcwAnim = -1;
    private int runCwAnim = -1;
    private int walkCcwAnim = -1;
    private int walkCwAnim = -1;
    private int[] randomStandAnimations;
    private int[] randomStandAnimationChances;
    private int totalChance;
    private boolean aBool2787 = true;
    private int anInt2813;
    private int anInt2790;
    private int[] modelRotators;
    private int[][] anIntArrayArray2791;

}
