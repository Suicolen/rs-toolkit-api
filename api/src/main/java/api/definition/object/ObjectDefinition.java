package api.definition.object;

import api.definition.Definition;
import api.definition.NamedDefinition;
import lombok.Data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static api.definition.object.ObjectDefinition.Defaults.*;

@Data
public class ObjectDefinition implements Definition, NamedDefinition {
    private int id = DEFAULT_ID;
    private int decorDisplacement = DEFAULT_DECOR_DISPLACEMENT;
    private boolean isHollow = DEFAULT_IS_HOLLOW;
    private String name = DEFAULT_NAME;
    private int[] modelIds;
    private int[] objectTypes;
    private int[] originalColors;
    private int[] targetColors;
    private int[] originalTextures;
    private int[] targetTextures;
    private int mapAreaId = DEFAULT_MAP_AREA_ID;
    private int sizeX = DEFAULT_SIZE_X;
    private int sizeY = DEFAULT_SIZE_Y;
    private int ambientSoundDistance = DEFAULT_AMBIENT_SOUND_DISTANCE;
    private int ambientSoundRetain = DEFAULT_AMBIENT_SOUND_RETAIN;
    private int[] ambientSoundIds;
    private int offsetX = DEFAULT_OFFSET_X;
    private boolean mergeNormals = DEFAULT_MERGE_NORMALS;
    private int wallOrDoor = DEFAULT_WALL_OR_DOOR;
    private int animationId = DEFAULT_ANIMATION_ID;
    private int varbitID = DEFAULT_VARBIT_ID;
    private int ambient = DEFAULT_AMBIENT;
    private int contrast = DEFAULT_CONTRAST;
    private String[] actions = DEFAULT_ACTIONS.clone();
    private int interactType = DEFAULT_INTERACT_TYPE;
    private int mapSceneID = DEFAULT_MAP_SCENE_ID;
    private int blockingMask = DEFAULT_BLOCKING_MASK;
    private boolean shadow = DEFAULT_SHADOW;
    private int modelSizeX = DEFAULT_MODEL_SIZE_X;
    private int modelSizeHeight = DEFAULT_MODEL_SIZE_HEIGHT;
    private int modelSizeY = DEFAULT_MODEL_SIZE_Y;
    private int objectID = DEFAULT_OBJECT_ID;
    private int offsetHeight = DEFAULT_OFFSET_HEIGHT;
    private int offsetY = DEFAULT_OFFSET_Y;
    private boolean obstructsGround = DEFAULT_OBSTRUCTS_GROUND;
    private int contouredGround = DEFAULT_CONTOURED_GROUND;
    private int supportsItems = DEFAULT_SUPPORTS_ITEMS;
    private int[] configChangeDest;
    private int category = DEFAULT_CATEGORY;
    private boolean isRotated = DEFAULT_IS_ROTATED;
    private int varpID = DEFAULT_VARP_ID;
    private int ambientSoundId = DEFAULT_AMBIENT_SOUND_ID;
    private boolean aBool2111 = DEFAULT_ABOOL_2111;
    private int ambientSoundChangeTicksMin = DEFAULT_AMBIENT_SOUND_CHANGE_TICKS_MIN;
    private int ambientSoundChangeTicksMax = DEFAULT_AMBIENT_SOUND_CHANGE_TICKS_MAX;
    private boolean blocksProjectile = DEFAULT_BLOCKS_PROJECTILE;
    private boolean randomizeAnimStart = DEFAULT_RANDOMIZE_ANIM_START;
    private boolean fixLocAnimAfterLocChange = DEFAULT_FIX_LOC_ANIM_AFTER_LOC_CHANGE; // weird ass name
    private Map<Integer, Object> params = null;

    /**
     * 718
     */

    private int[][] modelIds2D;
    private int[] recolorDestinationIndex;
    private boolean hidden;
    private boolean aBool5703 = true;
    private boolean aBool5702 = true;
    private boolean members;
    private int anInt5654 = -1;
    private boolean adjustMapSceneRotation;
    private boolean hasAnimation;
    private int cursor1Op = -1;
    private int cursor1 = -1;
    private int cursor2Op = -1;
    private int cursor2 = -1;
    private int mapSceneAngleOffset;
    private int ambientSoundVolume = 255;
    private boolean mapSceneFlipVertical;
    private int[] seqWeights;
    private int[] seqIds;
    private int totalSeqWeight;
    private int mapElement = -1;
    private int[] anIntArray5707;
    private int aByte5644;
    private int aByte5642;
    private int aByte5646;
    private int aByte5634;
    private int anInt5682;
    private int anInt5683;
    private int anInt5710;
    private int anInt5704;
    private boolean midiSound;
    private boolean midiSoundEffectsTimed = true;
    private int anInt5684 = 960;
    private int anInt5658;
    private int anInt5708 = 256;
    private int anInt5709 = 256;
    private boolean aBool5699;
    private int anInt5694;
    private boolean aBool5711 = true;

    private int[] aj;
    private int[] unknownArray3;
    private int[] unknownArray4;

    /**
     * 377 and below
     */

    private String examine;

    public ObjectDefinition copy() {
        ObjectDefinition copy = new ObjectDefinition();
        copy.id = id;
        copy.decorDisplacement = decorDisplacement;
        copy.isHollow = isHollow;
        copy.name = name;
        copy.modelIds = modelIds;
        copy.objectTypes = objectTypes;
        copy.originalColors = originalColors;
        copy.targetColors = targetColors;
        copy.originalTextures = originalTextures;
        copy.targetTextures = targetTextures;
        copy.mapAreaId = mapAreaId;
        copy.sizeX = sizeX;
        copy.sizeY = sizeY;
        copy.ambientSoundDistance = ambientSoundDistance;
        copy.ambientSoundRetain = ambientSoundRetain;
        copy.ambientSoundIds = ambientSoundIds;
        copy.offsetX = offsetX;
        copy.mergeNormals = mergeNormals;
        copy.wallOrDoor = wallOrDoor;
        copy.animationId = animationId;
        copy.varbitID = varbitID;
        copy.ambient = ambient;
        copy.contrast = contrast;
        copy.actions = actions;
        copy.interactType = interactType;
        copy.mapSceneID = mapSceneID;
        copy.blockingMask = blockingMask;
        copy.shadow = shadow;
        copy.modelSizeX = modelSizeX;
        copy.modelSizeHeight = modelSizeHeight;
        copy.modelSizeY = modelSizeY;
        copy.objectID = objectID;
        copy.offsetHeight = offsetHeight;
        copy.offsetY = offsetY;
        copy.obstructsGround = obstructsGround;
        copy.contouredGround = contouredGround;
        copy.supportsItems = supportsItems;
        copy.configChangeDest = configChangeDest;
        copy.category = category;
        copy.isRotated = isRotated;
        copy.varpID = varpID;
        copy.ambientSoundId = ambientSoundId;
        copy.aBool2111 = aBool2111;
        copy.ambientSoundChangeTicksMin = ambientSoundChangeTicksMin;
        copy.ambientSoundChangeTicksMax = ambientSoundChangeTicksMax;
        copy.blocksProjectile = blocksProjectile;
        copy.randomizeAnimStart = randomizeAnimStart;
        copy.params = params;
        copy.modelIds2D = modelIds2D;
        copy.recolorDestinationIndex = recolorDestinationIndex;
        copy.hidden = hidden;
        copy.aBool5703 = aBool5703;
        copy.aBool5702 = aBool5702;
        copy.members = members;
        copy.anInt5654 = anInt5654;
        copy.adjustMapSceneRotation = adjustMapSceneRotation;
        copy.hasAnimation = hasAnimation;
        copy.cursor1Op = cursor1Op;
        copy.cursor1 = cursor1;
        copy.cursor2Op = cursor2Op;
        copy.cursor2 = cursor2;
        copy.mapSceneAngleOffset = mapSceneAngleOffset;
        copy.ambientSoundVolume = ambientSoundVolume;
        copy.mapSceneFlipVertical = mapSceneFlipVertical;
        copy.seqWeights = seqWeights;
        copy.seqIds = seqIds;
        copy.totalSeqWeight = totalSeqWeight;
        copy.mapElement = mapElement;
        copy.anIntArray5707 = anIntArray5707;
        copy.aByte5644 = aByte5644;
        copy.aByte5642 = aByte5642;
        copy.aByte5646 = aByte5646;
        copy.aByte5634 = aByte5634;
        copy.anInt5682 = anInt5682;
        copy.anInt5683 = anInt5683;
        copy.anInt5710 = anInt5710;
        copy.anInt5704 = anInt5704;
        copy.midiSound = midiSound;
        copy.midiSoundEffectsTimed = midiSoundEffectsTimed;
        copy.anInt5684 = anInt5684;
        copy.anInt5658 = anInt5658;
        copy.anInt5708 = anInt5708;
        copy.anInt5709 = anInt5709;
        copy.aBool5699 = aBool5699;
        copy.anInt5694 = anInt5694;
        copy.aBool5711 = aBool5711;
        copy.aj = aj;
        copy.unknownArray3 = unknownArray3;
        copy.unknownArray4 = unknownArray4;
        copy.examine = examine;
        copy.fixLocAnimAfterLocChange = fixLocAnimAfterLocChange;
        return copy;
    }

    public ObjectDefinition deepCopy() {
        ObjectDefinition copy = new ObjectDefinition();
        copy.id = id;
        copy.decorDisplacement = decorDisplacement;
        copy.isHollow = isHollow;
        copy.name = name;
        if (modelIds != null) {
            copy.modelIds = Arrays.copyOf(modelIds, modelIds.length);
        }
        if (objectTypes != null) {
            copy.objectTypes = Arrays.copyOf(objectTypes, objectTypes.length);
        }
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
        copy.mapAreaId = mapAreaId;
        copy.sizeX = sizeX;
        copy.sizeY = sizeY;
        copy.ambientSoundDistance = ambientSoundDistance;
        copy.ambientSoundRetain = ambientSoundRetain;
        if (ambientSoundIds != null) {
            copy.ambientSoundIds = Arrays.copyOf(ambientSoundIds, ambientSoundIds.length);
        }
        copy.offsetX = offsetX;
        copy.mergeNormals = mergeNormals;
        copy.wallOrDoor = wallOrDoor;
        copy.animationId = animationId;
        copy.varbitID = varbitID;
        copy.ambient = ambient;
        copy.contrast = contrast;
        if (actions != null) {
            copy.actions = Arrays.copyOf(actions, actions.length);
        }
        copy.interactType = interactType;
        copy.mapSceneID = mapSceneID;
        copy.blockingMask = blockingMask;
        copy.shadow = shadow;
        copy.modelSizeX = modelSizeX;
        copy.modelSizeHeight = modelSizeHeight;
        copy.modelSizeY = modelSizeY;
        copy.objectID = objectID;
        copy.offsetHeight = offsetHeight;
        copy.offsetY = offsetY;
        copy.obstructsGround = obstructsGround;
        copy.contouredGround = contouredGround;
        copy.supportsItems = supportsItems;
        if (configChangeDest != null) {
            copy.configChangeDest = Arrays.copyOf(configChangeDest, configChangeDest.length);
        }
        copy.category = category;
        copy.isRotated = isRotated;
        copy.varpID = varpID;
        copy.ambientSoundId = ambientSoundId;
        copy.aBool2111 = aBool2111;
        copy.ambientSoundChangeTicksMin = ambientSoundChangeTicksMin;
        copy.ambientSoundChangeTicksMax = ambientSoundChangeTicksMax;
        copy.blocksProjectile = blocksProjectile;
        copy.randomizeAnimStart = randomizeAnimStart;
        if (params != null) {
            copy.params = new HashMap<>(params);
        }
        if (modelIds2D != null) {
            copy.modelIds2D = Arrays.copyOf(modelIds2D, modelIds2D.length);
        }
        if (recolorDestinationIndex != null) {
            copy.recolorDestinationIndex = Arrays.copyOf(recolorDestinationIndex, recolorDestinationIndex.length);
        }
        copy.hidden = hidden;
        copy.aBool5703 = aBool5703;
        copy.aBool5702 = aBool5702;
        copy.members = members;
        copy.anInt5654 = anInt5654;
        copy.adjustMapSceneRotation = adjustMapSceneRotation;
        copy.hasAnimation = hasAnimation;
        copy.cursor1Op = cursor1Op;
        copy.cursor1 = cursor1;
        copy.cursor2Op = cursor2Op;
        copy.cursor2 = cursor2;
        copy.mapSceneAngleOffset = mapSceneAngleOffset;
        copy.ambientSoundVolume = ambientSoundVolume;
        copy.mapSceneFlipVertical = mapSceneFlipVertical;
        if (seqWeights != null) {
            copy.seqWeights = Arrays.copyOf(seqWeights, seqWeights.length);
        }
        if (seqIds != null) {
            copy.seqIds = Arrays.copyOf(seqIds, seqIds.length);
        }
        copy.totalSeqWeight = totalSeqWeight;
        copy.mapElement = mapElement;
        if (anIntArray5707 != null) {
            copy.anIntArray5707 = Arrays.copyOf(anIntArray5707, anIntArray5707.length);
        }
        copy.aByte5644 = aByte5644;
        copy.aByte5642 = aByte5642;
        copy.aByte5646 = aByte5646;
        copy.aByte5634 = aByte5634;
        copy.anInt5682 = anInt5682;
        copy.anInt5683 = anInt5683;
        copy.anInt5710 = anInt5710;
        copy.anInt5704 = anInt5704;
        copy.midiSound = midiSound;
        copy.midiSoundEffectsTimed = midiSoundEffectsTimed;
        copy.anInt5684 = anInt5684;
        copy.anInt5658 = anInt5658;
        copy.anInt5708 = anInt5708;
        copy.anInt5709 = anInt5709;
        copy.aBool5699 = aBool5699;
        copy.anInt5694 = anInt5694;
        copy.aBool5711 = aBool5711;
        if (aj != null) {
            copy.aj = Arrays.copyOf(aj, aj.length);
        }
        if (unknownArray3 != null) {
            copy.unknownArray3 = Arrays.copyOf(unknownArray3, unknownArray3.length);
        }
        if (unknownArray4 != null) {
            copy.unknownArray4 = Arrays.copyOf(unknownArray4, unknownArray4.length);
        }
        copy.examine = examine;
        copy.fixLocAnimAfterLocChange = fixLocAnimAfterLocChange;
        return copy;
    }

    public static class Defaults {
        public static int DEFAULT_ID = 0;
        public static int DEFAULT_DECOR_DISPLACEMENT = 16;
        public static boolean DEFAULT_IS_HOLLOW = false;
        public static String DEFAULT_NAME = "null";
        public static int DEFAULT_MAP_AREA_ID = -1;
        public static int DEFAULT_SIZE_X = 1;
        public static int DEFAULT_SIZE_Y = 1;
        public static int DEFAULT_AMBIENT_SOUND_DISTANCE = 0;
        public static int DEFAULT_AMBIENT_SOUND_RETAIN = 0;
        public static int DEFAULT_OFFSET_X = 0;
        public static boolean DEFAULT_MERGE_NORMALS = false;
        public static int DEFAULT_WALL_OR_DOOR = -1;
        public static int DEFAULT_ANIMATION_ID = -1;
        public static int DEFAULT_VARBIT_ID = -1;
        public static int DEFAULT_AMBIENT = 0;
        public static int DEFAULT_CONTRAST = 0;
        public static String[] DEFAULT_ACTIONS = new String[5];
        public static int DEFAULT_INTERACT_TYPE = 2;
        public static int DEFAULT_MAP_SCENE_ID = -1;
        public static int DEFAULT_BLOCKING_MASK = 0;
        public static boolean DEFAULT_SHADOW = true;
        public static int DEFAULT_MODEL_SIZE_X = 128;
        public static int DEFAULT_MODEL_SIZE_HEIGHT = 128;
        public static int DEFAULT_MODEL_SIZE_Y = 128;
        public static int DEFAULT_OBJECT_ID;
        public static int DEFAULT_OFFSET_HEIGHT = 0;
        public static int DEFAULT_OFFSET_Y = 0;
        public static boolean DEFAULT_OBSTRUCTS_GROUND = false;
        public static int DEFAULT_CONTOURED_GROUND = -1;
        public static int DEFAULT_SUPPORTS_ITEMS = -1;
        public static int DEFAULT_CATEGORY = 0;
        public static boolean DEFAULT_IS_ROTATED = false;
        public static int DEFAULT_VARP_ID = -1;
        public static int DEFAULT_AMBIENT_SOUND_ID = -1;
        public static boolean DEFAULT_ABOOL_2111 = false;
        public static int DEFAULT_AMBIENT_SOUND_CHANGE_TICKS_MIN = 0;
        public static int DEFAULT_AMBIENT_SOUND_CHANGE_TICKS_MAX = 0;
        public static boolean DEFAULT_BLOCKS_PROJECTILE = true;
        public static boolean DEFAULT_RANDOMIZE_ANIM_START;
        public static boolean DEFAULT_FIX_LOC_ANIM_AFTER_LOC_CHANGE = false;
    }

    @Override
    public String name() {
        return "null".equalsIgnoreCase(name) ? (name + ": " + id) : name + " - " + id;
    }
}
